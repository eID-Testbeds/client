package com.secunet.ipsmall.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;

import com.secunet.bouncycastle.crypto.tls.Certificate;
import com.secunet.ipsmall.GlobalSettings;
import com.secunet.ipsmall.eval.Evaluator;
import com.secunet.ipsmall.eval.EvaluationConfig;
import com.secunet.ipsmall.exception.GeneralException;
import com.secunet.ipsmall.log.IModuleLogger.EnvironmentClassification;
import com.secunet.ipsmall.log.IModuleLogger.LogLevel;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.test.ITestProtocolCallback.ITestEvent;
import com.secunet.ipsmall.test.ITestProtocolCallback.SourceComponent;
import com.secunet.ipsmall.util.CommonUtil;
import com.secunet.ipsmall.util.FileDateComperator;
import com.secunet.ipsmall.util.FileUtils;
import com.secunet.ipsmall.util.VariableParser;
import com.secunet.testbedutils.cvc.cvcertificate.DataBuffer;

public class FileBasedTestData implements ITestData {

    public final static String c_eCardTestCaseLoad = "ecard.testcase.load";
    public final static String c_eCardTestCaseEnabled = "ecard.testcase.enabled";
    public final static String c_eCardTestCaseManualResult = "ecard.testcase.manualresult";
    public final static String c_eCardTestCaseDesc = "ecard.testcase.description";
    public final static String c_eCardTestCaseReference = "ecard.testcase.reference";
    public final static String c_eCardTestCaseType = "ecard.testcase.type";
    public final static String c_eCardTestCaseFailOnXMLEvaluationError = "ecard.testcase.failOnXMLEvaluationError";
    public final static String c_eCardTestCaseLegacyActivation = "ecard.testcase.legacyActivation";

    public final static String c_eCardTestCaseMessagesBeginPrefix = "ecard.testcase.messages.begin.";
    public final static String c_eCardTestCaseMessagesEndPrefix = "ecard.testcase.messages.end.";

    public final static String c_clientURL = "client.url";

    public final static String c_eidServiceHost = "eidservice.hostname";
    public final static String c_eidServicePort = "eidservice.port";
    public final static String c_eidServiceCertificate = "eidservice.certificate";
    public final static String c_eidServicePrivateKey = "eidservice.privatekey";
    public final static String c_eidServiceUsePSK = "eidservice.usepsk";
    public final static String c_eidServiceIsAttached = "eidservice.isAttached";
    public final static String c_eidServiceAcceptNonConformHTTP11Message = "eidservice.accept.non.conform.http11.messages";

    public final static String c_eidServiceCV_CVCA = "eidservice.cv.cvca";
    public final static String c_eidServiceCV_DVCA = "eidservice.cv.dvca";
    public final static String c_eidServiceCV_TERM = "eidservice.cv.terminal";
    public final static String c_eidServiceCV_TERM2 = "eidservice.cv.terminal2";
    public final static String c_eidServiceCV_TERM_KEY = "eidservice.cv.terminal.key";
    public final static String c_eidServiceCV_TERM_SECTOR = "eidservice.cv.terminal.sector";
    public final static String c_eidServiceCertDescription = "eidservice.cert.description";
    public final static String c_eidServiceChatRequired = "eidservice.chat.req";
    public final static String c_eidServiceChatOptional = "eidservice.chat.opt";
    public final static String c_eidServiceAuxData = "eidservice.aux.data";
    public final static String c_eidServiceTLSVersion = "eidservice.tls.version";
    public final static String c_eidServiceTLSCipherSuites = "eidservice.tls.ciphersuites";
    public final static String c_eidServiceTLSExpectedClientVersion = "eidservice.tls.expectedClientVersion";

    public final static String c_eidServicePAOSresponse = "eidservice.paos.response";
    public final static String c_eidServiceCheckURI = "eidservice.check.uri";
    public final static String c_eidServiceCertsCV_CVCAPrefix = "eidservice.cv.cvca.";
    public final static String c_eidServiceCertsCV_DVCAPrefix = "eidservice.cv.dvca.";
    public final static String c_eidServiceCertsCV_TERMPrefix = "eidservice.cv.terminal.";

    public final static String c_eServiceHost = "eservice.hostname";
    public final static String c_eServicePort = "eservice.port";
    public final static String c_eServiceCertificate = "eservice.certificate";
    public final static String c_eServicePrivateKey = "eservice.privatekey";

    public final static String c_eServiceIndexPageTemplate = "eservice.index.template";
    public final static String c_eServiceIndexPageURL = "eservice.index.urlpath";
    public final static String c_eServiceRefreshPageTemplate = "eservice.refresh.template";
    public final static String c_eServiceRefreshPageURL = "eservice.refresh.urlpath";
    public final static String c_eServiceCommunicationErrorPageTemplate = "eservice.communicationerror.template";
    public final static String c_eServiceCommunicationErrorPageURL = "eservice.communicationerror.urlpath";
    public final static String c_eServiceTCTokenURL = "eservice.tctoken.urlpath";
    public final static String c_eServiceTCTokenTemplate = "eservice.tctoken.template";
    public final static String c_eServiceTLSVersion = "eservice.tls.version";
    public final static String c_eServiceTLSCipherSuites = "eservice.tls.ciphersuites";
    public final static String c_eServiceTLSdhParameters = "eservice.tls.dhParameters";
    public final static String c_eServiceTLSExpectedClientVersion = "eservice.tls.expectedClientVersion";
    public final static String c_eServiceTLSSignatureAlgorithm = "eservice.tls.signaturealgorithm";

    public final static String c_eServiceTLSecCurve = "eservice.tls.eccurve";
    public final static String c_eServiceRedirectURL = "eservice.redirect.urlpath";
    public final static String c_eServiceeRedirectorTCToken = "eservice.redirector.tctoken";
    public final static String c_eServiceRedirectLocation = "eservice.redirect.location";

    public final static String c_tcTokenProviderHost = "tctokenprovider.hostname";
    public final static String c_tcTokenProviderPort = "tctokenprovider.port";
    public final static String c_tcTokenProviderCertificate = "tctokenprovider.certificate";
    public final static String c_tcTokenProviderPrivateKey = "tctokenprovider.privatekey";
    public final static String c_tcTokenProviderIndexPageTemplate = "tctokenprovider.index.template";
    public final static String c_tcTokenProviderTCTokenURL = "tctokenprovider.tctoken.urlpath";
    public final static String c_tcTokenProviderTLSVersion = "tctokenprovider.tls.version";
    public final static String c_tcTokenProviderTLSCipherSuites = "tctokenprovider.tls.ciphersuites";

    public final static String c_commErrorAddressServerHost = "commerroraddressserver.hostname";
    public final static String c_commErrorAddressServerPort = "commerroraddressserver.port";
    public final static String c_commErrorAddressServerCommunicationErrorPageTemplate = "commerroraddressserver.communicationerror.template";
    public final static String c_commErrorAddressServerCommunicationErrorPageURL = "commerroraddressserver.communicationerror.urlpath";
    public final static String c_commErrorAddressServerCertificate = "commerroraddressserver.certificate";
    public final static String c_commErrorAddressServerPrivateKey = "commerroraddressserver.privatekey";
    public final static String c_commErrorAddressServerIndexPageTemplate = "commerroraddressserver.index.template";
    public final static String c_commErrorAddressServerTLSVersion = "commerroraddressserver.tls.version";
    public final static String c_commErrorAddressServerTLSCipherSuites = "commerroraddressserver.tls.ciphersuites";

    public final static String c_redirectorsTCTokenPrefix = "redirector.tctoken.";
    public final static String c_redirectorsRefreshPrefix = "redirector.refresh.";

    public final static String c_redirectorTCTokenHostNamesPrefix = "redirector.tctoken.hostname.";
    public final static String c_redirectorRefreshHostNamesPrefix = "redirector.refresh.hostname.";
    public final static String c_redirectorTCTokenPortsPrefix = "redirector.tctoken.port.";
    public final static String c_redirectorRefreshPortsPrefix = "redirector.refresh.port.";
    public final static String c_redirectorTCTokenUrlPathsPrefix = "redirector.tctoken.urlpath.";
    public final static String c_redirectorRefreshUrlPathsPrefix = "redirector.refresh.urlpath.";
    public final static String c_redirectorTCTokenCertificatesPrefix = "redirector.tctoken.certificate.";
    public final static String c_redirectorRefreshCertificatesPrefix = "redirector.refresh.certificate.";
    public final static String c_redirectorTCTokenPrivateKeysPrefix = "redirector.tctoken.privatekey.";
    public final static String c_redirectorRefreshPrivateKeysPrefix = "redirector.refresh.privatekey.";
    public final static String c_redirectorTCTokenStatusPrefix = "redirector.tctoken.status.";
    public final static String c_redirectorRefreshStatusPrefix = "redirector.refresh.status.";

    public final static String c_eCardStepInitializeFramework = "ecard.InitializeFramework";
    public final static String c_eCardStepDIDAuthenticate1 = "ecard.DIDAuthenticate1";
    public final static String c_eCardStepDIDAuthenticate2 = "ecard.DIDAuthenticate2";
    public final static String c_eCardStepDIDAuthenticate3 = "ecard.DIDAuthenticate3";
    public final static String c_eCardStepTransmit = "ecard.Transmit";
    // public final static String c_eCardStep5 = "ecard.step5";
    public final static String c_eCardStepStartPAOSResponse = "ecard.StartPaosResponse";
    public final static String c_eCardErrorStartPaosResponse = "ecard.ErrorStartPaosResponse";

    public final static String c_eCardUseRawEphemeralPublicKey = "ecard.UseRawEphemeralPublicKey";

    public final static String c_browserSimulatorRmiHost = "browsersimulator.rmi.host";
    public final static String c_browserSimulatorRmiPort = "browsersimulator.rmi.port";

