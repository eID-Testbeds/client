package com.secunet.ipsmall;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.secunet.ipsmall.log.IModuleLogger.EnvironmentClassification;
import com.secunet.ipsmall.log.IModuleLogger.LogLevel;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.rmi.BrowserSimulatorRmiClient;
import com.secunet.ipsmall.test.FileBasedTestData;
import com.secunet.ipsmall.test.ITestData;
import com.secunet.ipsmall.test.ITestData.Type;
import com.secunet.ipsmall.test.TestRunner;

public class IPSmallTester {
    
    private final EIDServer eidServer;
    private final EService eService;
    private final AttachedEIDServer attachedEidServer;
    private CommErrorAddressServer commErrorAddressServer;
    private BrowserSimulatorRmiClient browserSimulatorRmiClient;
    private final ITestData testData;
    private TestRunner testRunner;
    private final List<Redirector> redirectors = new ArrayList<Redirector>();
    private TCTokenProvider tcTokenProvider;
    
    public IPSmallTester(FileBasedTestData fileBasedTestData) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        if (!fileBasedTestData.isLoaded()) {
            fileBasedTestData.load();
        }
        
        testData = fileBasedTestData;
        
        if(fileBasedTestData.isEIDServiceAttached()) {
            attachedEidServer = new AttachedEIDServer(fileBasedTestData);
            eidServer = null;
            eService = null;
        }
        else {
            eidServer = new EIDServer(fileBasedTestData);
            fileBasedTestData.setPSKCallback(eidServer);
            eService = new EService(fileBasedTestData);
            attachedEidServer = null;
        }
        
        // communication error Address server now separated from eService, see EIDCLIENTC-239 
        if ((testData.getCommErrorAddressServerHost() != null) && (testData.getCommErrorAddressServerHost().length() > 0)) {
            commErrorAddressServer = new CommErrorAddressServer(fileBasedTestData);
        }
        
        if ((testData.getTCTokenProviderHost() != null) && (testData.getTCTokenProviderHost().length() > 0)) {
            tcTokenProvider = new TCTokenProvider(fileBasedTestData);
        }
        
        HashMap<Integer, String[]> redirectorInfoTCToken = fileBasedTestData.getRedirectorsInfoTCToken();
        if (redirectorInfoTCToken != null) {
            for (Map.Entry<Integer, String[]> entry : redirectorInfoTCToken.entrySet()) {
                // String key = entry.getKey().toString();
                String[] values = entry.getValue();
                Redirector redirector = new Redirector(testData, Redirector.Type.TCToken, values);
                redirectors.add(redirector);
            }
        }
        
        HashMap<Integer, String[]> redirectorInfoRefresh = fileBasedTestData.getRedirectorsInfoRefreshAddress();
        if (redirectorInfoRefresh != null) {
            for (Map.Entry<Integer, String[]> entry : redirectorInfoRefresh.entrySet()) {
                // String key = entry.getKey().toString();
                String[] values = entry.getValue();
                Redirector redirector = new Redirector(testData, Redirector.Type.RefreshAddress, values);
                redirectors.add(redirector);
            }
        }
        
        Logger.TestRunner.logState("Initialized Testcase:\t" + fileBasedTestData.getTestName());
        Logger.TestRunner.logEnvironment(EnvironmentClassification.TestCaseConfig, "Description: " + fileBasedTestData.getTestDescription());
        Logger.TestRunner.logEnvironment(EnvironmentClassification.TestCaseConfig, "Parameters:\r\n" + fileBasedTestData.serialize());
    }
    
    public void start() throws Exception {
        if (testData.getTestType() == Type.BROWSERSIMULATOR) {
            Logger.BrowserSim.logState("Starting connection to browser simulator...");
            browserSimulatorRmiClient = new BrowserSimulatorRmiClient(testData);
            Logger.BrowserSim.logState("connection established");
        }
        
        if( eidServer != null ){
            eidServer.start();
        }
        if( eService != null ){
            eService.start();
        }
        if( attachedEidServer != null ){
            attachedEidServer.start();
        }
        if( commErrorAddressServer != null ){
        	commErrorAddressServer.start();
        }
        if (tcTokenProvider != null) {
            tcTokenProvider.start();
        }
        
        if (redirectors.size() > 0) {
            for (int i = 0; i < redirectors.size(); i++) {
                Redirector redirector = redirectors.get(i);
                Logger.Redirector.logState(redirectors.size() + " redirector(s) started.", LogLevel.Debug);
                redirector.start();
            }
            Logger.Redirector.logState(redirectors.size() + " redirector(s) started.");
        }
        
        testRunner = new TestRunner(testData);
        testData.addTestProtocolCallback(testRunner);
        Logger.TestRunner.logState("TestRunner started.", LogLevel.Debug);
        
        Logger.TestRunner.logState("Testcase " + testData.getTestName() + " server started.", LogLevel.Debug);
        
        // if this is an automated browser-simulator test, don't wait for any
        // user-action
        // but connect to the RMI-server of the browser-simulator and trigger
        // the testcase.
        // Note: this COULD be improved with additional settings, e.g. ManagerUI
        // options for
        // deciding whether to auto-start tests or not
        if (testData.getTestType() == Type.BROWSERSIMULATOR) {
            
            // String testURL = "https://ausweisapp-idp-test.secunet.de/";
            
            // we need to parse that form the index-page-html-snippet, cause it
            // might contain tc_token_redirects instead of the tc_token url
            String clientURL = parseClientURL();
            
            Logger.BrowserSim.logState("Trying to trigger browsersimulator: " + clientURL);
//            browserSimulatorRmiClient.sendHttpRequest(clientURL, testData.getEServiceCertificate(), false);
            browserSimulatorRmiClient.sendHttpRequest(clientURL, null, false);
            Logger.BrowserSim.logState("Triggered: " + clientURL);
        }
        
    }
    
    /**
     * removes testrunner as listener from testdata, stops all server, redirectors and services and disconnects the rmi browsersimulator connection.
     */
    public void stop() {
        testData.removeTestProtocolCallback(testRunner);
        if( eidServer != null ){
            eidServer.stop();
        }
        if( eService != null ){
            eService.stop();
        }
        if( attachedEidServer != null ){
            attachedEidServer.stop();
        }
        if( commErrorAddressServer != null ){
        	commErrorAddressServer.stop();
        }
        if (tcTokenProvider != null) {
            tcTokenProvider.stop();
        }
        for (int i = 0; i < redirectors.size(); i++) {
            redirectors.get(i).stop();
        }
        if (browserSimulatorRmiClient != null) {
            browserSimulatorRmiClient.stop();
        }
    }
    
    private String parseClientURL() {
        String indexPage = testData.getEServiceIndexPage();
        if ((testData.getTCTokenProviderHost() != null) && (testData.getTCTokenProviderHost().length() > 0)) {
            indexPage = testData.getTCTokenProviderIndexPage();
        }
        int startIndex = indexPage.indexOf(testData.getClientURL());
        int endIndex = indexPage.indexOf(">", startIndex) - 1; // -1 for ">
        if (startIndex < 0 || endIndex < -1 || endIndex >= indexPage.length()) {
            Logger.TestRunner.logState("Unable to parse URL form eService index page!", LogLevel.Error);
            return "";
        }
        indexPage = indexPage.substring(startIndex, endIndex); // second substring to get first > after url!
        return indexPage;
    }
    
}
