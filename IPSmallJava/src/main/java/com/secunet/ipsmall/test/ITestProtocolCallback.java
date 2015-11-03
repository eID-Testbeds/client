package com.secunet.ipsmall.test;

public interface ITestProtocolCallback {
    
    public interface ITestEvent {
    }
    
    /**
     * TestSteps are events/steps reported to this callback while a testcase is
     * being process. This is any event, including the default TestSteps
     * (success case) as well as some extra events/steps (browser results,
     * communication error, etc.). Its interpretation is to be done by an
     * implementing callback (see TestRunner).
     * 
     * @author kersten.benjamin
     * 
     */
    public enum TestStep implements ITestEvent {
        
        // The following TestSteps is the expected default test-flow for
        // success testcases, see Jira ECARDCONF-144
        /** implicit, only evaluable by recognizing next step */
        ACTIVATE(
                true, -1),
        /** SAML request only */
        TC_TOKEN_REDIRECT(
                true, 10),
        TC_TOKEN(
                true, 20),
        SERVER_ADDRESS(
                true, 30),
        START_PAOS(
                true, 40),
        INITIALIZE_FRAMEWORK(
                true, 50),
        EAC1(
                true, 60),
        EAC2(
                true, 70),
        EAC3(
                true, 80),
        TRANSMIT(
                true, 90),
        START_PAOS_RESPONSE(
                true, 100),
        /** SAML response only */
        REFRESH_ADDRESS_REDIRECT(
                true, 110),
        /**
         * This TestStep describes the TLS-handshake of the eClientId (AA) with
         * the eService. This is done to verify the refreshAddress. That is,
         * this is NOT the request of the refresh address by the browser (which
         * is BTW totally out of scope for eClient-tests, because the eClient is
         * done as soon as the correct redirect was sent to the browser).
         */
        REFRESH_ADDRESS(
                true, 120),
        /**
         * Redirect the browser (typically to refreshUrl) as final step of
         * default flow
         */
        REDIRECT_BROWSER(
                true, 130),
        // EO default flow
        
        // Some more steps that may occur as additional steps. Those are not
        // part of the default flow and therefore not to be defined as 
        // expect.finalDefaultFlowStep, but only as expect.additionalStep:
        /**
         * reported if eservice's communication error address is requested. Note
         * that this is unusual as the expected result is usually a
         * REDIRECT_BROWSER to the communication error address. In contrast,
         * this would be a direct request to the communication error address.
         * Therefore, the source has to be checked. If the source is the AA and
         * passes content to the browser this might be wrong.
         */
        COMMUNICATION_ERROR_ADDRESS(
                false, 140),
        /**
         * any content (e.g. xml, html) directly passed to the browser. This
         * might be reported by the browser simulator component. Usually, there
         * should be nothing shown in the browser except for the
         * refresh-/communicationError-redirects, in particular not
         * 200-OK-content.
         */
        BROWSER_CONTENT(
                false, 150);
        
        private final boolean partOfSuccessFlow;
        private final int stepOrder;
        
        private TestStep(boolean partOfSuccessFlow, int stepOrder) {
            this.partOfSuccessFlow = partOfSuccessFlow;
            this.stepOrder = stepOrder;
        }
        
        public boolean isPartOfSuccessFlow() {
            return partOfSuccessFlow;
        }
        
        public int getStepOrder() {
            return stepOrder;
        }
    }
    
    public enum TestError implements ITestEvent {
        
        BrowserSimulator(true);
        
        private final boolean critical;

        private TestError(final boolean isCritical) {
            critical = isCritical;
        }
        
        public boolean isCritical() {
            return critical;
        }
        
    }
    
    /**
     * General TestEvent which needs to be interpreted. E.g. TLS_HANDSHAKE might
     * match multiple TestSteps or none.
     * 
     * @author kersten.benjamin
     */
    public enum TestEvent implements ITestEvent {
        
        TLS_HANDSHAKE_DONE;

        private String interpretation;

        public String getInterpretation() {
            return interpretation;
        }

        public void setInterpretation(String interpretation) {
            this.interpretation = interpretation;
        }
    }
    
    /**
     * The source component which is triggering an ITestEvent. This is e.g.
     * important to know whether eService-URL was requested by BrowserSimulator,
     * Browser or eIdClient(AA)
     * 
     * @author kersten.benjamin
     * 
     */
    public enum SourceComponent {
        EID_CLIENT,
        BROWSER_SIMULATOR,
        BROWSER
    }
    
    /**
     * Extension of TestSteps with numbered order, to be used by TestRunner to
     * control sequence of processed TestSteps.
     * 
     * @author kersten.benjamin
     * 
     */
    public class OrderedTestStep {
        
        TestStep testStep;
        /** enumerated for whole testcase: 0,1,2,3,4.... */
        private int numOfStepInSequence;
        /**
         * enumerated per type, i.e. zero for single steps, enumerated for
         * multi-steps, e.g. 0, 0, 0,1,2, 0, 0, ...
         */
        private int numOfStepForType;
        
        private boolean isFinalDefaultFlowStep;
        
        // additional expect params if any:
        private String expectedUrl;
        private String expectedUrlPath;
        private String[] expectedUrlParams;
        private int[] expectedStatusCodes;
        
        public OrderedTestStep(TestStep testStep, int numOfStepInSequence, int numOfStepForType) {
            super();
            this.testStep = testStep;
            this.numOfStepInSequence = numOfStepInSequence;
            this.numOfStepForType = numOfStepForType;
        }
        
        public int getNumOfStepInSequence() {
            return numOfStepInSequence;
        }
        
        public void setNumOfStepInSequence(int numOfStepInSequence) {
            this.numOfStepInSequence = numOfStepInSequence;
        }
        
        public int getNumOfStepForType() {
            return numOfStepForType;
        }
        
        public void setNumOfStepForType(int numOfStepForType) {
            this.numOfStepForType = numOfStepForType;
        }
        
        public TestStep getTestStep() {
            return testStep;
        }
        
        public boolean isFinalDefaultFlowStep() {
            return isFinalDefaultFlowStep;
        }
        
        public void setFinalDefaultFlowStep(boolean isFinalDefaultFlowStep) {
            this.isFinalDefaultFlowStep = isFinalDefaultFlowStep;
        }
        
        public String getExpectedUrl() {
            return expectedUrl;
        }
        
        public void setExpectedUrl(String expectedUrl) {
            this.expectedUrl = expectedUrl;
        }
        
        public String[] getExpectedUrlParams() {
            return expectedUrlParams;
        }
        
        public void setExpectedUrlParams(String[] expectedUrlParams) {
            this.expectedUrlParams = expectedUrlParams;
        }
        
        public int[] getExpectedStatusCodes() {
            return expectedStatusCodes;
        }
        
        public void setExpectedStatusCodes(int[] expectedStatusCodes) {
            this.expectedStatusCodes = expectedStatusCodes;
        }
        
        public String getExpectedUrlPath() {
            return expectedUrlPath;
        }
        
        public void setExpectedUrlPath(String expectedUrlPath) {
            this.expectedUrlPath = expectedUrlPath;
        }
        
    }
    
    public enum TestResult
            implements
            ITestEvent {
        PASSED,
        FAILED,
        UNDETERMINED
    }
    
    /**
     * Method is called on any registered callback for any event occuring during
     * processing of a test.
     * 
     * @param event
     * @param data
     * @param sourceComponent
     * @param callingClass
     */
    public void testProtocolCallback(ITestEvent event, Object data, SourceComponent sourceComponent, Object caller);
}