    public final static String c_testcaseAutonomic = "testcase.autonomic";
    public final static String c_testcaseProfiles = "testcase.profiles";
    public final static String c_isEac1ConfirmDialog = "testcase.eac1.confirm";

    public final static String c_networkUnreachableHostname = "network.unreachable.hostname";

    public final static String c_expectedAllowedResultMinorErrors = "expect.allowedParams.resultMinorErrors";

    public final static String c_expectedFinalDefaultFlowStep = "expect.finalDefaultFlowStep";
    public final static String c_expectedFinalDefaultFlowStepUrl = "expect.finalDefaultFlowStep.url";
    public final static String c_expectedFinalDefaultFlowStepUrlPath = "expect.finalDefaultFlowStep.urlPath";
    public final static String c_expectedFinalDefaultFlowStepUrlParams = "expect.finalDefaultFlowStep.urlParams";
    public final static String c_expectedFinalDefaultFlowStepStatusCodes = "expect.finalDefaultFlowStep.statusCodes";

    public final static String c_expectedAdditionalStepPrefix = "expect.additionalStep.";
    public final static String c_expectedAdditionalStepUrlPrefix = "expect.additionalStep.url.";
    public final static String c_expectedAdditionalStepUrlPathPrefix = "expect.additionalStep.urlPath.";
    public final static String c_expectedAdditionalStepUrlParamsPrefix = "expect.additionalStep.urlParams.";
    public final static String c_expectedAdditionalStepStatusCodesPrefix = "expect.additionalStep.statusCodes.";

    public final static String c_eval_startPaosEvalCfg = "eval.startpaos";
    public final static String c_eval_auth1EvalCfg = "eval.auth1";
    public final static String c_eval_auth2EvalCfg = "eval.auth2";
    public final static String c_eval_auth3EvalCfg = "eval.auth3";
    public final static String c_eval_initFwkEvalCfg = "eval.initFramework";
    public final static String c_eval_transmitEvalCfg = "eval.transmit";

    // http/1.1 tests
    public final static String c_disable_http11_tests = "disable.http11.tests";

    // chunked transfer
    public final static String c_chunked_transfer = "chunked.transfer";

    // modfied openssl
    public final static String c_use_mod_ssl = "eservice.useModifiedSSL";

    // card parameters
    public final static String c_DefaultCAKeyID = "card.ca.defaultkeyid";
    public final static String c_EFcardaccess = "card.ef.cardaccess";
    public final static String c_EFcardsecurity = "card.ef.cardsecurity";
    public final static String c_ESignDST = "card.esign.dst";

    // simulation
    public final static String c_cardSimulationDate = "cardsimulation.date";
    public final static String c_simulatedCard_Trustpoint1 = "cardsimulation.trustpoint1";
    public final static String c_simulatedCard_Trustpoint2 = "cardsimulation.trustpoint2";

    public final static String c_cardSimulationResetAtStart = "cardsimulation.resetAtStart";
    public final static String c_cardSimulationConfigurationIdentifier = "cardsimulation.configurationIdentifier";

    // logging
    public final static String c_LogTestFilepath = "Log.TestFilepath";
    public final static String c_LogDebugFilepath = "Log.DebugFilepath";

    // result
    public final static String c_resultIndeterminable = "result.indeterminable";
    public final static String c_resultIndeterminableReason = "result.indeterminable.reason";

    // FIXME remove this once the connection handling is fixed (JIRA EIDCLIENTC-69)
    public final static String c_tcTokenDisconnect = "disable.tctoken.disconnect";
    boolean tcTokenDisconnect;

    // Maximal number of entries for list elements in config.properties (entry suffix range [0-9])
    private final static int c_maxNumberOfCfgListEntries = 10;

    boolean testLoad;
    boolean testEnabled;
    boolean testManualResult;
    String testReference;

    List<String> testMessagesBegin;
    List<String> testMessagesEnd;

    Properties testConfig = null;
    String testConfigBasePath = null;
    Properties commonConfig = null;
    String commonConfigBasePath = null;

    File testObjLogDir;
    String relativeTestObjectFolder;
    // Collection with paths to all available 'config.properties' files +
    // complete content as Properties
    ArrayList<PropsEntry> allProperties = new ArrayList<PropsEntry>();
    // Collection with all found keys and path to related 'config.properties'
    // file that will be used for the appropriate values
    Map<String, String> keyPropFilePath = new TreeMap<String, String>();
    // Collection with file related names (= value in 'config.properties') and
    // path of used file (for e.g. certificates, keys etc.)
    Map<String, String> fileNameFoundPath = new HashMap<String, String>();
    String testNumber;
    String testModuleName;
    String testLocation;
    String testDescription;
    Type testType = ITestData.Type.BROWSERSIMULATOR;
    boolean failOnXMLEvaluationError;
    boolean legacyActivation;

    String clientURL;

    String eidServiceHostname;
    int eidServicePort;
    Certificate eidServiceCertificate;
    AsymmetricKeyParameter eidServicePrivateKey;
    boolean eidServiceUsePSK = false;
    boolean eidServiceIsAttached = false;
    IPublishPSK pskCallback;
    boolean eidServiceAcceptNonConformHTTP11Message;

    byte[] eidServiceCV_CVCA;
    byte[] eidServiceCV_DVCA;
    byte[] eidServiceCV_TERM;
    byte[] eidServiceCV_TERM2;
    byte[] eidServiceCV_TERM_KEY;
    byte[] eidServiceCV_TERM_SECTOR;
    byte[] eidServiceCertificateDescription;
    byte[] eidServiceChatRequired;
    byte[] eidServiceChatOptional;
    byte[] eidServiceAuxData;
    byte[] simulatedCard_Trustpoint1;
    byte[] simulatedCard_Trustpoint2;

    HashMap<Integer, byte[]> eidServiceCerts_CV_CVCA;
    HashMap<Integer, byte[]> eidServiceCerts_CV_DVCA;
    HashMap<Integer, byte[]> eidServiceCerts_CV_TERM;

    int eidServicePAOSresponse;

    String eServiceHostname;
    int eServicePort;
    Certificate eServiceCertificate;
    AsymmetricKeyParameter eServicePrivateKey;
    String eServiceTokenURL;
    String eServiceTokenTemplate;
    String eServiceIndexPageURL;
    String eServiceIndexPageTemplate;
    String eServiceRefreshPageURL;
    String eServiceRefreshPageTemplate;
    String eServiceCommunicationErrorPageURL;
    String eServiceCommunicationErrorPageTemplate;
    String eServiceRedirectURL;
    Integer eServiceRedirectorTCTokenNumber;
    String eServiceRedirectLocation;

    String tcTokenProviderHostname;
    int tcTokenProviderPort;
    X509Certificate[] tcTokenProviderCertificate;
    PrivateKey tcTokenProviderPrivateKey;
    String tcTokenProviderIndexPageTemplate;
    String tcTokenProviderTokenURL;
    List<String> tcTokenProviderTLSVersion;
    List<String> tcTokenProviderTLSCipherSuites;

    String commErrorAddressServerHostname;
    int commErrorAddressServerPort;
    String commErrorAddressServerCommunicationErrorPageURL;
    String commErrorAddressServerCommunicationErrorPageTemplate;
    X509Certificate[] commErrorAddressServerCertificate;
    PrivateKey commErrorAddressServerPrivateKey;
    String commErrorAddressServerIndexPageTemplate;
    List<String> commErrorAddressServerTLSVersion;
    List<String> commErrorAddressServerTLSCipherSuites;

    HashMap<Integer, String[]> redirectorsInfoTCToken;
    HashMap<Integer, String[]> redirectorsInfoRefresh;

    HashMap<Integer, String[]> redirectorTCTokenHosts;
    HashMap<Integer, String[]> redirectorRefreshHosts;
    HashMap<Integer, String[]> redirectorTCTokenPorts;
    HashMap<Integer, String[]> redirectorRefreshPorts;
    HashMap<Integer, String[]> redirectorTCTokenUrlPaths;
    HashMap<Integer, String[]> redirectorRefreshUrlPaths;
    HashMap<Integer, String[]> redirectorTCTokenCertificates;
    HashMap<Integer, String[]> redirectorRefreshCertificates;
    HashMap<Integer, String[]> redirectorTCTokenPrivateKeys;
    HashMap<Integer, String[]> redirectorRefreshPrivateKeys;
    HashMap<Integer, String[]> redirectorTCTokenStatus;
    HashMap<Integer, String[]> redirectorRefreshStatus;

    HashMap<String, ITestSession> testSession = new HashMap<String, ITestSession>();

    String initializeFrameworkTemplate;
    String didAuthenticate1Template;
    String didAuthenticate2Template;
    String didAuthenticate3Template;
    List<String> transmitTemplates;
    // String step5Template;
    String startPAOSResponseTemplate;
    protected String errorStartPaosResponse;

    boolean useRawEphemeralPublicKey;
    boolean disableHTTP11ConformityTest;
    int chunkedTransfer;
    boolean useModifiedSSL;

    String browserSimulatorRmiHost;
    int browserSimulatorRmiPort;

    boolean autonomic;
    List<String> profiles;

    boolean isEac1ConfirmDialog;

    String networkUnreachableHostname;

    List<String> allowedResultMinorErrors;

    /**
     * A HashMap<key,value> representing the final default flow step according
     * to config.properties definitions, e.g.: expectedFinalDefaultFlowStep {
     * c_expectedFinalDefaultFlowStep=redirect_browser,
     * c_expectedFinalDefaultFlowStepUrl={eservice.refresh.urlpath},
     * c_expectedFinalDefaultFlowStepUrlParams=ResultMajor=ok }; i.e. here (in
     * FileBasedTestData), this is pure String-parsing. Conversion to dataTypes
     * and interpretation is done in TestRunner.
     */
    HashMap<ITestData.ExpectedTestStepKey, String> expectedFinalDefaultFlowStep;

