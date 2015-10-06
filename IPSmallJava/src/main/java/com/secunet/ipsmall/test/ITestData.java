package com.secunet.ipsmall.test;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

import com.secunet.bouncycastle.crypto.tls.Certificate;
import com.secunet.ipsmall.eval.EvaluationConfig;
import com.secunet.ipsmall.test.ITestProtocolCallback.ITestEvent;
import com.secunet.ipsmall.test.ITestProtocolCallback.SourceComponent;

public interface ITestData {
    
    public enum Type {
        BROWSER,
        BROWSERSIMULATOR
    }
    
    public enum ExpectedTestStepKey {
        STEP,
        /** absolute/complete URL, e.g. protocol://host:port/urlPath?urlParams */
        URL,
        URL_PATH,
        URL_PARAMS,
        STATUS_CODES
    }
    
    /**
     * Allowed protocols as defined in config.properties for *.tls.version.
     */
    public enum PROTOCOLS {
        sslv2,
        sslv3,
        tls10,
        tls11,
        tls12
    }
    
    public String getValue(String key);
    
    /**
     * TestCase Stuff
     */
    public String getTestName();
    
    public String getTestModuleName();
    
    public boolean getTestLoad();
    
    public boolean getTestEnabled();
    
    public boolean getTestManualResult();
    
    public String getTestDescription();
    
    public String getTestReference();
    
    public Type getTestType();
    
    public List<String> getTestMessagesBegin();
    
    public List<String> getTestMessagesEnd();
    
    /**
     * 
     * Client Methods
     */
    public String getClientURL();
    
    /**
     * 
     * EID Service Methods
     */
    public String getEIDServiceHost();
    
    public int getEIDServicePort();
    
    public Certificate getEIDServiceCertificate();
    
    public AsymmetricKeyParameter getEIDServerPrivateKey();
    
    public boolean useEIDServiceTLSPSK();
    
    public boolean isEIDServiceAttached();
    
    public boolean eIDServiceAccpetNonConformHTTP11Message();
    
    public byte[] getEIDServiceCV_CVCA();
    
    public byte[] getEIDServiceCV_DVCA();
    
    public byte[] getEIDServiceCV_TERM();
    
    public byte[] getEIDServiceCV_TERM2();
    
    public byte[] getEIDServiceCV_TERM_KEY();
    
    public byte[] getEIDServiceCV_TERM_SECTOR();
    
    public byte[] getEIDServiceCertificateDescription();
    
    public byte[] getEIDServiceChatRequired();
    
    public byte[] getEIDServiceChatOptional();
    
    public byte[] getEIDServiceAuxData();
    
    public byte[] getSimulatedCard_Trustpoint1();
    
    public byte[] getSimulatedCard_Trustpoint2();
    
    public void setPSKCallback(IPublishPSK pskCallback);
    
    public List<String> getEIDServiceTLSVersion();
    
    public List<String> getEIDServiceTLSCipherSuites();
    
    public String getEIDServiceTLSExpectedClientVersion();

    public boolean getEIDServiceCheckURI();
    
    public byte[] getEIDServiceCV_CVCA(Integer number);
    
    public byte[] getEIDServiceCV_DVCA(Integer number);
    
    public byte[] getEIDServiceCV_TERM(Integer number);
    
    /**
     * 
     * EService Methods
     */
    public String getEServiceHost();
    
    public int getEServicePort();
    
    public Certificate getEServiceCertificate();
    
    public AsymmetricKeyParameter getEServerPrivateKey();
    
    public String getEServiceIndexPage();
    
    public String getEServiceIndexPageURL();
    
    public String getEServiceRefreshPage();
    
    public String getEServiceRefreshPageURL();
    
    public String getEServiceCommunicationErrorPage();
    
    public String getEServiceCommunicationErrorPageURL();
    
    public String getEServiceTCTokenURL();
    
    public String getEServiceTokenTemplate();
    
    public List<String> getEServiceTLSVersion();
    
