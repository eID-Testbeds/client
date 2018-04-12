package com.secunet.ipsmall.test;

import static com.secunet.ipsmall.test.ITestProtocolCallback.SourceComponent.BROWSER;
import static com.secunet.ipsmall.test.ITestProtocolCallback.SourceComponent.BROWSER_SIMULATOR;
import static com.secunet.ipsmall.test.ITestProtocolCallback.SourceComponent.EID_CLIENT;
import static com.secunet.testbedutils.utilities.CommonUtil.containsKeyIgnoreCase;
import static com.secunet.testbedutils.utilities.CommonUtil.getIgnoreCase;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.secunet.ipsmall.AttachedEIDServer;
import com.secunet.ipsmall.CommErrorAddressServer;
import com.secunet.ipsmall.EIDServer;
import com.secunet.ipsmall.EService;
import com.secunet.ipsmall.TCTokenProvider;
import com.secunet.ipsmall.eval.EvaluateResult;
import com.secunet.ipsmall.http.NanoHTTPD.HTTPSession;
import com.secunet.ipsmall.log.IModuleLogger.ConformityResult;
import com.secunet.ipsmall.log.IModuleLogger.LogLevel;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.rmi.RmiHttpResponse;
import com.secunet.ipsmall.test.ITestData.ExpectedTestStepKey;
import com.secunet.ipsmall.test.ITestData.Type;
import com.secunet.ipsmall.tls.BouncyCastleTlsNotificationListener;
import com.secunet.testbedutils.utilities.CommonUtil;
import com.secunet.ipsmall.util.HttpUtils;
import com.secunet.testbedutils.utilities.VariableParser;

/**
 * This TestRunner analyzes a running test, checks reported events against
 * defined expectations and finally sets a test to passed or failed. This is
 * done by registering itself as ITestProtocolCallback to be notified on test
 * events.
 *
 * @author kersten.benjamin
 *
 */
public class TestRunner implements ITestProtocolCallback {

    ITestData testData;
    VariableParser variableParser;
    List<TestResultWarning> resultWarnings = new ArrayList<TestResultWarning>();

    /**
     * Ordered list of steps that is expected to be performed. In execution of
     * testcase differs, TestRunner will set testcase to failed.
     */
    OrderedTestStep[] testStepSequence;

    /**
     * Caches the ordered index of the last testStep successfully processed.
     * E.g. if lastStepInSequencePerformed==3 and the next step being about to
     * be processed is 5, TestRunner will report a fail due to missing step 4.
     */
    int lastStepIndexOfSequencePerformed = -1;

    /**
     * The index of the next step expected to be processed, which is usually
     * lastStepInSequencePerformed+1
     */
    int nextExpectedStepIndex;

    /**
     * The TestStep (step-type) passed to the callback
     */
    TestStep passedStep;

    /**
     * The orderedTestStep equivalent of passedStep. This is taken from
     * testStepSequence if and only if the expected testSequence was
     * successfully asserted.
     */
    OrderedTestStep orderedTestStep;

    // The objects passed to testProtocolCallback. Cached in this vars to
    // shorten method signatures.
    // Cause of that, testProtocolCallback-method is synchronized.
    Object data;
    SourceComponent sourceComponent;
    Object caller;

    /**
     * Once a result was set, TestRunner does not proceed with analysis of
     * subsequent requests. E.g. AA cannot be necessarily stopped and might go
     * on sending some requests after finalStep for current TestCase is already
     * reached.
     */
    boolean resultAlreadySet;

    public TestRunner(ITestData testData) throws IllegalArgumentException, IllegalAccessException {
        this.testData = testData;
        variableParser = new VariableParser(new VariableParameter(testData));
        testStepSequence = generateExpectedTestStepSequence();
        lastStepIndexOfSequencePerformed = -1;
    }