    /**
     * Represents the flow of optional additional steps being expected AFTER the
     * last default flow step.
     *
     * The inner HashMap<String, String> is the same as described above for
     * expectedFinalDefaultFlowStep.
     *
     * The outer list represents the sequence steps, as additional steps might
     * consist of multiple(!) steps, i.e. list of steps 0..n.
     *
     */
    List<HashMap<ITestData.ExpectedTestStepKey, String>> expectedAdditionalSteps;

    List<String> eServiceTLSVersion;
    List<String> eServiceTLSCipherSuites;
    String eServiceTLSdhParameters;
    String eServiceTLSExpectedClientVersion;
    String eServiceTLSSignatureAlgorithm;
    String eServiceTLSecCurve;
    List<String> eidServiceTLSVersion;
    List<String> eidServiceTLSCipherSuites;
    String eidServiceTLSExpectedClientVersion;

    boolean eidServiceCheckURI;

    // logging
    protected File LogFile;
    protected String lastTestFilepath;

    // result
    protected boolean resultIndeterminable;
    protected String resultIndeterminableReason;

    // state
    protected boolean loaded = false;
    protected List<ITestProtocolCallback> testProtocolCallbackList = new ArrayList<ITestProtocolCallback>();

    // evaluation
    protected Map<String, EvaluationConfig> startPaosEvalCfg;
    protected Map<String, EvaluationConfig> auth1EvalCfg;
    protected Map<String, EvaluationConfig> auth2EvalCfg;
    protected Map<String, EvaluationConfig> auth3EvalCfg;
    protected Map<String, EvaluationConfig> initFwkEvalCfg;
    protected Map<String, EvaluationConfig> transmitEvalCfg;

    // card parameters
    protected int defaultCAKeyID;
    protected byte[] efCardAccessFile;
    protected byte[] efCardSecurityFile;
    protected byte[] esignDST;

    // card simulation
    protected Date cardDate;
    private boolean cardSimulationResetAtStart;
    private String cardSimulationConfigurationIdentifier;

    // Entry in ArrayList with all Properties
    private class PropsEntry {

        private final String path;
        private final Properties properties;

        public PropsEntry(String path, Properties properties) {
            this.path = path;
            this.properties = properties;
        }

        public String getPath() {
            return path;
        }

        public Properties getProperties() {
            return properties;
        }
    }

    public FileBasedTestData(String testName, File testObjFolder) throws FileNotFoundException, IOException {
        File testcase = new File(testObjFolder, GlobalSettings.getTOTestsDir() + File.separator + testName);
        if (testcase.exists()) {
            initialize(testName, testObjFolder, GlobalSettings.getTOTestsDir(), "Common");
        } else {
            testcase = new File(testObjFolder, GlobalSettings.getTOCopiedTestsDir() + File.separator + testName);
            if (testcase.exists()) {
                initialize(testName, testObjFolder, GlobalSettings.getTOCopiedTestsDir(), null);
            } else {
                throw new FileNotFoundException("Testcase " + testName + " not found.");
            }
        }
    }

    public FileBasedTestData(String testName, File testObjFolder, String testLocation, String commonName) throws FileNotFoundException, IOException {
        initialize(testName, testObjFolder, testLocation, commonName);
    }

    private void initialize(String testName, File testObjFolder, String testLocation, String commonName) throws FileNotFoundException, IOException {
        LogFile = null;
        relativeTestObjectFolder = testObjFolder.toString();

        testObjLogDir = new File(testObjFolder, GlobalSettings.getTOLogDir());

        FileInputStream fis = null;
        if (commonName != null && !commonName.isEmpty()) {
            // mandatory common properties (default test values)
            commonConfigBasePath = testObjFolder.getAbsolutePath() + File.separator + testLocation + File.separator + commonName;
            fis = new FileInputStream(new File(commonConfigBasePath, "config.properties"));
            commonConfig = new Properties();
            commonConfig.load(fis);
            fis.close();
        }

        // mandatory test case-specific properties
        testNumber = CommonUtil.getSubstringAfter(testName, "/", true);
        testModuleName = testName.substring(0, testName.length() - 1 - testNumber.length());
        this.testLocation = testLocation;
        testConfigBasePath = testObjFolder.getAbsolutePath() + File.separator + testLocation + File.separator + testName;
        fis = new FileInputStream(testConfigBasePath + File.separator + "config.properties");
        testConfig = new Properties();
        testConfig.load(fis);
        fis.close();

        allProperties.add(new PropsEntry(testConfigBasePath, testConfig));
        if (commonName != null && !commonName.isEmpty()) {
            allProperties.add(new PropsEntry(commonConfigBasePath, commonConfig));
        }
    }

    public String serialize() {
        StringBuilder builder = new StringBuilder(500);
        for (Map.Entry<String, String> currentEntry : keyPropFilePath.entrySet()) {
            // <key> : <path_cfg_file>
            builder.append(currentEntry.getKey()).append(" : ").append(currentEntry.getValue());

            Properties props = null;
            for (int i = 0; i < allProperties.size(); i++) {
                if (currentEntry.getValue().equalsIgnoreCase(allProperties.get(i).getPath())) {
                    props = allProperties.get(i).getProperties();
                    break;
                }
            }
            if (props != null) {
                String value = (String) props.get(currentEntry.getKey());
                if (fileNameFoundPath.containsKey(value)) {
                    // -> <path_of_file>
                    builder.append(" -> ").append(fileNameFoundPath.get(value));
                }
            }
            builder.append("\r\n");
        }
        return builder.toString();
    }