    public List<String> getEServiceTLSCipherSuites();
    
    public String getEServiceTLSdhParameters();

    public String getEServiceTLSExpectedClientVersion();
    
    public String getEServiceTLSSignatureAlgorithm();

    public String getEServiceTLSecCurve();

    public String getEServiceRedirectURL();
    
    public String getEServiceRedirectLocation();
    
    public Integer getEServiceRedirectorTCTokenNumber();
    
    /**
     * 
     * TCTokenProvider Methods
     */
    public String getTCTokenProviderHost();
    
    public int getTCTokenProviderPort();
    
    public X509Certificate[] getTCTokenProviderCertificate();
    
    public PrivateKey getTCTokenProviderPrivateKey();
    
    public String getTCTokenProviderIndexPage();
    
    public String getTCTokenProviderTCTokenURL();
    
    public List<String> getTCTokenProviderTLSVersion();
    
    public List<String> getTCTokenProviderTLSCipherSuites();
    
    
    // Communication Error Address Server methods:
    public String getCommErrorAddressServerHost();
    
    public int getCommErrorAddressServerPort();
    
    public String getCommErrorAddressServerCommunicationErrorPage();
    
    public String getCommErrorAddressServerCommunicationErrorPageURL();
    
    public X509Certificate[] getCommErrorAddressServerCertificate();
    
    public PrivateKey getCommErrorAddressServerPrivateKey();
    
    public String getCommErrorAddressServerIndexPage();
    
    public List<String> getCommErrorAddressServerTLSVersion();
    
    public List<String> getCommErrorAddressServerTLSCipherSuites();
    
    
    
    /**
     * Redirector Methods
     */
    
    /**
     * TCToken redirects can be used e.g. for SAML requests.
     * 
     * Redirects are defined in tupels (from,to,certificate,private_key,status_code) in .properties. <br/>
     * from is the redirect server URL <br/>
     * to is the target redirect URL <br/>
     * certificate is the SSL certificate of the redirector <br/>
     * private_key is the private key of the redirector used for SSL connections <br/>
     * status_code is the redirection HTTP Status Code <br/>
     * 
     * @return Collection of redirect information or null if no redirects are necessary
     */
    public HashMap<Integer, String[]> getRedirectorsInfoTCToken();
    
    /**
     * Same schema as for {@link #getRedirectorsInfoTCToken()}, but e.g. for SAML responses.
     * 
     * @return Collection of redirect information or null if no redirects are necessary
     */
    public HashMap<Integer, String[]> getRedirectorsInfoRefreshAddress();
    
    public String getRedirectorTCTokenHost(Integer number);
    
    public String getRedirectorRefreshHost(Integer number);
    
    public String getRedirectorTCTokenPort(Integer number);
    
    public String getRedirectorRefreshPort(Integer number);
    
    public String getRedirectorTCTokenURL(Integer number);
    
    public String getRedirectorRefreshURL(Integer number);
    
    public String getRedirectorTCTokenCertificate(Integer number);
    
    public String getRedirectorRefreshCertificate(Integer number);
    
    public String getRedirectorTCTokenPrivateKey(Integer number);
    
    public String getRedirectorRefreshPrivateKey(Integer number);
    
    public String getRedirectorTCTokenStatus(Integer number);
    
    public String getRedirectorRefreshStatus(Integer number);
    
    /**
     * Read a private key from a file in test object-specific data directory
     * 
     * @param fileName
     *            File name
     * @return Private key
     * @throws Exception
     *             in error case
     */
    public PrivateKey readPrivateKey(String fileName) throws Exception;
    
    /**
     * Read a X.509certificate from a file in test object-specific data directory
     * 
     * @param fileName
     *            File name
     * @return Certificate
     * @throws Exception
     *             in error case
     */
    public X509Certificate readCertificate(String fileName) throws Exception;
    
    public ITestSession getNewSession();
    
    public ITestSession getSession(String id);
    