    /**
     * This method is called as testprotocol-callback from anywhere during
     * test-execution. It will then perform an analysis on whether the test is
     * still valid or failed for some reason.
     */
    @Override
    public synchronized void testProtocolCallback(ITestEvent event, Object data, SourceComponent sourceComponent, Object caller) {
        // log
        StringBuilder logEntry = new StringBuilder("TestRunner got ");
        if (event instanceof TestEvent) {
            logEntry.append("TestEvent: ");
        } else if (event instanceof TestStep) {
            logEntry.append("TestStep: ");
        } else {
            logEntry.append("called: ");
        }
        logEntry.append(event.toString());
        logEntry.append(", source:").append(((sourceComponent != null) ? sourceComponent.toString() : "null"));
        logEntry.append(", caller:").append(((caller != null) ? caller.getClass().getSimpleName() : "null"));
        logEntry.append(", data:").append(((data != null) ? data.getClass().getSimpleName() : "null"));
        if (data != null && data instanceof String && event instanceof TestEvent && ((TestEvent) event) == TestEvent.TLS_HANDSHAKE_DONE) {
            logEntry.append("=" + data);
        }

        Logger.TestRunner.logState(logEntry.toString(), LogLevel.Debug);

        this.sourceComponent = sourceComponent;
        this.data = data;
        this.caller = caller;

        try {

            // we might be notified on general TestEvents such as
            // TLS_HANDSHAKE_DONE, which might match
            // a TestStep, but does not necessarily have to
            if (event instanceof TestEvent) {

                TestEvent testEvent = (TestEvent) event;
                if (testEvent == TestEvent.TLS_HANDSHAKE_DONE) {
                    // always set interpretation to null, to clear last one
                    testEvent.setInterpretation(null);
                    
                    // Process results of ICS checks
                    if (caller instanceof BouncyCastleTlsNotificationListener) {
                        if (((BouncyCastleTlsNotificationListener) caller).hasFatalErrors()) {
                            throw new TestAnalysisAbortException(TestResult.FAILED, Reason.UNEXPECTED_TLS_HANDSHAKE,
                                    "Parameters used during TLS Handshake did not match ICS.");
                        }
                    }

                    // A first TLS handshake is done when the eIdClient requests
                    // the TcToken. But we are not
                    // interested in this one.
                    if (!isStepDone(TestStep.TC_TOKEN)) {

                        if ((caller.getClass() == EService.class) || (caller.getClass() == TCTokenProvider.class) || (caller.getClass() == AttachedEIDServer.class)) {
                            // TLS handshake expected, but not interesting
                            return;
                        } else if (caller.getClass() == EIDServer.class) {
                            // no EID TLS handshake expected before TC_TOKEN
                            throw new TestAnalysisAbortException(TestResult.FAILED, Reason.UNEXPECTED_TLS_HANDSHAKE,
                                    "TLS Handshake with EIDService before TC_TOKEN step was processed");
                        }
                        // else: nothing: there might be Redirector handshakes
                        // (TC_TOKEN_REDIRECT) here.

                    } else { // TC_TOKEN step done

                        // Special case:
                        // TLS handshake with eService initiated by the browser when no browser simulator is used
                        // => ignore it, if REFRESH_ADDRESS was already done
                        if (testData.getTestType() != null && testData.getTestType() == Type.BROWSER && isStepDone(TestStep.REFRESH_ADDRESS)) {
                            // check
                            if ((caller.getClass() == EService.class) || (caller.getClass() == TCTokenProvider.class)) {
                                return;
                            } else if (caller.getClass() == EIDServer.class) {
                                // no EID TLS handshake expected after REFRESH_ADDRESS
                                throw new TestAnalysisAbortException(TestResult.FAILED, Reason.UNEXPECTED_TLS_HANDSHAKE,
                                        "TLS Handshake with EIDService after REFRESH_ADDRESS step was processed");
                            }
                        }
                        // afterwards, TLS handshake is also done for
                        // - EIDService just before PAOS (Step SERVER_ADDRESS)
                        // - EService just before browser-redirect (Step
                        // REFRESH_ADDRESS)
                        // and we ARE interested in these events as they
                        // represent TestSteps, so
                        // cast these events to TestSteps so that they are
                        // processed below and their
                        // sequence is analyzed (e.g. SERVER_ADDRESS before PAOS
                        // etc.).
                        // Note: this is casted by intent (instead of calling
                        // this method again with
                        // different event) to prevent another caller being
                        // scheduled in between.
                        if (caller.getClass() == EIDServer.class) {
                            testEvent.setInterpretation(TestStep.SERVER_ADDRESS.name());
                            event = TestStep.SERVER_ADDRESS;
                        } else if ((caller.getClass() == EService.class) || (caller.getClass() == TCTokenProvider.class)) {
                            testEvent.setInterpretation(TestStep.REFRESH_ADDRESS.name());
                            event = TestStep.REFRESH_ADDRESS;
                        }
                        // else: nothing: might be redirector handshake
                        // (REFRESH_ADDRESS_REDIRECT) with no need for cast.
                    }
                }
            }

            // we are interested in TestSteps here (i.e. no TestResults).
            // TestSteps are
            // then further analyzed (compared to expected steps, sequence of
            // steps, etc.)
            // Do NOT else-if here!
            if (event instanceof TestStep) {

                // ignore further callback notifications if result was already
                // set. E.g. AA might want to proceed
                // with some calls, even if our testcase already succeeded
                if (!resultAlreadySet) {

                    passedStep = (TestStep) event;

                    // when using legacy activation, a call of REFRESH_ADDRESS
                    // may occur independently, that means we have to skip the sequence
                    // check
                    if (testData.isLegacyActivation() && TestStep.REFRESH_ADDRESS == passedStep) {
                        return;
                    }
                    
                    // compare the passed step to our expected sequence
                    // (do not increment++, but add +1)
                    nextExpectedStepIndex = lastStepIndexOfSequencePerformed + 1;

                    assertTestStepSequence(passedStep);
                    // if assertion of sequence was successful, get
                    // orderedTestStep from passed step (which is
                    // essentially the TestStep plus sequence indices
                    orderedTestStep = testStepSequence[nextExpectedStepIndex];
                    
                    if (testData.getTestType() != null && testData.getTestType() == Type.BROWSER) {
                        if (nextExpectedStepIndex + 1 < testStepSequence.length) {
                            if (testStepSequence[nextExpectedStepIndex + 1].getTestStep() == TestStep.REDIRECT_BROWSER ) {
                                testData.setSkipNextICSCheck(true);
                            }
                        }
                    }
                    
                    // now do test-specific analysis (after general sequence has
                    // been passed):
                    switch (passedStep) {

                        case ACTIVATE:
                            onStepActivate();
                            break;

                        case TC_TOKEN_REDIRECT:
                            onStepTcTokenRedirect();
                            break;

                        case TC_TOKEN:
                            onStepTcToken();
                            break;

                        case SERVER_ADDRESS:
                            onStepServerAddress();
                            break;

                        case START_PAOS:
                            onStepStartPaos();
                            break;

                        case INITIALIZE_FRAMEWORK:
                            onStepInitializeFramework();
                            break;

                        case EAC1:
                            onStepEac1();
                            break;

                        case EAC2:
                            onStepEac2();
                            break;

                        case EAC3:
                            onStepEac3();
                            break;

                        case TRANSMIT:
                            onStepTransmit();
                            break;

                        case START_PAOS_RESPONSE:
                            onStepStartPaosResponse();
                            break;

                        case REFRESH_ADDRESS_REDIRECT:
                            onStepRefreshAddressRedirect();
                            break;

                        case REFRESH_ADDRESS:
                            onStepRefreshAddress();
                            break;

                        case REDIRECT_BROWSER:
                            if (testData.getTestType() != null && testData.getTestType() == Type.BROWSER && ((caller.getClass() == EService.class) || (caller.getClass() == AttachedEIDServer.class) || (caller.getClass() == TCTokenProvider.class) || (caller.getClass() == CommErrorAddressServer.class))) {
                                onStepRedirectBrowserViaHTTPServer();
                            } else {
                                onStepRedirectBrowser();
                            }
                            break;

                        case BROWSER_CONTENT:
                            onStepBrowserContent();
                            break;

                        default:
                            break;
                    }

                    // check if data is an EvaluateResult and check for errors
                    // and warnings
                    if (data instanceof EvaluateResult) {
                        EvaluateResult evalResult = (EvaluateResult) data;

                        if (evalResult.isError()) {
                            if (testData.isFailOnXMLEvaluationError() || evalResult.isCriticalError()) {
                                throw new TestAnalysisAbortException(TestResult.FAILED, Reason.XML_VALIDATION_FAILED, evalResult,
                                        "Errors occured during the validation of the xml message " + passedStep.toString() + ":");
                            } else {
                                System.out.println("TODO: Activate hard error on xml message validation fail -----");
                                Logger.TestRunner.logConformity(ConformityResult.failed, "Errors occured during the validation of the xml message "
                                        + passedStep.toString() + ":" + System.lineSeparator() + getFormattedEvaluateResultMessage(evalResult), LogLevel.Error);
                            }
                        }

                        // check for warnings
                        if (evalResult.getWarningsReference() != null && !evalResult.getWarningsReference().isEmpty() && evalResult.isValid()) {
                            Logger.TestRunner.logConformity(ConformityResult.failed, "During the validation of the xml message " + passedStep.toString()
                                    + " warning(s) occured:" + System.lineSeparator() + getFormattedEvaluateResultMessage(evalResult), LogLevel.Warn);

                        }
                    }

                    // when this LOC is reached, testStep was successful
                    lastStepIndexOfSequencePerformed++;

                    // finally check if testcase is finished successfully.
                    // This is true if current step is final step
                    if (lastStepIndexOfSequencePerformed == testStepSequence.length - 1) {
                        // TODO are exceptional flows also included?
                        // even though success, we throw an exception (not a
                        // good pattern, but much easier to handle here:
                        // automatic logging, detail logging, set of bool flag,
                        // etc.)
                        if (testData.isResultIndeterminable()) {
                            throw new TestAnalysisAbortException(TestResult.UNDETERMINED, Reason.MANUAL_CHECK_NEEDED, testData.getResultIndeterminableReason());
                        } else if (resultWarnings.size() > 0) {
                            throw new TestAnalysisAbortException(TestResult.UNDETERMINED, Reason.MANUAL_CHECK_NEEDED, resultWarnings.size() + " test result warning(s) occured.");
                        }

                        throw new TestAnalysisAbortException(TestResult.PASSED, Reason.FINAL_RESULT_STEP_REACHED, "Last step reached: " + passedStep.toString());
                    }

                }

            } // EO resultSet
        } // EO event == TestStep
        catch (TestAnalysisAbortException e) {

            logAbortException(e);
            if (e.getResult() != null) {
                resultAlreadySet = true;
                testData.sendMessageToCallbacks(e.getResult(), null, null, this);
            }

        }
    }