    public void load() throws Exception {
        testLoad = Boolean.parseBoolean(getPropertyValue(c_eCardTestCaseLoad));
        testEnabled = Boolean.parseBoolean(getPropertyValue(c_eCardTestCaseEnabled));
        testManualResult = Boolean.parseBoolean(getPropertyValue(c_eCardTestCaseManualResult));
        testDescription = getPropertyValue(c_eCardTestCaseDesc);
        testReference = getPropertyValue(c_eCardTestCaseReference);
        String type = getPropertyValue(c_eCardTestCaseType);
        if (type != null) {
            testType = Enum.valueOf(Type.class, getPropertyValue(c_eCardTestCaseType).toUpperCase());
        }
        testMessagesBegin = getListByPropertyKeyPrefix(c_eCardTestCaseMessagesBeginPrefix, c_maxNumberOfCfgListEntries);
        testMessagesEnd = getListByPropertyKeyPrefix(c_eCardTestCaseMessagesEndPrefix, c_maxNumberOfCfgListEntries);

        clientURL = getPropertyValue(c_clientURL);
        eidServiceHostname = getPropertyValue(c_eidServiceHost);
        eidServicePort = Integer.parseInt(getPropertyValue(c_eidServicePort));
        eidServiceCertificate = loadCertifcatesBC(c_eidServiceCertificate);
        eidServicePrivateKey = loadPrivateKeyBC(c_eidServicePrivateKey);
        eidServiceUsePSK = Boolean.parseBoolean(getPropertyValue(c_eidServiceUsePSK));
        eidServiceIsAttached = Boolean.parseBoolean(getPropertyValue(c_eidServiceIsAttached));
        eidServiceAcceptNonConformHTTP11Message = Boolean.parseBoolean(getPropertyValue(c_eidServiceAcceptNonConformHTTP11Message));
        eServiceHostname = getPropertyValue(c_eServiceHost);
        eServicePort = Integer.parseInt(getPropertyValue(c_eServicePort));
        eServiceCertificate = loadCertifcatesBC(c_eServiceCertificate);
        eServicePrivateKey = loadPrivateKeyBC(c_eServicePrivateKey);

        eServiceIndexPageURL = getPropertyValue(c_eServiceIndexPageURL);
        eServiceRefreshPageURL = getPropertyValue(c_eServiceRefreshPageURL);
        eServiceCommunicationErrorPageURL = getPropertyValue(c_eServiceCommunicationErrorPageURL);
        eServiceTokenURL = getPropertyValue(c_eServiceTCTokenURL);
        eServiceTLSVersion = getList(getPropertyValue(c_eServiceTLSVersion));
        eServiceTLSCipherSuites = getList(getPropertyValue(c_eServiceTLSCipherSuites));
        eServiceTLSdhParameters = getPropertyValue(c_eServiceTLSdhParameters);
        eServiceTLSExpectedClientVersion = getPropertyValue(c_eServiceTLSExpectedClientVersion);
        eServiceTLSSignatureAlgorithm = getPropertyValue(c_eServiceTLSSignatureAlgorithm);
        eServiceTLSecCurve = getPropertyValue(c_eServiceTLSecCurve);

        tcTokenProviderHostname = getPropertyValue(c_tcTokenProviderHost);
        if ((tcTokenProviderHostname != null) && (tcTokenProviderHostname.length() > 0)) {
            String tcTokenProviderPortStr = getPropertyValue(c_tcTokenProviderPort);
            if ((tcTokenProviderPortStr != null) && (tcTokenProviderPortStr.length() > 0)) {
                tcTokenProviderPort = Integer.parseInt(tcTokenProviderPortStr);
            }
            tcTokenProviderCertificate = loadCertifcates(c_tcTokenProviderCertificate);
            tcTokenProviderPrivateKey = loadPrivateKey(c_tcTokenProviderPrivateKey);
            tcTokenProviderTokenURL = getPropertyValue(c_tcTokenProviderTCTokenURL);
            tcTokenProviderTLSVersion = getList(getPropertyValue(c_tcTokenProviderTLSVersion));
            tcTokenProviderTLSCipherSuites = getList(getPropertyValue(c_tcTokenProviderTLSCipherSuites));
        }

        commErrorAddressServerHostname = getPropertyValue(c_commErrorAddressServerHost);
        if (commErrorAddressServerHostname != null && commErrorAddressServerHostname.length() > 0) {
            String commErrorAddressServerPortStr = getPropertyValue(c_commErrorAddressServerPort);
            commErrorAddressServerPort = Integer.parseInt(commErrorAddressServerPortStr);
            commErrorAddressServerCertificate = loadCertifcates(c_commErrorAddressServerCertificate);
            commErrorAddressServerPrivateKey = loadPrivateKey(c_commErrorAddressServerPrivateKey);
            commErrorAddressServerTLSVersion = getList(getPropertyValue(c_commErrorAddressServerTLSVersion));
            commErrorAddressServerTLSCipherSuites = getList(getPropertyValue(c_commErrorAddressServerTLSCipherSuites));
            commErrorAddressServerCommunicationErrorPageURL = getPropertyValue(c_commErrorAddressServerCommunicationErrorPageURL);
            commErrorAddressServerCommunicationErrorPageTemplate = getPropertyValue(c_commErrorAddressServerCommunicationErrorPageTemplate);
        }

        initializeFrameworkTemplate = getStringFileByPropertyKey(c_eCardStepInitializeFramework);
        didAuthenticate1Template = getStringFileByPropertyKey(c_eCardStepDIDAuthenticate1);
        didAuthenticate2Template = getStringFileByPropertyKey(c_eCardStepDIDAuthenticate2);
        didAuthenticate3Template = getStringFileByPropertyKey(c_eCardStepDIDAuthenticate3);
        transmitTemplates = getStringFilesByPropertyKey(c_eCardStepTransmit);
        // step5Template = getStringFileByProperty(c_eCardStep5);
        startPAOSResponseTemplate = getStringFileByPropertyKey(c_eCardStepStartPAOSResponse);
        errorStartPaosResponse = getStringFileByPropertyKey(c_eCardErrorStartPaosResponse);
        useRawEphemeralPublicKey = Boolean.parseBoolean(getPropertyValue(c_eCardUseRawEphemeralPublicKey));

        eidServiceCV_CVCA = getFileByPropertyKey(c_eidServiceCV_CVCA);
        eidServiceCV_DVCA = getFileByPropertyKey(c_eidServiceCV_DVCA);
        eidServiceCV_TERM = getFileByPropertyKey(c_eidServiceCV_TERM);
        eidServiceCV_TERM2 = getFileByPropertyKey(c_eidServiceCV_TERM2);
        eidServiceCV_TERM_KEY = getFileByPropertyKey(c_eidServiceCV_TERM_KEY);
        eidServiceCV_TERM_SECTOR = getFileByPropertyKey(c_eidServiceCV_TERM_SECTOR);
        eidServiceCertificateDescription = getFileByPropertyKey(c_eidServiceCertDescription);
        eidServiceChatRequired = getFileByPropertyKey(c_eidServiceChatRequired);
        eidServiceChatOptional = getFileByPropertyKey(c_eidServiceChatOptional);
        eidServiceAuxData = getFileByPropertyKey(c_eidServiceAuxData);
        simulatedCard_Trustpoint1 = getFileByPropertyKey(c_simulatedCard_Trustpoint1);
        simulatedCard_Trustpoint2 = getFileByPropertyKey(c_simulatedCard_Trustpoint2);
        eidServiceTLSVersion = getList(getPropertyValue(c_eidServiceTLSVersion));
        eidServiceTLSCipherSuites = getList(getPropertyValue(c_eidServiceTLSCipherSuites));
        eidServiceTLSExpectedClientVersion = getPropertyValue(c_eidServiceTLSExpectedClientVersion);
        eidServiceCheckURI = Boolean.parseBoolean(getPropertyValue(c_eidServiceCheckURI));

        eidServiceCerts_CV_CVCA = getFileContentsByPropertyKeyPrefix(c_eidServiceCertsCV_CVCAPrefix, c_maxNumberOfCfgListEntries);
        eidServiceCerts_CV_DVCA = getFileContentsByPropertyKeyPrefix(c_eidServiceCertsCV_DVCAPrefix, c_maxNumberOfCfgListEntries);
        eidServiceCerts_CV_TERM = getFileContentsByPropertyKeyPrefix(c_eidServiceCertsCV_TERMPrefix, c_maxNumberOfCfgListEntries);

        // the http response code for PAOS requests
        String paosResponseCode = getPropertyValue(c_eidServicePAOSresponse);
        if (paosResponseCode != null) {
            try {
                eidServicePAOSresponse = Integer.parseInt(paosResponseCode);
            } catch (IllegalArgumentException e) {
                Logger.Global.logEnvironment(EnvironmentClassification.TestCaseConfig, "Invalid PAOS reponse code: " + paosResponseCode, LogLevel.Error);
            }
        }

        redirectorTCTokenHosts = getNTupelArrays(c_redirectorTCTokenHostNamesPrefix, c_maxNumberOfCfgListEntries, 1);
        redirectorRefreshHosts = getNTupelArrays(c_redirectorRefreshHostNamesPrefix, c_maxNumberOfCfgListEntries, 1);
        redirectorTCTokenPorts = getNTupelArrays(c_redirectorTCTokenPortsPrefix, c_maxNumberOfCfgListEntries, 1);
        redirectorRefreshPorts = getNTupelArrays(c_redirectorRefreshPortsPrefix, c_maxNumberOfCfgListEntries, 1);
        redirectorTCTokenUrlPaths = getNTupelArrays(c_redirectorTCTokenUrlPathsPrefix, c_maxNumberOfCfgListEntries, 1);
        redirectorRefreshUrlPaths = getNTupelArrays(c_redirectorRefreshUrlPathsPrefix, c_maxNumberOfCfgListEntries, 1);
        redirectorTCTokenCertificates = getNTupelArrays(c_redirectorTCTokenCertificatesPrefix, c_maxNumberOfCfgListEntries, 1);
        for (Map.Entry<Integer, String[]> mapEntry : redirectorTCTokenCertificates.entrySet()) {
            String[] array = mapEntry.getValue();
            for (int i = 0; i < array.length; i++) {
                getAbsFilePath(array[i]);
            }
        }
        redirectorRefreshCertificates = getNTupelArrays(c_redirectorRefreshCertificatesPrefix, c_maxNumberOfCfgListEntries, 1);
        for (Map.Entry<Integer, String[]> mapEntry : redirectorRefreshCertificates.entrySet()) {
            String[] array = mapEntry.getValue();
            for (int i = 0; i < array.length; i++) {
                getAbsFilePath(array[i]);
            }
        }
        redirectorTCTokenPrivateKeys = getNTupelArrays(c_redirectorTCTokenPrivateKeysPrefix, c_maxNumberOfCfgListEntries, 1);
        for (Map.Entry<Integer, String[]> mapEntry : redirectorTCTokenPrivateKeys.entrySet()) {
            String[] array = mapEntry.getValue();
            for (int i = 0; i < array.length; i++) {
                getAbsFilePath(array[i]);
            }
        }
        redirectorRefreshPrivateKeys = getNTupelArrays(c_redirectorRefreshPrivateKeysPrefix, c_maxNumberOfCfgListEntries, 1);
        for (Map.Entry<Integer, String[]> mapEntry : redirectorRefreshPrivateKeys.entrySet()) {
            String[] array = mapEntry.getValue();
            for (int i = 0; i < array.length; i++) {
                getAbsFilePath(array[i]);
            }
        }
        redirectorTCTokenStatus = getNTupelArrays(c_redirectorTCTokenStatusPrefix, c_maxNumberOfCfgListEntries, 1);
        redirectorRefreshStatus = getNTupelArrays(c_redirectorRefreshStatusPrefix, c_maxNumberOfCfgListEntries, 1);

        redirectorsInfoTCToken = getNTupelArrays(c_redirectorsTCTokenPrefix, c_maxNumberOfCfgListEntries, 5);
        redirectorsInfoRefresh = getNTupelArrays(c_redirectorsRefreshPrefix, c_maxNumberOfCfgListEntries, 5);

        eServiceRedirectURL = getParsedPropertyValue(c_eServiceRedirectURL);

        String eServiceRedirectorTCToken = getPropertyValue(c_eServiceeRedirectorTCToken);
        if (eServiceRedirectorTCToken != null) {
            eServiceRedirectorTCTokenNumber = Integer.parseInt(CommonUtil.getSubstringBefore(
                    CommonUtil.getSubstringAfter(eServiceRedirectorTCToken, ".", true), "}", true));
        }

        eServiceRedirectLocation = getParsedPropertyValue(c_eServiceRedirectLocation);

        eServiceTokenTemplate = getStringFileByPropertyKey(c_eServiceTCTokenTemplate);

        browserSimulatorRmiHost = getPropertyValue(c_browserSimulatorRmiHost);
        browserSimulatorRmiPort = Integer.parseInt(getPropertyValue(c_browserSimulatorRmiPort));

        allowedResultMinorErrors = new ArrayList<String>(Arrays.asList(getPropertyValue(c_expectedAllowedResultMinorErrors).split(",")));

        expectedFinalDefaultFlowStep = getExpectedStep(-1);
        expectedAdditionalSteps = getExpectedAdditionalSteps(10);

        autonomic = Boolean.parseBoolean(getPropertyValue(c_testcaseAutonomic));
        profiles = getList(getPropertyValue(c_testcaseProfiles));
        isEac1ConfirmDialog = Boolean.parseBoolean(getPropertyValue(c_isEac1ConfirmDialog));
        failOnXMLEvaluationError = Boolean.parseBoolean(getPropertyValue(c_eCardTestCaseFailOnXMLEvaluationError));
        legacyActivation = Boolean.parseBoolean(getPropertyValue(c_eCardTestCaseLegacyActivation));

        networkUnreachableHostname = getPropertyValue(c_networkUnreachableHostname);

        String sCAKeyID = getPropertyValue(c_DefaultCAKeyID);
        if (sCAKeyID != null) {
            try {
                defaultCAKeyID = Integer.parseInt(sCAKeyID);
            } catch (IllegalArgumentException e) {
                Logger.Global.logEnvironment(EnvironmentClassification.TestCaseConfig, "Invalid CA key ID: " + sCAKeyID, LogLevel.Error);
            }
        }
        efCardAccessFile = getFileByPropertyKey(c_EFcardaccess);
        efCardSecurityFile = getFileByPropertyKey(c_EFcardsecurity);

        String dstValue = getPropertyValue(c_ESignDST);
        if (dstValue != null) {
            try {
                esignDST = DatatypeConverter.parseHexBinary(dstValue);
            } catch (IllegalArgumentException e) {
                esignDST = new byte[0];
                Logger.Global.logEnvironment(EnvironmentClassification.TestCaseConfig, "Invalid hex format: " + dstValue, LogLevel.Error);
            }
        } else {
            esignDST = new byte[0];
            Logger.Global.logEnvironment(EnvironmentClassification.TestCaseConfig, c_ESignDST + " not set.", LogLevel.Error);
        }

        DateFormat dateFor = new SimpleDateFormat("yyyyMMdd");
        try {
            cardDate = dateFor.parse(getPropertyValue(c_cardSimulationDate));
        } catch (ParseException e) {
            Logger.Global.logEnvironment(EnvironmentClassification.TestCaseConfig, "Invalid date format: " + getPropertyValue(c_cardSimulationDate),
                    LogLevel.Error);
        }

        // http1.1
        disableHTTP11ConformityTest = Boolean.parseBoolean(getPropertyValue(c_disable_http11_tests));

        // chunked transfer
        String sChunkSize = getPropertyValue(c_chunked_transfer);
        if (sChunkSize != null) {
            try {
                int chunkSize = Integer.parseInt(sChunkSize);
                chunkedTransfer = (chunkSize > 0) ? chunkSize : -1;
            } catch (IllegalArgumentException e) {
                chunkedTransfer = -1;
                Logger.Global.logEnvironment(EnvironmentClassification.TestCaseConfig, "Illegal chunk size: " + sChunkSize, LogLevel.Error);
            }
        } else {
            chunkedTransfer = -1;
        }

        // use modified ssl version
        useModifiedSSL = Boolean.parseBoolean(getPropertyValue(c_use_mod_ssl));

        // FIXME remove this once the connection handling is fixed (JIRA EIDCLIENTC-69)
        tcTokenDisconnect = Boolean.parseBoolean(getPropertyValue(c_tcTokenDisconnect));

        resultIndeterminable = Boolean.parseBoolean(getPropertyValue(c_resultIndeterminable));
        resultIndeterminableReason = getPropertyValue(c_resultIndeterminableReason);

        cardSimulationResetAtStart = Boolean.parseBoolean(getPropertyValue(c_cardSimulationResetAtStart));
        cardSimulationConfigurationIdentifier = getPropertyValue(c_cardSimulationConfigurationIdentifier);

        preparePages();

        loaded = true;
        /*for (String nullMember : getNotLoadedMembers()) {
         Logger.Global.logEnvironment(EnvironmentClassification.TestCaseConfig, testModuleName + "/" + testNumber + "\tProperty '" + nullMember
         + "' is NULL!", LogLevel.Warn);
         }*/
    }