    public String getNetworkUnreachableHostname();
    
    public String getECardDIDAuthenticate3Template();
    
    public List<String> getECardStepTransmitTemplates();
    
    // public String getECardStep5Template();
    public String getECardStartPAOSResponseTemplate();
    
    public String getECardInitializeFrameworkTemplate();
    
    public String getECardDIDAuthenticate1Template();
    
    public String getECardDIDAuthenticate2Template();
    
    /** message from the xml file to be send on error with client */
    public String getECardErrorStartPaosResponseTemplate();
    
    public boolean getUseRawEphemeralPublicKey();
    
    // browsersimulator related data (used only if ITestData.Type ==
    // BROWSERSIMULATOR)
    public String getBrowserSimulatorRmiServerHost();
    
    public int getBrowserSimulatorRmiServerPort();
    
    // card parameters
    public int getDefaultCAKeyID();
    
    public byte[] getEFCardAccessFile();
    
    public byte[] getEFCardSecurityFile();
    
    public byte[] getESignDST();
    
    // card simulation data
    public Date getSimulatedCardDate();
    
    // log data
    public String getLogTestcasesFilepath();
    
    public ITestProtocolCallback[] getCallbacks();
    
    public void addTestProtocolCallback(ITestProtocolCallback tpcb);
    
    public void removeTestProtocolCallback(ITestProtocolCallback tpcb);
    
    public void sendMessageToCallbacks(ITestEvent event, Object data, SourceComponent sourceComponent, Object source);
    
    public boolean isAutonomic();
    
    public void generateNewTestcaseLogfile();
    
    public List<String> getAllowedResultMinorErrors();
    
    public HashMap<ITestData.ExpectedTestStepKey, String> getExpectedFinalDefaultFlowStep();
    
    public List<HashMap<ITestData.ExpectedTestStepKey, String>> getExpectedAdditionalSteps();
    
    public List<String> getProfiles();
    
    public boolean isFailOnXMLEvaluationError();
    
    public boolean isLegacyActivation();
    
    // evaluation of the messages
    public Map<String, EvaluationConfig> getStartPAOSEvaluationConfig();
    
    public Map<String, EvaluationConfig> getAuth1EvaluationConfig();
    
    public Map<String, EvaluationConfig> getAuth2EvaluationConfig();
    
    public Map<String, EvaluationConfig> getAuth3EvaluationConfig();
    
    public Map<String, EvaluationConfig> getInitFrameworkEvaluationConfig();
    
    public Map<String, EvaluationConfig> getTransmitEvaluationConfig();
    
    /*
     * HTTP/1.1 conformity methods
     */
    public boolean disableHTTP11ConformityTest();
    
    /*
     * Chunked transfer enablement
     */
    public int chunkedTransfer();
    
    /*
     * Use the modified OpenSSL implementation to send an invalid curve
     */
    public boolean useModifiedSSL();
    
    /**
     * The HTTP response that will be sent to the client for PAOS requests
     */
    public int getPaosRequestResponseCode();
    
    // FIXME remove this once the connection handling is fixed (JIRA EIDCLIENTC-69)
    public boolean tcTokenDisconnect();
    
    /**
     * Get flag if result is indeterminable (if automatic result was positive).
     */
    public boolean getResultIndeterminable();
    
    /**
     * Get reason why state is indeterminable.
     */
    public String getResultIndeterminableReason();
    
    /**
     * Whether to show a confirm dialog after EAC1 step in order to block
     * test run until user confirms. This gives time to abort online-authentication.
     * 
     * See config.properties, testcase.eac1.confirm=false
     */
    public boolean isEac1ConfirmDialog();
    
    /**
     * This is true if the remote card simulation should be reset at start of a test case.
     */
    public boolean isCardSimulationResetAtStart();

    /**
     * Get the configurationIdentifier to indicate to the remote card simulation which card should be simulated.
     */
    public String getCardSimulationConfigurationIdentifier();
}