    /**
     * Creates a formatted string over more than one line, that includes
     * differend information of the result
     *
     * @param evalResult must not be null
     * @param step must not be null
     * @return string over more than one line
     */
    protected String getFormattedEvaluateResultMessage(EvaluateResult evalResult) {
        return getFormattedEvaluateResultMessage(evalResult, 0);
    }

    protected String getFormattedEvaluateResultMessage(EvaluateResult evalResult, final int child) {
        String result = "";

        if (child == 0) {
            result += "-------- Message Evaluation:--------" + System.lineSeparator();
        }
        StringBuilder str = new StringBuilder("");
        for (int i = 0; i < child; i++) {
            str.append(" ");
        }
        String prefix = str.toString();
        // if (evalResult.isError()) {
        result += prefix + "Error type: " + evalResult.getType().toString() + System.lineSeparator();
        result += prefix + "Error description: " + evalResult.getDescription() + System.lineSeparator();
        result += prefix + "Expected: " + evalResult.getExpectedValue() + System.lineSeparator();
        result += prefix + "but found: " + evalResult.getMessageValue() + System.lineSeparator();
        // } else {
        // Log.logError("No top level error");
        // }
        if (evalResult.getWarningsReference() != null && !evalResult.getWarningsReference().isEmpty()) {
            result += prefix + "Childwarnings: + System.lineSeparator()";
            for (EvaluateResult warning : evalResult.getWarningsReference()) {
                result += getFormattedEvaluateResultMessage(warning, (child + 1));
            }
        }

        if (child == 0) {
            result += "-------- Message Evaluation END --------";
        }

        return result;
    }

    /**
     * Checks whether the passed step has already been successfully processed.
     * Return true for the first step found matching the passed type.
     *
     * @param step
     * @return
     */
    private boolean isStepDone(TestStep step) {
        return isStepDone(step, 0);
    }