    /**
     * @return list contains non static, non final members that are null and
     * don't start with 'Log' and not 'lastTestFilepath'
     */
    public List<String> getNotLoadedMembers() {
        List<String> nullMembers = new ArrayList<>();

        for (Field field : getClass().getDeclaredFields()) {
            try {
                if (!(Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers()))
                        && (field.get(this) == null && !field.getName().startsWith("Log") && !field.getName().equals("lastTestFilepath"))) {
                    nullMembers.add(field.getName());
                }
            } catch (Exception ex) {
            }
        }

        return nullMembers;
    }

    /**
     * Takes a property value defined as comma-delimited tupel and returns a
     * String[] from that. E.g. takes <code>key=value1, value2</code> and
     * returns <code><br/>
     * String<br/>[value1][value2]
     * </code>
     *
     * @param prop Property
     * @param numberOfTupels Number of expected tupels
     * @return Array of split property values
     */
    private String[] getNTupelArray(String prop, int numberOfTupels) {
        String[] result = null;
        if (prop != null && !prop.trim().isEmpty()) {
            String[] parts = prop.split(",");
            if (parts.length % numberOfTupels != 0) {
                throw new RuntimeException("Test file parsing error, no valid tupel for: " + prop);
            }
            result = new String[numberOfTupels];
            for (int i = 0; i < parts.length; i++) {
                result[i] = parts[i].trim();
            }

        }
        return result;
    }

    /**
     * Get collection of n tupel arrays (see
     * {@link #getNTupelArray(String, int)})
     *
     * @param keyPrefix Prefix of property key.
     * @param maxNumber Maximal number of keys that will be checked for values
     * @param numberOfTupels Number of expected tupels
     * @return Collection of n tupel arrays
     */
    private HashMap<Integer, String[]> getNTupelArrays(String keyPrefix, int maxNumber, int numberOfTupels) throws Exception {
        HashMap<Integer, String[]> map = null;
        VariableParser parser = new VariableParser(new VariableParameter(this));

        for (int i = 0; i < maxNumber; i++) {

            String value = getPropertyValue(keyPrefix + Integer.toString(i));
            if (value != null) {

                value = parser.format(value);

                if (map == null) {
                    map = new HashMap<Integer, String[]>();
                }
                map.put(i, getNTupelArray(value, numberOfTupels));
            }
        }

        return map;
    }

    /**
     * Get list of property values referenced by property key prefix
     *
     * @param keyPrefix Prefix of property key
     * @param maxNumber Maximal number of keys that will be checked for values
     * @return List of property values
     */
    private List<String> getListByPropertyKeyPrefix(String keyPrefix, int maxNumber) {
        List<String> list = new ArrayList<String>(maxNumber);

        for (int i = 0; i < maxNumber; i++) {
            String value = getPropertyValue(keyPrefix + Integer.toString(i));
            if (value != null) {
                list.add(value);
            }
        }
        return list;
    }

    /**
     * Get collection of binary file contents referenced by property key prefix
     *
     * @param keyPrefix Prefix of property key
     * @param maxNumber Maximal number of keys that will be checked for values
     * @return Collection of file contents
     */
    private HashMap<Integer, byte[]> getFileContentsByPropertyKeyPrefix(String keyPrefix, int maxNumber) throws Exception {

        HashMap<Integer, byte[]> map = null;

        for (int i = 0; i < maxNumber; i++) {
            byte[] content = getFileByPropertyKey(keyPrefix + Integer.toString(i));
            if (content != null) {

                if (map == null) {
                    map = new HashMap<Integer, byte[]>();
                }
                map.put(i, content);
            }
        }

        return map;
    }

    /**
     * constructs a HashMap of String-based <c_key, value>-pairs for a single
     * TestStep, either default-flow or additional.
     *
     * @param number -1 for default-flow, 0..n for additional
     * @return
     */
    private HashMap<ITestData.ExpectedTestStepKey, String> getExpectedStep(int number) {

        String keyStep, keyUrl, keyUrlPath, keyUrlParams, keyStatusCodes = null;

        if (number == -1) {
            // -1 represents defaultFlowStep
            keyStep = c_expectedFinalDefaultFlowStep;
            keyUrl = c_expectedFinalDefaultFlowStepUrl;
            keyUrlPath = c_expectedFinalDefaultFlowStepUrlPath;
            keyUrlParams = c_expectedFinalDefaultFlowStepUrlParams;
            keyStatusCodes = c_expectedFinalDefaultFlowStepStatusCodes;
        } else {
            // 0..n represents additionalFlowSteps
            keyStep = c_expectedAdditionalStepPrefix + number;
            keyUrl = c_expectedAdditionalStepUrlPrefix + number;
            keyUrlPath = c_expectedAdditionalStepUrlPathPrefix + number;
            keyUrlParams = c_expectedAdditionalStepUrlParamsPrefix + number;
            keyStatusCodes = c_expectedAdditionalStepStatusCodesPrefix + number;
        }

        if (getPropertyValue(keyStep) == null) {
            // the step is mandatory, return null if not present
            return null;
        } else {
            HashMap<ITestData.ExpectedTestStepKey, String> result = new HashMap<>(3);
            result.put(ITestData.ExpectedTestStepKey.STEP, getPropertyValue(keyStep));
            result.put(ITestData.ExpectedTestStepKey.URL, getPropertyValue(keyUrl));
            result.put(ITestData.ExpectedTestStepKey.URL_PATH, getPropertyValue(keyUrlPath));
            result.put(ITestData.ExpectedTestStepKey.URL_PARAMS, getPropertyValue(keyUrlParams));
            result.put(ITestData.ExpectedTestStepKey.STATUS_CODES, getPropertyValue(keyStatusCodes));

            return result;
        }
    }

    /**
     * Reads the expected additional steps (expected AFTER default flow) as list
     * of String-based HashMap. Additional steps are optional and its
     * number/size is arbitrary. The maxNumber defines the maxNumber to try to
     * parse, e.g. 10 is a reasonable number if not more than 10 additional
     * steps are expected for any testCase. It would then try to get
     * expect.additionalStep.0 -to- .9 and skip any non-existing entries (e.g.
     * also works if 0 and 2 are present, but 1 not).
     *
     * @param maxNumber
     * @return
     */
    private List<HashMap<ITestData.ExpectedTestStepKey, String>> getExpectedAdditionalSteps(int maxNumber) {

        List<HashMap<ITestData.ExpectedTestStepKey, String>> result = null;
        for (int i = 0; i < maxNumber; i++) {
            HashMap<ITestData.ExpectedTestStepKey, String> stepResult = getExpectedStep(i);
            if (stepResult != null) {
                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(stepResult);
            }
        }
        return result;
    }

    /**
     * Takes a property value defined as comma-delimited values and returns a
     * List from that.
     *
     * @param prop Property
     * @return List of split property values
     */
    private List<String> getList(String prop) {
        List<String> list = new ArrayList<String>();
        if (prop != null && !prop.trim().isEmpty()) {
            String[] parts = prop.split(",");
            for (int i = 0; i < parts.length; i++) {
                list.add(parts[i].trim());
            }
        }
        return list;
    }

    private void preparePages() throws Exception {
        VariableParser parser = new VariableParser(new VariableParameter(this));
        eServiceIndexPageTemplate = parser.format(getStringFileByPropertyKey(c_eServiceIndexPageTemplate));
        tcTokenProviderIndexPageTemplate = parser.format(getStringFileByPropertyKey(c_tcTokenProviderIndexPageTemplate));
        eServiceRefreshPageTemplate = parser.format(getStringFileByPropertyKey(c_eServiceRefreshPageTemplate));
        eServiceCommunicationErrorPageTemplate = parser.format(getStringFileByPropertyKey(c_eServiceCommunicationErrorPageTemplate));
    }

    public String getValue(String key) {
        String value = getPropertyValue(key);

        File valueFile = null;
        try {
            valueFile = new File(getAbsFilePath(value));
            if (valueFile.isFile()) {
                int i = valueFile.getName().lastIndexOf('.');
                if (i > 0) {
                    String ext = valueFile.getName().substring(i + 1);
                    switch (ext) {
                        case "html":
                        case "xml":
                        case "properties":
                            value = getStringFileByPropertyKey(key);
                            break;
                        default:
                            DataBuffer fileData = new DataBuffer(getFileByPropertyKey(key));
                            value = fileData.asHex("");
                            break;
                    }
                }
            }
        } catch (Exception e) {
        }

        return value;
    }

    private String getParsedPropertyValue(String key) throws Exception {
        VariableParser parser = new VariableParser(new VariableParameter(this));
        String value = getPropertyValue(key);
        return (value != null) ? parser.format(getPropertyValue(key)) : null;
    }

    private String getPropertyValue(String key) {
        if (testConfig != null) {
            String value = testConfig.getProperty(key);
            if (value == null && commonConfig != null) {
                value = commonConfig.getProperty(key);
            } else {
                if (testConfigBasePath != null) {
                    keyPropFilePath.put(key, testConfigBasePath);
                }

                return value == null ? value : value.trim();
            }

            if (value != null && commonConfigBasePath != null) {
                keyPropFilePath.put(key, commonConfigBasePath);
            }

            return value == null ? value : value.trim();
        } else {
            return null;
        }
    }

    private String getPropertyValue(Properties props, String key) {
        String value = props.getProperty(key);
        return value == null ? value : value.trim();
    }

    private List<String> getStringFilesByPropertyKey(String key) throws IOException {

        List<String> values = getList(getPropertyValue(key));
        if (values == null) {
            return null;
        }

        List<String> fileContents = null;
        for (int i = 0; i < values.size(); i++) {
            // String startPath = keyPropFilePath.get(key);
            String absFilePath = getFilePath(key, null);
            if (absFilePath != null) {
                if (fileContents == null) {
                    fileContents = new ArrayList<String>();
                }
                fileContents.add(CommonUtil.readFromFileAsString(absFilePath));
            }
        }
        return fileContents;
    }

    private String getStringFileByPropertyKey(String key) throws IOException {
        String value = getPropertyValue(key);
        if (value == null) {
            return null;
        }
        // String startPath = keyPropFilePath.get(key);
        String absFilePath = getFilePath(key, null);
        return absFilePath == null ? null : CommonUtil.readFromFileAsString(absFilePath);
    }

    private byte[] getFileByPropertyKey(String key) throws IOException {
        String value = getPropertyValue(key);
        if (value == null) {
            return null;
        }
        // String startPath = keyPropFilePath.get(key);
        String absFilePath = getFilePath(key, null);
        return absFilePath == null ? null : CommonUtil.readFromFile(absFilePath);
    }

    private PrivateKey loadPrivateKey(String propKey) throws Exception {
        String value = getPropertyValue(propKey);
        if (value == null) {
            return null;
        }
        // String startPath = keyPropFilePath.get(propKey);
        String absFilePath = getFilePath(propKey, null);
        if (absFilePath == null) {
            return null;
        }
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(CommonUtil.readFromFile(absFilePath));

        PrivateKey pk = null;

        // This workaround could surely be more elegant
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            pk = kf.generatePrivate(keySpec);
        } catch (java.security.spec.InvalidKeySpecException e) {
            try {
                KeyFactory kf = KeyFactory.getInstance("EC");
                pk = kf.generatePrivate(keySpec);
            } catch (java.security.spec.InvalidKeySpecException e2) {
                KeyFactory kf = KeyFactory.getInstance("DSA");
                pk = kf.generatePrivate(keySpec);
            }
        }
        return pk;
    }

    private AsymmetricKeyParameter loadPrivateKeyBC(String propKey) throws Exception {
        String value = getPropertyValue(propKey);
        if (value == null) {
            return null;
        }

        String absFilePath = getFilePath(propKey, null);
        if (absFilePath == null) {
            return null;
        }

        return PrivateKeyFactory.createKey(CommonUtil.readFromFile(absFilePath));
    }

    @Override
    public PrivateKey readPrivateKey(String fileName) throws Exception {
        String absFilePath = getAbsFilePath(fileName);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(CommonUtil.readFromFile(absFilePath));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(keySpec);
    }

    private X509Certificate[] loadCertifcates(String key) throws Exception {
        try {
            ArrayList<X509Certificate> certList = new ArrayList<X509Certificate>();

            String propFiles = getPropertyValue(key);
            StringTokenizer token = new StringTokenizer(propFiles, ";");
            while (token.hasMoreTokens()) {
                String curToken = token.nextToken();
                certList.add(readCertificate(curToken));
            }
            return certList.toArray(new X509Certificate[]{});
        } catch (Exception ex) {
            throw new GeneralException("Could not load certificate: " + key);
        }
    }

    @Override
    public X509Certificate readCertificate(String fileName) throws Exception {
        CertificateFactory fac = CertificateFactory.getInstance("x.509");
        String absFilePath = getAbsFilePath(fileName);
        FileInputStream fis = new FileInputStream(absFilePath);
        X509Certificate cert = (X509Certificate) fac.generateCertificate(fis);
        fis.close();

        return cert;
    }

    private Certificate loadCertifcatesBC(String key) throws Exception {
        try {
            ArrayList<org.bouncycastle.asn1.x509.Certificate> certList = new ArrayList<org.bouncycastle.asn1.x509.Certificate>();

            String propFiles = getPropertyValue(key);
            StringTokenizer token = new StringTokenizer(propFiles, ";");
            while (token.hasMoreTokens()) {
                String curToken = token.nextToken();
                certList.add(readCertificateBC(curToken));
            }
            org.bouncycastle.asn1.x509.Certificate[] asn1Certs = certList.toArray(new org.bouncycastle.asn1.x509.Certificate[]{});
            return new Certificate(asn1Certs);
        } catch (Exception ex) {
            throw new GeneralException("Could not load certificate: " + key);
        }
    }

    public org.bouncycastle.asn1.x509.Certificate readCertificateBC(String fileName) throws Exception {
        String absFilePath = getAbsFilePath(fileName);
        FileInputStream fis = new FileInputStream(absFilePath);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = fis.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();

        org.bouncycastle.asn1.x509.Certificate asn1Cert = org.bouncycastle.asn1.x509.Certificate.getInstance(buffer.toByteArray());

        fis.close();

        return asn1Cert;
    }

    private String getAbsFilePath(String fileName) {
        for (int i = 0; i < allProperties.size(); i++) {
            String absFilePath = allProperties.get(i).getPath() + File.separator + fileName;
            if (CommonUtil.isFileAccessible(absFilePath)) {
                fileNameFoundPath.put(fileName, absFilePath);
                return absFilePath;
            }
        }
        return null;
    }

    private String getFilePath(String key, String startPath) {

        String value = null;
        String path = null;

        for (int i = 0; i < allProperties.size(); i++) {
            if ((startPath != null) && (!startPath.equalsIgnoreCase(allProperties.get(i).getPath()))) {
                continue;
            }
            if (value == null) {
                value = getPropertyValue(allProperties.get(i).getProperties(), key);
            }
            if (value != null) {
                path = getAbsFilePath(value);
                break;
            }
        }

        return path;
    }

    @Override
    public String getTestName() {
        return testNumber;
    }

    @Override
    public boolean getTestLoad() {
        return testLoad;
    }

    @Override
    public boolean getTestEnabled() {
        return testEnabled;
    }

    @Override
    public boolean getTestManualResult() {
        return testManualResult;
    }

    @Override
    public String getTestDescription() {
        return testDescription;
    }

    @Override
    public String getTestReference() {
        return testReference;
    }

    @Override
    public List<String> getTestMessagesBegin() {
        return testMessagesBegin;
    }

    @Override
    public List<String> getTestMessagesEnd() {
        return testMessagesEnd;
    }

    @Override
    public String getClientURL() {
        return clientURL;
    }

    @Override
    public String getEIDServiceHost() {
        return eidServiceHostname;
    }

    @Override
    public int getEIDServicePort() {
        return eidServicePort;
    }

    @Override
    public Certificate getEIDServiceCertificate() {

        return eidServiceCertificate;
    }

    @Override
    public AsymmetricKeyParameter getEIDServerPrivateKey() {

        return eidServicePrivateKey;
    }

    @Override
    public boolean useEIDServiceTLSPSK() {

        return eidServiceUsePSK;
    }

    @Override
    public boolean isEIDServiceAttached() {

        return eidServiceIsAttached;
    }

    @Override
    public boolean eIDServiceAccpetNonConformHTTP11Message() {
        return eidServiceAcceptNonConformHTTP11Message;
    }

    @Override
    public void setPSKCallback(IPublishPSK pskCallback) {
        this.pskCallback = pskCallback;
    }

    @Override
    public byte[] getEIDServiceCV_CVCA() {
        return eidServiceCV_CVCA;
    }

    @Override
    public byte[] getEIDServiceCV_DVCA() {
        return eidServiceCV_DVCA;
    }

    @Override
    public byte[] getEIDServiceCV_TERM() {
        return eidServiceCV_TERM;
    }

    @Override
    public byte[] getEIDServiceCV_TERM2() {
        return eidServiceCV_TERM2;
    }

    @Override
    public byte[] getEIDServiceCV_TERM_KEY() {
        return eidServiceCV_TERM_KEY;
    }

    @Override
    public byte[] getEIDServiceCV_TERM_SECTOR() {
        return eidServiceCV_TERM_SECTOR;
    }

    @Override
    public byte[] getEIDServiceCertificateDescription() {
        return eidServiceCertificateDescription;
    }

    @Override
    public byte[] getEIDServiceChatRequired() {
        return eidServiceChatRequired;
    }

    @Override
    public byte[] getEIDServiceChatOptional() {
        return eidServiceChatOptional;
    }

    @Override
    public byte[] getEIDServiceAuxData() {
        return eidServiceAuxData;
    }

    @Override
    public byte[] getSimulatedCard_Trustpoint1() {
        return simulatedCard_Trustpoint1;
    }

    @Override
    public byte[] getSimulatedCard_Trustpoint2() {
        return simulatedCard_Trustpoint2;
    }

    @Override
    public String getEServiceHost() {
        return eServiceHostname;
    }

    @Override
    public int getEServicePort() {
        return eServicePort;
    }

    @Override
    public Certificate getEServiceCertificate() {
        return eServiceCertificate;
    }

    @Override
    public AsymmetricKeyParameter getEServerPrivateKey() {
        return eServicePrivateKey;
    }

    @Override
    public String getEServiceIndexPage() {
        return eServiceIndexPageTemplate;
    }

    @Override
    public String getEServiceRefreshPage() {
        return eServiceRefreshPageTemplate;
    }

    @Override
    public String getEServiceRefreshPageURL() {
        return eServiceRefreshPageURL;
    }

    @Override
    public String getEServiceTCTokenURL() {
        return eServiceTokenURL;
    }

    @Override
    public String getEServiceTokenTemplate() {
        return eServiceTokenTemplate;
    }

    @Override
    public String getTCTokenProviderHost() {
        return tcTokenProviderHostname;
    }

    @Override
    public int getTCTokenProviderPort() {
        return tcTokenProviderPort;
    }

    @Override
    public X509Certificate[] getTCTokenProviderCertificate() {
        return tcTokenProviderCertificate;
    }

    @Override
    public PrivateKey getTCTokenProviderPrivateKey() {
        return tcTokenProviderPrivateKey;
    }

    @Override
    public String getTCTokenProviderIndexPage() {
        return tcTokenProviderIndexPageTemplate;
    }

    @Override
    public String getTCTokenProviderTCTokenURL() {
        return tcTokenProviderTokenURL;
    }

    @Override
    public List<String> getTCTokenProviderTLSVersion() {
        return tcTokenProviderTLSVersion;
    }

    @Override
    public List<String> getTCTokenProviderTLSCipherSuites() {
        return tcTokenProviderTLSCipherSuites;
    }

    @Override
    public HashMap<Integer, String[]> getRedirectorsInfoTCToken() {
        return redirectorsInfoTCToken;
    }

    @Override
    public HashMap<Integer, String[]> getRedirectorsInfoRefreshAddress() {
        return redirectorsInfoRefresh;
    }

    @Override
    public String getRedirectorTCTokenHost(Integer number) {
        if ((redirectorTCTokenHosts != null) && (number != null)) {
            String[] params = redirectorTCTokenHosts.get(number);
            if (params != null) {
                return params[0];
            }
        }
        return null;
    }

    @Override
    public String getRedirectorRefreshHost(Integer number) {
        if ((redirectorRefreshHosts != null) && (number != null)) {
            String[] params = redirectorRefreshHosts.get(number);
            if (params != null) {
                return params[0];
            }
        }
        return null;
    }

    @Override
    public String getRedirectorTCTokenPort(Integer number) {
        if ((redirectorTCTokenPorts != null) && (number != null)) {
            String[] params = redirectorTCTokenPorts.get(number);
            if (params != null) {
                return params[0];
            }
        }
        return null;
    }

    @Override
    public String getRedirectorRefreshPort(Integer number) {
        if ((redirectorRefreshPorts != null) && (number != null)) {
            String[] params = redirectorRefreshPorts.get(number);
            if (params != null) {
                return params[0];
            }
        }
        return null;
    }

    @Override
    public String getRedirectorTCTokenURL(Integer number) {
        if ((redirectorTCTokenUrlPaths != null) && (number != null)) {
            String[] params = redirectorTCTokenUrlPaths.get(number);
            if (params != null) {
                return params[0];
            }
        }
        return null;
    }

    @Override
    public String getRedirectorRefreshURL(Integer number) {
        if ((redirectorRefreshUrlPaths != null) && (number != null)) {
            String[] params = redirectorRefreshUrlPaths.get(number);
            if (params != null) {
                return params[0];
            }
        }
        return null;
    }

    @Override
    public String getRedirectorTCTokenCertificate(Integer number) {
        if ((redirectorTCTokenCertificates != null) && (number != null)) {
            String[] params = redirectorTCTokenCertificates.get(number);
            if (params != null) {
                return params[0];
            }
        }
        return null;
    }

    @Override
    public String getRedirectorRefreshCertificate(Integer number) {
        if ((redirectorRefreshCertificates != null) && (number != null)) {
            String[] params = redirectorRefreshCertificates.get(number);
            if (params != null) {
                return params[0];
            }
        }
        return null;
    }

    @Override
    public String getRedirectorTCTokenPrivateKey(Integer number) {
        if ((redirectorTCTokenPrivateKeys != null) && (number != null)) {
            String[] params = redirectorTCTokenPrivateKeys.get(number);
            if (params != null) {
                return params[0];
            }
        }
        return null;
    }

    @Override
    public String getRedirectorRefreshPrivateKey(Integer number) {
        if ((redirectorRefreshPrivateKeys != null) && (number != null)) {
            String[] params = redirectorRefreshPrivateKeys.get(number);
            if (params != null) {
                return params[0];
            }
        }
        return null;
    }

    @Override
    public String getRedirectorTCTokenStatus(Integer number) {
        if ((redirectorTCTokenStatus != null) && (number != null)) {
            String[] params = redirectorTCTokenStatus.get(number);
            if (params != null) {
                return params[0];
            }
        }
        return null;
    }

    @Override
    public String getRedirectorRefreshStatus(Integer number) {
        if ((redirectorRefreshStatus != null) && (number != null)) {
            String[] params = redirectorRefreshStatus.get(number);
            if (params != null) {
                return params[0];
            }
        }
        return null;
    }

    @Override
    public ITestSession getNewSession() {
        TestSession sessionObj = new TestSession(this);
        testSession.put(sessionObj.getSessionID(), sessionObj);
        if (pskCallback != null) {
            try {
                pskCallback.addPSK(sessionObj.getSessionID(), sessionObj.getPSKKey());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionObj;
    }

    @Override
    public ITestSession getSession(String id) {
        return testSession.get(id);
    }

    @Override
    public String getECardInitializeFrameworkTemplate() {
        return initializeFrameworkTemplate;
    }

    @Override
    public String getECardDIDAuthenticate1Template() {
        return didAuthenticate1Template;
    }

    @Override
    public String getECardDIDAuthenticate2Template() {
        return didAuthenticate2Template;
    }

    @Override
    public String getECardDIDAuthenticate3Template() {
        return didAuthenticate3Template;
    }

    @Override
    public List<String> getECardStepTransmitTemplates() {
        return transmitTemplates;
    }

    // @Override
    // public String getECardStep5Template()
    // {
    // return step5Template;
    // }
    @Override
    public String getECardStartPAOSResponseTemplate() {
        return startPAOSResponseTemplate;
    }

    @Override
    public boolean disableHTTP11ConformityTest() {
        return disableHTTP11ConformityTest;
    }

    @Override
    public int chunkedTransfer() {
        return chunkedTransfer;
    }

    @Override
    public void generateNewTestcaseLogfile() {
        copyConfigFileToLogDir(GlobalSettings.getLogSchemaFileName());
        copyConfigFileToLogDir(GlobalSettings.getLogStyleFileName());

        LogFile = new File(testObjLogDir, getTestModuleName() + " " + getTestName() + " "
                + new SimpleDateFormat("YYYY-MM-dd HH-mm-ss-SSS").format(new Date(System.currentTimeMillis())) + ".xml");
    }

    /**
     * Copies file from config to log directory.
     *
     * @param fileName File to copy.
     */
    private void copyConfigFileToLogDir(String fileName) {
        File source = new File(GlobalSettings.getConfigDir(), fileName);
        if (source.exists()) {
            File dest = new File(testObjLogDir, fileName);
            // copy only if not exists
            if (!dest.exists()) {
                try {
                    FileUtils.copyFile(source, dest, true);
                } catch (IOException e) {
                    Logger.Global.logState(e.getMessage(), LogLevel.Error);
                }
            }
        } else {
            Logger.Global.logState(fileName + " not found.", LogLevel.Error);
        }
    }

    @Override
    public String getLogTestcasesFilepath() {
        String result = "";

        // load latest log file if unset.
        if (LogFile == null) {
            if (testObjLogDir.isDirectory()) {
                List<File> logFiles = new ArrayList<>();
                for (File logFile : testObjLogDir.listFiles()) {
                    if (logFile.getName().startsWith(getTestModuleName() + " " + getTestName())
                            && logFile.getName().endsWith(".xml")) {
                        logFiles.add(logFile);
                    }
                }

                if (logFiles.size() > 0) {
                    logFiles.sort(new FileDateComperator());
                    LogFile = logFiles.get(logFiles.size() - 1);
                }
            }
        }

        if (LogFile != null) {
            result = LogFile.getAbsolutePath();
        }

        return result;
    }

    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public ITestProtocolCallback[] getCallbacks() {
        return testProtocolCallbackList.toArray(new ITestProtocolCallback[testProtocolCallbackList.size()]);
    }

    @Override
    public void addTestProtocolCallback(ITestProtocolCallback tpcb) {
        testProtocolCallbackList.add(tpcb);
    }

    @Override
    public void removeTestProtocolCallback(ITestProtocolCallback tpcb) {
        testProtocolCallbackList.remove(tpcb);
    }

    @Override
    public void sendMessageToCallbacks(ITestEvent event, Object data, SourceComponent sourceComponent, Object callingClass) {
        for (ITestProtocolCallback cb : getCallbacks()) {
            cb.testProtocolCallback(event, data, sourceComponent, callingClass);
        }
    }

    @Override
    public String getEServiceIndexPageURL() {
        return eServiceIndexPageURL;
    }

    @Override
    public String getEServiceCommunicationErrorPage() {
        return eServiceCommunicationErrorPageTemplate;
    }

    @Override
    public boolean getUseRawEphemeralPublicKey() {
        return useRawEphemeralPublicKey;
    }

    @Override
    public String getEServiceCommunicationErrorPageURL() {
        return eServiceCommunicationErrorPageURL;
    }

    @Override
    public Type getTestType() {
        return testType;
    }

    @Override
    public String getBrowserSimulatorRmiServerHost() {
        return browserSimulatorRmiHost;
    }

    @Override
    public int getBrowserSimulatorRmiServerPort() {
        return browserSimulatorRmiPort;
    }

    @Override
    public boolean isAutonomic() {
        return autonomic;
    }

    @Override
    public String toString() {
        return getTestName();
    }

    @Override
    public String getNetworkUnreachableHostname() {
        return networkUnreachableHostname;
    }

    @Override
    public String getTestModuleName() {
        return testModuleName;
    }

    public String getTestLocation() {
        return testLocation;
    }

    @Override
    public HashMap<ITestData.ExpectedTestStepKey, String> getExpectedFinalDefaultFlowStep() {
        return expectedFinalDefaultFlowStep;
    }

    @Override
    public List<HashMap<ITestData.ExpectedTestStepKey, String>> getExpectedAdditionalSteps() {
        return expectedAdditionalSteps;
    }

    @Override
    public List<String> getProfiles() {
        return profiles;
    }

    @Override
    public List<String> getEIDServiceTLSVersion() {
        return eidServiceTLSVersion;
    }

    @Override
    public List<String> getEIDServiceTLSCipherSuites() {
        return eidServiceTLSCipherSuites;
    }

    @Override
    public String getEIDServiceTLSExpectedClientVersion() {
        return eidServiceTLSExpectedClientVersion;
    }

    @Override
    public boolean getEIDServiceCheckURI() {
        return eidServiceCheckURI;
    }

    @Override
    public byte[] getEIDServiceCV_CVCA(Integer number) {
        byte[] cert = null;
        if ((eidServiceCerts_CV_CVCA != null) && (number != null)) {
            cert = eidServiceCerts_CV_CVCA.get(number);
        }
        return cert;
    }

    @Override
    public byte[] getEIDServiceCV_DVCA(Integer number) {
        byte[] cert = null;
        if ((eidServiceCerts_CV_DVCA != null) && (number != null)) {
            cert = eidServiceCerts_CV_DVCA.get(number);
        }
        return cert;
    }

    @Override
    public byte[] getEIDServiceCV_TERM(Integer number) {
        byte[] cert = null;
        if ((eidServiceCerts_CV_TERM != null) && (number != null)) {
            cert = eidServiceCerts_CV_TERM.get(number);
        }
        return cert;
    }

    @Override
    public List<String> getEServiceTLSVersion() {
        return eServiceTLSVersion;
    }

    @Override
    public List<String> getEServiceTLSCipherSuites() {
        return eServiceTLSCipherSuites;
    }

    @Override
    public String getEServiceTLSdhParameters() {
        return eServiceTLSdhParameters;
    }

    @Override
    public String getEServiceTLSExpectedClientVersion() {
        return eServiceTLSExpectedClientVersion;
    }

    @Override
    public String getEServiceTLSSignatureAlgorithm() {
        return eServiceTLSSignatureAlgorithm;
    }

    @Override
    public String getEServiceTLSecCurve() {
        return eServiceTLSecCurve;
    }

    @Override
    public String getEServiceRedirectURL() {
        return eServiceRedirectURL;
    }

    @Override
    public Integer getEServiceRedirectorTCTokenNumber() {
        Integer number = null;

        String value = getPropertyValue(c_eServiceeRedirectorTCToken);
        if (value != null) {
            number = Integer.parseInt(CommonUtil.getSubstringBefore(CommonUtil.getSubstringAfter(value, ".", true), "}", true));
        }
        return number;
    }

    @Override
    public String getEServiceRedirectLocation() {
        return eServiceRedirectLocation;
    }

    @Override
    public boolean isFailOnXMLEvaluationError() {
        return failOnXMLEvaluationError;
    }

    @Override
    public boolean isLegacyActivation() {
        return legacyActivation;
    }

    private Map<String, EvaluationConfig> loadEvalConfig(final String key, final String initialNode) {
        try {
            Properties evalProp = new Properties();
            evalProp.load(new StringReader(getStringFileByPropertyKey(key)));
            return Evaluator.createEvaluationSetup(this, evalProp, initialNode);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public Map<String, EvaluationConfig> getStartPAOSEvaluationConfig() {
        if (startPaosEvalCfg == null) {
            startPaosEvalCfg = loadEvalConfig(c_eval_startPaosEvalCfg, "StartPAOS");
        }
        return startPaosEvalCfg;
    }

    @Override
    public Map<String, EvaluationConfig> getAuth1EvaluationConfig() {
        if (auth1EvalCfg == null) {
            auth1EvalCfg = loadEvalConfig(c_eval_auth1EvalCfg, "DIDAuthenticateResponse");
        }
        return auth1EvalCfg;
    }

    @Override
    public Map<String, EvaluationConfig> getAuth2EvaluationConfig() {
        if (auth2EvalCfg == null) {
            auth2EvalCfg = loadEvalConfig(c_eval_auth2EvalCfg, "DIDAuthenticateResponse");
        }
        return auth2EvalCfg;
    }

    @Override
    public Map<String, EvaluationConfig> getAuth3EvaluationConfig() {
        if (auth3EvalCfg == null) {
            auth3EvalCfg = loadEvalConfig(c_eval_auth3EvalCfg, "DIDAuthenticateResponse");
        }
        return auth3EvalCfg;
    }

    @Override
    public Map<String, EvaluationConfig> getInitFrameworkEvaluationConfig() {
        if (initFwkEvalCfg == null) {
            initFwkEvalCfg = loadEvalConfig(c_eval_initFwkEvalCfg, "InitializeFrameworkResponse");
        }
        return initFwkEvalCfg;
    }

    @Override
    public Map<String, EvaluationConfig> getTransmitEvaluationConfig() {
        if (transmitEvalCfg == null) {
            transmitEvalCfg = loadEvalConfig(c_eval_transmitEvalCfg, "TransmitResponse");
        }
        return transmitEvalCfg;
    }

    @Override
    public Date getSimulatedCardDate() {
        return cardDate;
    }

    @Override
    public byte[] getEFCardAccessFile() {
        return efCardAccessFile;
    }

    @Override
    public byte[] getEFCardSecurityFile() {
        return efCardSecurityFile;
    }

    @Override
    public int getDefaultCAKeyID() {
        return defaultCAKeyID;
    }

    @Override
    public byte[] getESignDST() {
        return esignDST;
    }

    @Override
    public String getECardErrorStartPaosResponseTemplate() {
        return errorStartPaosResponse;
    }

    // FIXME remove this once the connection handling is fixed (JIRA EIDCLIENTC-69)
    @Override
    public boolean tcTokenDisconnect() {
        return !tcTokenDisconnect;
    }

    @Override
    public boolean useModifiedSSL() {
        return useModifiedSSL;
    }

    public String getDefaultConfigPath() {
        return testConfigBasePath;
    }

    public String getRelativeTestObjectFolder() {
        return relativeTestObjectFolder;
    }

    @Override
    public int getPaosRequestResponseCode() {
        return eidServicePAOSresponse;
    }

    @Override
    public boolean getResultIndeterminable() {
        return resultIndeterminable;
    }

    @Override
    public String getResultIndeterminableReason() {
        return resultIndeterminableReason;
    }

    @Override
    public boolean isEac1ConfirmDialog() {
        return isEac1ConfirmDialog;
    }

    @Override
    public List<String> getAllowedResultMinorErrors() {
        return allowedResultMinorErrors;
    }

    @Override
    public String getCommErrorAddressServerHost() {
        return commErrorAddressServerHostname;
    }

    @Override
    public int getCommErrorAddressServerPort() {
        return commErrorAddressServerPort;
    }

    @Override
    public String getCommErrorAddressServerCommunicationErrorPage() {
        return commErrorAddressServerIndexPageTemplate;
    }

    @Override
    public String getCommErrorAddressServerCommunicationErrorPageURL() {
        return commErrorAddressServerCommunicationErrorPageURL;
    }

    @Override
    public X509Certificate[] getCommErrorAddressServerCertificate() {
        return commErrorAddressServerCertificate;
    }

    @Override
    public PrivateKey getCommErrorAddressServerPrivateKey() {
        return commErrorAddressServerPrivateKey;
    }

    @Override
    public String getCommErrorAddressServerIndexPage() {
        return commErrorAddressServerIndexPageTemplate;
    }

    @Override
    public List<String> getCommErrorAddressServerTLSVersion() {
        return commErrorAddressServerTLSVersion;
    }

    @Override
    public List<String> getCommErrorAddressServerTLSCipherSuites() {
        return commErrorAddressServerTLSCipherSuites;
    }

    @Override
    public boolean isCardSimulationResetAtStart() {
        return cardSimulationResetAtStart;
    }

    @Override
    public String getCardSimulationConfigurationIdentifier() {
        return cardSimulationConfigurationIdentifier;
    }

}