    /**
     * Checks whether the passed step has already been successfully processed.
     * Pass a typeIndex different from zero to find the specified number of this
     * testtype (if there are multiple, e.g. for redirects). See OrderedTestStep
     * sequence.
     *
     * @param step
     * @return
     */
    private boolean isStepDone(TestStep step, int typeIndex) {

        if (lastStepIndexOfSequencePerformed < 0) {
            return false;
        } else {
            OrderedTestStep passed;
            for (int i = 0; i <= lastStepIndexOfSequencePerformed; i++) {
                passed = testStepSequence[i];
                if (passed.getTestStep() == step && passed.getNumOfStepForType() == typeIndex) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Compares the reported TestStep with the expected sequence of steps.
     * Throws a TestAnalysisAbortException to set TestCase to failed if the
     * reported TestStep does not match the expected sequence or is already
     * beyond the expected final step.
     *
     * @param step
     * @param data
     * @param source
     * @throws TestAnalysisAbortException
     */
    public void assertTestStepSequence(TestStep step) throws TestAnalysisAbortException {

        // get the next OrderedTestStep (lastStep + 1) that is expected to be
        // processed
        if (nextExpectedStepIndex > testStepSequence.length - 1) {
            throw new TestAnalysisAbortException(TestResult.FAILED, Reason.SEQUENCE_EXCEEDED, "TestStep about to be processed: " + step.toString()
                    + " (number: " + nextExpectedStepIndex + ")");
        }

        OrderedTestStep nextOrderedStep = testStepSequence[nextExpectedStepIndex];
        TestStep nextStep = nextOrderedStep.getTestStep();

        if (nextStep != step) {
            throw new TestAnalysisAbortException(TestResult.FAILED, Reason.SEQUENCE_FAILED, "Expected TestStep: " + nextStep.toString() + ", Was: "
                    + step.toString());
        }

    }

    public void onStepActivate() throws TestAnalysisAbortException {

    }

    public void onStepTcTokenRedirect() throws TestAnalysisAbortException {
        // SAML request redirects
        // Commented out to test redirectors without browser simulator
        // assertSourceComponent(sourceComponent, EID_CLIENT);
    }

    public void onStepTcToken() throws TestAnalysisAbortException {
        // Commented out to test redirectors without browser simulator
        // assertSourceComponent(sourceComponent, EID_CLIENT);
    }

    public void onStepServerAddress() throws TestAnalysisAbortException {
        // We do not have client information here as not available on SSL layer.
    }

    public void onStepStartPaos() throws TestAnalysisAbortException {
        assertSourceComponent(sourceComponent, EID_CLIENT);
    }

    public void onStepInitializeFramework() throws TestAnalysisAbortException {
        assertSourceComponent(sourceComponent, EID_CLIENT);
    }

    public void onStepEac1() throws TestAnalysisAbortException {
        assertSourceComponent(sourceComponent, EID_CLIENT);
    }

    public void onStepEac2() throws TestAnalysisAbortException {
        assertSourceComponent(sourceComponent, EID_CLIENT);
    }

    public void onStepEac3() throws TestAnalysisAbortException {
        assertSourceComponent(sourceComponent, EID_CLIENT);
    }

    public void onStepTransmit() throws TestAnalysisAbortException {
        assertSourceComponent(sourceComponent, EID_CLIENT);
    }

    public void onStepStartPaosResponse() throws TestAnalysisAbortException {
        assertSourceComponent(sourceComponent, EID_CLIENT);
    }

    public void onStepRefreshAddressRedirect() throws TestAnalysisAbortException {
        // SAML response redirects

        // HTTPSession httpReq = (HTTPSession) data;
    }

    public void onStepRefreshAddress() throws TestAnalysisAbortException {
        // We do not have client information here as not available on SSL layer.
        // No need to verify anything. Http data will be empty anyway (SSL
        // handshake done for verification only).
    }

    public void onStepRedirectBrowser() throws TestAnalysisAbortException {

        // assert that this was requested by a browser(-sim)
        assertSourceComponent(sourceComponent, BROWSER_SIMULATOR, BROWSER);

        // cast passed data to http-data
        if (!(data instanceof RmiHttpResponse)) {
            // should never occur => debug purposes only
            throw new TestAnalysisAbortException(TestResult.FAILED, Reason.INCOMPATIBLE_DATA_TYPES, "Assertions for step "
                    + TestStep.REDIRECT_BROWSER.toString() + " need to pass data of type " + RmiHttpResponse.class.getName());
        }

        RmiHttpResponse response = (RmiHttpResponse) data;
        String redirectUrl = HttpUtils.getRedirectUrl(response);
        int statusCode = response.statusCode;

        // general assertions on contained urlParams
        assertGeneralUrlParamRules(redirectUrl);

        // specific url assertions as defined in expected results
        if (orderedTestStep.getExpectedStatusCodes() != null || orderedTestStep.getExpectedUrl() != null || orderedTestStep.getExpectedUrlPath() != null
                || orderedTestStep.getExpectedUrlParams() != null) {
            // We need to verify at least one of the params above. This
            // required data to be of type RmiHttpResponse which is true
            // for BrowserSimulator responses. If params above are not
            // set, we do not necessarily need to have RmiHttpResponses
            // (e.g. manual browser tests).
            if (!(data instanceof RmiHttpResponse)) {
                throw new TestAnalysisAbortException(TestResult.FAILED, Reason.INCOMPATIBLE_DATA_TYPES, "Assertions for step "
                        + TestStep.REDIRECT_BROWSER.toString() + " need to pass data of type " + RmiHttpResponse.class.getName()
                        + " if expectations of http params (url, urlParams, statusCodes, etc) are defined.");
            }

            if (orderedTestStep.getExpectedStatusCodes() != null) {
                assertStatusCodes(orderedTestStep.getExpectedStatusCodes(), statusCode);
            }
            if (orderedTestStep.getExpectedUrl() != null) {
                assertStrings(ExpectedTestStepKey.URL, convertPlaceholders(orderedTestStep.getExpectedUrl()), redirectUrl);
            }
            if (orderedTestStep.getExpectedUrlPath() != null) {
                assertExpectedUrlPath(convertPlaceholders(orderedTestStep.getExpectedUrlPath()), redirectUrl, false);
            }
            if (orderedTestStep.getExpectedUrlParams() != null) {
                assertExpectedUrlParams(orderedTestStep.getExpectedUrlParams(), redirectUrl);
            }
        }
    }

    public void onStepRedirectBrowserViaHTTPServer() throws TestAnalysisAbortException {

        // assert that this was requested by a browser
        assertSourceComponent(sourceComponent, BROWSER);

        // cast passed data to http-data
        if (!(data instanceof HTTPSession)) {
            // should never occur => debug purposes only
            throw new TestAnalysisAbortException(TestResult.FAILED, Reason.INCOMPATIBLE_DATA_TYPES, "Assertions for step "
                    + TestStep.REDIRECT_BROWSER.toString() + " called via eService need to pass data of type " + HTTPSession.class.getName());
        }
        
        HTTPSession response = (HTTPSession) data;
        String redirectUrl = HttpUtils.getRedirectUrl(response);
        
        // general assertions on contained urlParams
        assertGeneralUrlParamRules(redirectUrl);
        
        // specific url assertions as defined in expected results
        if (orderedTestStep.getExpectedUrl() != null || orderedTestStep.getExpectedUrlPath() != null
                || orderedTestStep.getExpectedUrlParams() != null) {
            // We need to verify at least one of the params above. This
            // required data to be of type RmiHttpResponse which is true
            // for BrowserSimulator responses. If params above are not
            // set, we do not necessarily need to have RmiHttpResponses
            // (e.g. manual browser tests).
            if (!(data instanceof HTTPSession)) {
                throw new TestAnalysisAbortException(TestResult.FAILED, Reason.INCOMPATIBLE_DATA_TYPES, "Assertions for step "
                        + TestStep.REDIRECT_BROWSER.toString() + " need to pass data of type " + HTTPSession.class.getName()
                        + " if expectations of http params (url, urlParams, statusCodes, etc) are defined.");
            }

            if (orderedTestStep.getExpectedUrl() != null) {
                assertStrings(ExpectedTestStepKey.URL, convertPlaceholders(orderedTestStep.getExpectedUrl()), redirectUrl);
            }
            if (orderedTestStep.getExpectedUrlPath() != null) {
                assertExpectedUrlPath(convertPlaceholders(orderedTestStep.getExpectedUrlPath()), redirectUrl, false);
            }
            if (orderedTestStep.getExpectedUrlParams() != null) {
                assertExpectedUrlParams(orderedTestStep.getExpectedUrlParams(), redirectUrl);
            }
            
            // ignore status code in browser mode
            /*if (orderedTestStep.getExpectedStatusCodes() != null) {
                throw new TestAnalysisAbortException(TestResult.UNDETERMINED, Reason.MANUAL_CHECK_NEEDED, "Status code not checkable in browser mode.");
            }*/
        }
    }

    private String convertPlaceholders(String s) throws TestAnalysisAbortException {
        try {
            return variableParser.format(s);
        } catch (Exception e) {
            throw new TestAnalysisAbortException(TestResult.FAILED, Reason.ASSERTION_FAILED, "Could parse: " + s);
        }
    }

    public void onStepBrowserContent() throws TestAnalysisAbortException {
        assertSourceComponent(sourceComponent, BROWSER_SIMULATOR, BROWSER);

        if (orderedTestStep.getExpectedUrl() != null) {
            // TODO maybe we want to warn operator that we will not assert
            // specific params for specific tests?
            // E.g. browser content does not have url+params (request or
            // redirect has)
        }

        if (orderedTestStep.getExpectedStatusCodes() != null) {
            // We need to verify at least one of the params above. This
            // required data to be of type RmiHttpResponse which is true
            // for BrowserSimulator responses. If params above are not
            // set, we do not necessarily need to have RmiHttpResponses
            // (e.g. manual browser tests).
            if (!(data instanceof RmiHttpResponse)) {
                throw new TestAnalysisAbortException(TestResult.FAILED, Reason.INCOMPATIBLE_DATA_TYPES, "Assertions for step "
                        + TestStep.BROWSER_CONTENT.toString() + " need to pass data of type " + RmiHttpResponse.class.getName()
                        + " if expectations of http params (url, urlParams, statusCodes, etc) are defined.");
            }

            RmiHttpResponse response = (RmiHttpResponse) data;

            if (orderedTestStep.getExpectedStatusCodes() != null) {
                assertStatusCodes(orderedTestStep.getExpectedStatusCodes(), response.statusCode);
            }

        }

    }

    /**
     * Throws exception if passed sourceComponent does not match expected
     * component
     *
     * @param sourceComponent
     * @param expected
     */
    private void assertSourceComponent(SourceComponent sourceComponent, SourceComponent... expected) throws TestAnalysisAbortException {
        // calling sourceComponent must match any of the expected components
        StringBuilder expectedBldr = new StringBuilder();
        for (SourceComponent validComponent : expected) {
            if (sourceComponent == validComponent) {
                // assertion passed
                return;
            }
            expectedBldr.append(validComponent).append("|");
        }
        // System.out.println("<TODO> " + "WARNING: " + "Invalid Source Component: expected " + expectedBldr.toString() + ",  was: " + sourceComponent +
        // "-----------------");
        Logger.TestRunner.logState("Invalid Source Component: expected " + expectedBldr.toString() + ",  was: " + sourceComponent, LogLevel.Error);
        // if not passed until here, assertion failed
        throw new TestAnalysisAbortException(TestResult.FAILED, Reason.INVALID_SOURCE_COMPONENT, "Invalid Source Component: expected " + expected + ",  was: "
                + sourceComponent);
    }

    /**
     * Can be thrown at any time during test analysis to abort current analysis
     * (and e.g. set a result instead).
     *
     * @author kersten.benjamin
     *
     */
    public class TestAnalysisAbortException extends Exception {

        private static final long serialVersionUID = 1L;

        public TestAnalysisAbortException(TestResult result, Reason reason, String... reasonDetails) {
            this(result, reason, null, reasonDetails);
        }

        public TestAnalysisAbortException(TestResult result, Reason reason, EvaluateResult evalResult, String... reasonDetails) {
            super();
            this.result = result;
            this.reason = reason;
            this.reasonDetails = reasonDetails;
            this.evalResult = evalResult;
        }

        private final TestResult result;
        private final Reason reason;
        private final String[] reasonDetails;
        private final EvaluateResult evalResult;

        public TestResult getResult() {
            return result;
        }

        public Reason getReason() {
            return reason;
        }

        public String[] getReasonDetails() {
            return reasonDetails;
        }

        public EvaluateResult getEvaluateResult() {
            return evalResult;
        }
    }

    /**
     * Reasons why TestRunner should abort an analysis and set a result instead.
     * Only used internally by TestRunner, used for logging.
     *
     * @author kersten.benjamin
     *
     */
    public enum Reason {

        /**
         * Processed TestSteps did not match expected sequence, e.g. Step 5
         * processed after Step 3, but Step 4 was missing.
         */
        SEQUENCE_FAILED(
                "Sequence of TestSteps failed."),
        /**
         * E.g. only 10 Steps defined, but Step 11 is about to be processed
         */
        SEQUENCE_EXCEEDED(
                "Sequence exceeded, i.e. current TestStep is not defined for this TestCase."),
        /**
         * EvaluateResult of PAOS-XML-validation failed (result != OK)
         */
        XML_VALIDATION_FAILED(
                "Xml Validation failed (EvaluateResult)"),
        ASSERTION_FAILED(
                "Assertion of expected results failed"),
        /**
         * Certain system parts must only be called by certain components, e.g.
         * PAOS steps are requested by eIdClient/AA, but never by browser
         */
        INVALID_SOURCE_COMPONENT(
                "Invalid caller: parts of the system were accessed by components which should not call these parts."),
        UNEXPECTED_TLS_HANDSHAKE(
                "Unexpected TLS handshake was performed."),
        INCOMPATIBLE_DATA_TYPES(
                "TestRunner cannot assert expectations due to incompatible data types."),
        /**
         * Success case
         */
        FINAL_RESULT_STEP_REACHED(
                "FinalResultStep reached."),
        /**
         * Undetermined
         */
        MANUAL_CHECK_NEEDED(
                "Automatic evaluation not possible: manual check needed.");

        private String reasonText;

        private Reason(String reasonText) {
            this.reasonText = reasonText;
        }

        public String getReasonText() {
            return reasonText;
        }

    }

    private void logAbortException(TestAnalysisAbortException e) {
        String resultMsg = "";
        ConformityResult result = ConformityResult.failed;
        LogLevel logLevel = LogLevel.Error;
        if (e.getResult() != null && TestResult.PASSED == e.getResult()) {
            result = ConformityResult.passed;
            logLevel = LogLevel.Info;
        } else if (e.getResult() != null && TestResult.UNDETERMINED == e.getResult()) {
            result = ConformityResult.undetermined;
            logLevel = LogLevel.Warn;
        }

        if (e.getResult() != null) {
            resultMsg += "Setting TestCase to " + e.getReason().toString() + System.lineSeparator();
        }

        if (e.getReason() != null) {
            resultMsg += "Reason: " + e.getReason().getReasonText() + System.lineSeparator();
        }

        if (e.getReasonDetails() != null) {
            for (String reasonDetail : e.getReasonDetails()) {
                resultMsg += reasonDetail + System.lineSeparator();
            }
        }

        if (e.getEvaluateResult() != null && e.getEvaluateResult().isError()) {
            resultMsg += "Error Result of the validation of the message: " + passedStep + System.lineSeparator();
            resultMsg += getFormattedEvaluateResultMessage(e.getEvaluateResult());
        } else if (e.getEvaluateResult() != null && e.getEvaluateResult().isValid()) {
            resultMsg += "Result of the validation of the message: " + passedStep + System.lineSeparator();
            resultMsg += "Okay" + (e.getEvaluateResult().getDescription() != null ? e.getEvaluateResult().getDescription() : "") + System.lineSeparator();
        } else if (e.getEvaluateResult() != null) {
            resultMsg += "Indeterminable Result of the validation of the message: " + passedStep + System.lineSeparator();
            resultMsg += getFormattedEvaluateResultMessage(e.getEvaluateResult());
        }

        List<String> additional = getAdditionalLogInfo(e);
        if (additional != null && !additional.isEmpty()) {
            for (String s : additional) {
                resultMsg += s + System.lineSeparator();
            }
        }

        Logger.TestRunner.logConformity(result, resultMsg, logLevel);
    }

    /**
     * This method MAY return additional log data for the passed step if this
     * makes sense. E.g. for SEQUENCE_FAILED, this method might want to list the
     * steps already processed and try to detect missing steps. Other TestStep
     * types maybe do not return additional log data.
     *
     * @param orderedStep
     * @return
     */
    private List<String> getAdditionalLogInfo(TestAnalysisAbortException e) {

        List<String> data = new ArrayList<>();

        switch (e.getReason()) {
            case SEQUENCE_FAILED:
                data.add("TestSteps successfully processed:");
                if (lastStepIndexOfSequencePerformed < 0) {
                    data.add("none");
                } else {
                    for (int i = 0; i <= lastStepIndexOfSequencePerformed; i++) {
                        data.add("  - " + testStepSequence[i].getTestStep().toString());
                    }
                }
                // improvement : maybe detect missing steps too?
                break;

            default:
                break;
        }

        return data;
    }

    private OrderedTestStep[] generateExpectedTestStepSequence() {

        ArrayList<OrderedTestStep> sequence = new ArrayList<>();
        int numOfStepInSequence = 0;

        // if finalDefaultFlowStep is null (i.e. defined with
        // 'expect.finalDefaultFlowStep='), then we do not
        // expect a single step from the default flow (e.g.
        // error even before tc_token (sample: A2_15)).
        if (!isExpectedFinalDefaultFlowStep(null, 0)) {
            // else: we begin with a representation of the default-flow:
            // all defined enum values...
            TestStep[] all = TestStep.values();
            // ... are traversed and added so many times as defined
            // for current testcase (up until finalDefaultFlowTest), which is...
            defaultFlowLoop:
            for (TestStep testStep : all) {
                if (testStep == TestStep.ACTIVATE) {
                    // ACTIVATE is skipped as it is implicit, cannot be detected
                    // directly
                    continue;
                } else if (!testStep.isPartOfSuccessFlow()) {
                    // testSteps not being part of default success test flow
                    // (e.g.
                    // communicationErrorAddress are skipped too (for now). They
                    // may
                    // be part of an exceptional test flow)
                    continue;
                } else if (testStep == TestStep.SERVER_ADDRESS && testData.isEIDServiceAttached()) {
                    // SERVER_ADDRESS is skipped for attached eID server, should never happen
                    continue;
                } else {
                    // else: each test-step is usually added once, but there are
                    // some which might occur 0..n or 0..1, see Jira
                    // ECARDCONF-144
                    for (int numOfStepForType = 0; numOfStepForType < getNumberOfOccurrencesForTestStep(testStep); numOfStepForType++) {
                        // Added test steps are numbered for later comparison.
                        // numOfStepInSequence is total numbering (from 0
                        // (TcToken...) to n (final refresh redirect).
                        // numOfStepInType additionally stores number for this
                        // particular type, i.e. if e.g.
                        // TcTokenRedirect has 3 redirects, they are also
                        // numbered
                        // 0,1,2.
                        OrderedTestStep orderedTestStep = new OrderedTestStep(testStep, numOfStepInSequence++, numOfStepForType);
                        sequence.add(orderedTestStep);
                        if (isExpectedFinalDefaultFlowStep(testStep, numOfStepForType)) {
                            orderedTestStep.setFinalDefaultFlowStep(true);
                            setOrderedTestStepProperties(orderedTestStep, testData.getExpectedFinalDefaultFlowStep());
                            break defaultFlowLoop;
                        }
                    }
                }
            }
        }

        // AFTER the final step of the default flow, we add (optional)
        // additional steps too (if present):
        List<HashMap<ITestData.ExpectedTestStepKey, String>> additionalList = testData.getExpectedAdditionalSteps();
        if (additionalList != null) {

            int numOfStepForType = 0;
            TestStep lastStep = null;

            for (HashMap<ITestData.ExpectedTestStepKey, String> map : additionalList) {
                TestStep testStep = TestStep.valueOf(map.get(ITestData.ExpectedTestStepKey.STEP));
                
                // special handling of step BROWSER_CONTENT in browser test mode
                if(testStep != null && testStep == TestStep.BROWSER_CONTENT && testData.getTestType() != null && testData.getTestType() == Type.BROWSER) {
                	String reason = (testData.isResultIndeterminable() ? ("" + testData.getResultIndeterminableReason() + "\n\n") : "");
                	reason += "Test Step BROWSER_CONTENT was skipped due to testing in browser test mode and must be checked manually.";
                	testData.setResultIndeterminableReason(reason);
                	continue;
                }
                
                //TODO this code is very suspicious!
                // 'lastStep' is never written => always null
                if (testStep == lastStep) {
                    numOfStepForType++;
                } else {
                    numOfStepForType = 0;
                }
                OrderedTestStep orderedTestStep = new OrderedTestStep(testStep, numOfStepInSequence++, numOfStepForType);
                setOrderedTestStepProperties(orderedTestStep, map);
                sequence.add(orderedTestStep);

            }

        }

        OrderedTestStep[] result = new OrderedTestStep[sequence.size()];
        return sequence.toArray(result);
    }

    private void setOrderedTestStepProperties(OrderedTestStep orderedTestStep, HashMap<ITestData.ExpectedTestStepKey, String> map) {
        orderedTestStep.setExpectedUrl(map.get(ITestData.ExpectedTestStepKey.URL));
        orderedTestStep.setExpectedUrlPath(map.get(ITestData.ExpectedTestStepKey.URL_PATH));
        orderedTestStep.setExpectedUrlParams(CommonUtil.commaSeparatedStringToStringArray(map.get(ITestData.ExpectedTestStepKey.URL_PARAMS)));
        orderedTestStep.setExpectedStatusCodes(CommonUtil.commaSeparatedStringToIntArray(map.get(ITestData.ExpectedTestStepKey.STATUS_CODES)));

    }

    /**
     * Return how many times the passed testStep is contained in current
     * testcase. This will be once for most steps, but there are some 0..n and
     * 0..1 steps.
     *
     * @param testStep
     * @return
     */
    private int getNumberOfOccurrencesForTestStep(TestStep testStep) {
        if (testStep == TestStep.TC_TOKEN_REDIRECT) {
            // 0..n
            return testData.getRedirectorsInfoTCToken() == null ? 0 : testData.getRedirectorsInfoTCToken().size();
        } else if (testStep == TestStep.INITIALIZE_FRAMEWORK) {
            // 0..1
            return (testData.getECardInitializeFrameworkTemplate() == null || testData.getECardInitializeFrameworkTemplate().trim().isEmpty()) ? 0 : 1;
        } else if (testStep == TestStep.EAC3) {
            // 0..1
            return (testData.getECardDIDAuthenticate3Template() == null || testData.getECardDIDAuthenticate3Template().trim().isEmpty()) ? 0 : 1;
        } else if (testStep == TestStep.TRANSMIT) {
            // 0..n
            return testData.getECardStepTransmitTemplates() == null ? 0 : testData.getECardStepTransmitTemplates().size();
        } else if (testStep == TestStep.REFRESH_ADDRESS_REDIRECT) {
            // 0..n
            return testData.getRedirectorsInfoRefreshAddress() == null ? 0 : testData.getRedirectorsInfoRefreshAddress().size();
        } else {
            return 1;
        }
    }

    /**
     * Checks whether passed test is the last expected step of the default flow
     * as defined via config.properties setting 'expect.finalDefaultFlowStep'
     *
     * @param testStep
     * @param numOfStepForType
     * @return
     */
    private boolean isExpectedFinalDefaultFlowStep(TestStep testStep, int numOfStepForType) {

        // null-check first (which is allowed: 'finalDefaultFlowStep=' to define
        // 'none')
        if ((testData.getExpectedFinalDefaultFlowStep().get(ITestData.ExpectedTestStepKey.STEP) == null || testData.getExpectedFinalDefaultFlowStep()
                .get(ITestData.ExpectedTestStepKey.STEP).trim().isEmpty())
                && testStep == null) {
            return true;
        }

        TestStep expectedFinalDefaultFlowStep = TestStep.valueOf(testData.getExpectedFinalDefaultFlowStep().get(ITestData.ExpectedTestStepKey.STEP)
                .toUpperCase());
        // if we are interested in checking numOfStepForType too (e.g. for
        // multi-steps like redirectors), we could
        // do this here (param would have to be passed as additional param
        // expected.additionalStep.numOfStepForType)
        if (testStep == expectedFinalDefaultFlowStep) {
            return true;
        }
        return false;
    }

    private void assertStrings(ITestData.ExpectedTestStepKey key, String expected, String current) throws TestAnalysisAbortException {
        TestAnalysisAbortException e = new TestAnalysisAbortException(TestResult.FAILED, Reason.ASSERTION_FAILED, "Assertion of: " + key.toString()
                + " failed: Expected " + expected + ", was: " + current);
        if (expected == null) {
            if (current == null) {
                // both null, ok
                return;
            } else {
                // expected null, but current not
                throw e;
            }
        } else {
            // expected!=null
            if (!(expected.equals(current))) {
                // not same
                throw e;
            }
        }
    }

    /**
     * Asserts the urlParams as expected for this TestCase by comparing them to
     * the params currently present. This is only called if at least one
     * urlParam was defined to be expected. For general url-param-checks, see
     * assertGeneralUrlParamRules instead.
     *
     * @param expectedList
     * @param current
     * @throws TestAnalysisAbortException
     */
    private void assertExpectedUrlParams(String[] expectedList, String current) throws TestAnalysisAbortException {

        // Check all expected URL params until finding a matching one
        for (int i = 0; i < expectedList.length; i++) {

            try {
                assertExpectedUrlParams(expectedList[i], current); // if no exception is thrown, result was valid

                if (i > 0) { // if this was not first expected value, store result warning 

                }

                return;
            } catch (TestAnalysisAbortException e) {
                continue; // do nothing, just continue
            }
        }

        // no match found
        resultWarnings.add(new TestResultWarning("Expected URL params '" + CommonUtil.arrayToCommaSeparatedString(expectedList) + "' did not match with '" + current + "'"));
    }

    /**
     * Asserts the urlParams as expected for this TestCase by comparing them to
     * the params currently present. This is only called if at least one
     * urlParam was defined to be expected. For general url-param-checks, see
     * assertGeneralUrlParamRules instead.
     *
     * @param expected
     * @param current
     * @throws TestAnalysisAbortException
     */
    private void assertExpectedUrlParams(String expected, String current) throws TestAnalysisAbortException {

        Map<String, String> expectedMap = HttpUtils.parseUrlParams(expected);

        // Check URL and get query
        String currentQuery = "";
        try {
            URL currentUrl = new URL(current);
            currentQuery = currentUrl.getQuery();
        } catch (MalformedURLException e) {
            throw new TestAnalysisAbortException(TestResult.FAILED, Reason.ASSERTION_FAILED, "Invalid URL: " + current);
        }

        Map<String, String> currentMap = HttpUtils.parseUrlParams(currentQuery);

        Set<String> expectedKeys = expectedMap.keySet();
        for (String expectedKey : expectedKeys) {

            if (!containsKeyIgnoreCase(currentMap, expectedKey)) {
                throw new TestAnalysisAbortException(TestResult.FAILED, Reason.ASSERTION_FAILED, "Expected key " + expectedKey + " not present: " + current);
            } else {
                String expectedValue = expectedMap.get(expectedKey);
                String currentValue = getIgnoreCase(currentMap, expectedKey);
                if (!expectedValue.equalsIgnoreCase(currentValue)) {
                    throw new TestAnalysisAbortException(TestResult.FAILED, Reason.ASSERTION_FAILED, "Param key " + expectedKey + ": expected value: "
                            + expectedValue + ", was: " + currentValue);
                }
            }

        }
    }

    private void assertExpectedUrlPath(String expected, String current, boolean withParams) throws TestAnalysisAbortException {

        // Check URL and get file
        String currentPathWithParams = "";
        String currentPathOnly = "";
        try {
            URL currentUrl = new URL(current);
            currentPathWithParams = currentUrl.getFile().replaceFirst("/", "");
            currentPathOnly = currentUrl.getPath().replaceFirst("/", "");
            ;
        } catch (MalformedURLException e) {
            throw new TestAnalysisAbortException(TestResult.FAILED, Reason.ASSERTION_FAILED, "Invalid URL: " + current);
        }

        if (withParams) {
            if (!expected.equalsIgnoreCase(currentPathWithParams)) {
                throw new TestAnalysisAbortException(TestResult.FAILED, Reason.ASSERTION_FAILED, "Expected path; " + expected + ", was: "
                        + currentPathWithParams);
            }
        } else {
            if (!expected.equalsIgnoreCase(currentPathOnly)) {
                throw new TestAnalysisAbortException(TestResult.FAILED, Reason.ASSERTION_FAILED, "Expected path; " + expected + ", was: " + currentPathOnly);
            }
        }
    }

    private void assertGeneralUrlParamRules(String current) throws TestAnalysisAbortException {

        // Check URL and get query
        String currentQuery = "";
        try {
            URL currentUrl = new URL(current);
            currentQuery = currentUrl.getQuery();
        } catch (MalformedURLException e) {
            throw new TestAnalysisAbortException(TestResult.FAILED, Reason.ASSERTION_FAILED, "Invalid URL: " + current);
        }

        Map<String, String> currentMap = HttpUtils.parseUrlParams(currentQuery);

        // general assertion: 'if ResultMajor=error, there must also be a
        // non-empty ResultMinor=res_min'
        String keyMajor = "ResultMajor";
        if (containsKeyIgnoreCase(currentMap, keyMajor)) {
            String valueMajor = getIgnoreCase(currentMap, keyMajor);
            if (valueMajor.equalsIgnoreCase("error")) {
                // resultMajor is indeed Error, i.e. ResultMinor must be
                // non-empty
                String keyMinor = "ResultMinor";
                if (!containsKeyIgnoreCase(currentMap, keyMinor)) {
                    throw new TestAnalysisAbortException(TestResult.FAILED, Reason.ASSERTION_FAILED,
                            "urlParams contains 'ResultMajor=Error', but no 'ResultMinor'-param.");
                } else {
                    String valueMinor = getIgnoreCase(currentMap, keyMinor);
                    if (valueMinor == null || valueMinor.trim().isEmpty()) {
                        throw new TestAnalysisAbortException(TestResult.FAILED, Reason.ASSERTION_FAILED,
                                "urlParams contains 'ResultMajor=Error' and 'ResultMinor'-param is null/empty.");
                    } else {
                        // check for valid error codes
                        boolean foundAllowedValue = false;
                        List<String> allowedResMinValues = testData.getAllowedResultMinorErrors();
                        for (String allowedResMinValue : allowedResMinValues) {
                            if (valueMinor.equalsIgnoreCase(allowedResMinValue)) {
                                foundAllowedValue = true;
                                break;
                            }
                        }
                        if (!foundAllowedValue) {
                            throw new TestAnalysisAbortException(TestResult.FAILED, Reason.ASSERTION_FAILED,
                                    "urlParams contains 'ResultMajor=Error' but no valid 'ResultMinor'-param.");
                        }
                    }
                }
            } else if (valueMajor.equalsIgnoreCase("ok")) {
                // resultMajor is indeed Ok, i.e. no other params are allowed
                if (currentMap.size() > 1) {
                    throw new TestAnalysisAbortException(TestResult.FAILED, Reason.ASSERTION_FAILED,
                            "urlParams contains 'ResultMajor=Ok' and further params.");
                }
            }
        }

    }

    private void assertStatusCodes(int[] expected, int current) throws TestAnalysisAbortException {

        for (int i = 0; i < expected.length; i++) {
            if (expected[i] == current) {
                return;
            }
        }
        throw new TestAnalysisAbortException(TestResult.FAILED, Reason.ASSERTION_FAILED, "Assertion of: "
                + ITestData.ExpectedTestStepKey.STATUS_CODES.toString() + " failed: Expected " + CommonUtil.arrayToCommaSeparatedString(expected) + ", was: "
                + current);

    }

}
