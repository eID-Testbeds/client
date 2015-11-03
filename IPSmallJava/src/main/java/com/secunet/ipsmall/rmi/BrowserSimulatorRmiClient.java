package com.secunet.ipsmall.rmi;

import com.secunet.ipsmall.GlobalInfo;
import com.secunet.ipsmall.IPSmallManager;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.security.cert.X509Certificate;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import com.secunet.ipsmall.log.IModuleLogger.ConformityResult;
import com.secunet.ipsmall.log.IModuleLogger.LogLevel;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.test.ITestData;
import com.secunet.ipsmall.test.ITestProtocolCallback.SourceComponent;
import com.secunet.ipsmall.test.ITestProtocolCallback.TestError;
import com.secunet.ipsmall.test.ITestProtocolCallback.TestStep;
import com.secunet.ipsmall.tobuilder.ics.TR031242ICS;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BrowserSimulatorRmiClient {
    
    private IBrowserSimulator browserSimulator;
    private final ExecutorService executors;
    private final ITestData testData;
    
    public BrowserSimulatorRmiClient(ITestData testData) throws Exception {
        
        // instead of default timeout (15000, 15sec) use 5 sec
        // System.setProperty("sun.rmi.transport.connectionTimeout", "5000");
        
        // browserSimulator = (IBrowserSimulator) Naming.lookup(
        // "rmi://192.168.134.128:1099/"+ IBrowserSimulator.RMI_SERVICE_NAME );
        // browserSimulator = (IBrowserSimulator) Naming.lookup( "rmi://" +
        // "localhost" + ":" + testData.getBrowserSimulatorRmiServerPort() + "/"
        // + IBrowserSimulator.RMI_SERVICE_NAME );
        browserSimulator = (IBrowserSimulator) Naming.lookup("rmi://" + testData.getBrowserSimulatorRmiServerHost() + ":"
                + testData.getBrowserSimulatorRmiServerPort() + "/" + IBrowserSimulator.RMI_SERVICE_NAME);
        
        executors = Executors.newCachedThreadPool();
        
        this.testData = testData;
    }
    
    /**
     * get the RMI proxy to send commands to
     * 
     * @return
     */
    private IBrowserSimulator getBrowserSimulator() {
        return browserSimulator;
    }
    
    public void sendHttpRequest(String url, X509Certificate[] trustedCerts, boolean followRedirects) {
        sendAsyncHttpRequest(url, trustedCerts, followRedirects);
    }
    
    /**
     * Initial http request method for testing. However, sync request might block testbed
     * 
     * @param url
     */
    @SuppressWarnings("unused")
    private void sendSyncHttpRequest(String url, X509Certificate[] trustedCerts, boolean followRedirects) {
        try {
            RmiHttpResponse response = getBrowserSimulator().sendHttpRequest(url, trustedCerts, followRedirects);
            onHttpResponse(response);
        } catch (Exception e) {
            try {
                onHttpException(e);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        }
    }
    
    /**
     * Send async http request for not blocking testbed and/or UI.
     * 
     * @param url
     */
    private void sendAsyncHttpRequest(final String url, final X509Certificate[] trustedCerts, final boolean followRedirects) {
        /*
        Runnable runnable = new Runnable() {
        	@Override
        	public void run() {
        		try {
        			getBrowserSimulator().sendHttpRequest(url);
        		} catch (RemoteException e) {
        			e.printStackTrace();
        		}
        	}
        };
        executors.execute(runnable);
        */
        
        Callable<Void> callable = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    RmiHttpResponse response = getBrowserSimulator().sendHttpRequest(url, trustedCerts, followRedirects);
                    checkResponseHeaders(url, response);
                    onHttpResponse(response);
                } catch (Exception e) {
                    try {
                        onHttpException(e);
                    } catch (RemoteException e1) {
                        Logger.BrowserSim.logState("Error handlying async HttpRequest Exception: " + e1.getMessage(), LogLevel.Error);
                    }
                }
                return null;
            }
        };
        try {
            @SuppressWarnings("unused")
            Future<Void> future = executors.submit(callable);
        } catch (RejectedExecutionException ignore) {
            Logger.BrowserSim.logState("Rejected http request: " + url, LogLevel.Error);
        }
    }
    
    /**
     * What to do if an http response comes back from browsersimulator. Logging only until now. TODO Analyze result as soon as TestRunner/ expectedResults are
     * available.
     * 
     * @param response
     * @throws RemoteException
     */
    private void onHttpResponse(RmiHttpResponse response) throws RemoteException {
        response.log();
        
        if (isRedirect(response)) {
            testData.sendMessageToCallbacks(TestStep.REDIRECT_BROWSER, response, SourceComponent.BROWSER_SIMULATOR, this);
        } else {
            testData.sendMessageToCallbacks(TestStep.BROWSER_CONTENT, response, SourceComponent.BROWSER_SIMULATOR, this);
        }
        
    }
    
    /**
     * Check response headers and log a conformity warning if necessary
     * 
     * @param url
     * @param response
     */
    private void checkResponseHeaders(final String url, final RmiHttpResponse response) {
        String headerServerValue = response.headers.get("Server");
        
        LogLevel failLevel = LogLevel.Warn; // only warning ...
        
        // get expected name and version from ics
        TR031242ICS ics = IPSmallManager.getInstance().getIcs();
        String name = null;
        String version = null;
        String trversion = GlobalInfo.TRVersion.getValue();
        if (ics != null) {
            TR031242ICS.SoftwareVersion swVersion = ics.getSoftwareVersion();
            if (swVersion != null) {
                name = swVersion.getName();
                if (swVersion.getVersionMajor() != null && swVersion.getVersionMinor() != null && swVersion.getVersionSubminor() != null) {
                    if (!swVersion.getVersionMajor().isEmpty()) {
                        version = swVersion.getVersionMajor();
                        if (!swVersion.getVersionMinor().isEmpty()) {
                            version += "." + swVersion.getVersionMinor();
                            if (!swVersion.getVersionSubminor().isEmpty()) {
                                version += "." + swVersion.getVersionSubminor();
                            }
                        }
                    }
                }
            }
        }
        
        if (name == null) {
            Logger.BrowserSim.logState("Unable read expected server name from ICS.", LogLevel.Error);
            return;
        }
        
        if (version == null) {
            Logger.BrowserSim.logState("Unable read expected server version from ICS.", LogLevel.Error);
            return;
        }
        
        if (trversion == null) {
            Logger.BrowserSim.logState("Unable read expected TR from config.", LogLevel.Error);
            return;
        }
                
        boolean isStatusAction = false;
        if (url.contains("?Status")) {
            isStatusAction = true;
        }
        
        if ((headerServerValue != null) && !headerServerValue.isEmpty()) {
            // get server token
            Pattern serverTokenPattern = Pattern.compile("(.*)/(.*) ?\\((.*)\\)");
            Matcher serverTokenMatcher = serverTokenPattern.matcher(headerServerValue);

            boolean foundName = false; 
            while (serverTokenMatcher.find()) {
                // check name
                String serverName = serverTokenMatcher.group(1).trim();
                String serverVersion = serverTokenMatcher.group(2).trim();
                String serverComments = serverTokenMatcher.group(3).trim();
                if (name.equals(serverName)) {
                    foundName = true;

                    // check version
                    if (!version.equals(serverVersion)) {
                        Logger.BrowserSim.logConformity(
                            ConformityResult.failed,
                            "Invalid version in Server header.",
                            failLevel);
                    }

                    // get comments as list
                    List<String> comments = new ArrayList<>();
                    Pattern commentsPattern = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");
                    Matcher commentsMatcher = commentsPattern.matcher(serverComments);
                    while (commentsMatcher.find()) {
                        comments.add(commentsMatcher.group(1));
                    }
                    
                    // check tr version(s) in comments
                    boolean foundTRVersion = false;
                    for (String comment : comments) {
                        if (trversion.equals(comment)) {
                            foundTRVersion = true;
                            break;
                        }
                    }
                    
                    if (!foundTRVersion) {
                        Logger.BrowserSim.logConformity(
                            ConformityResult.failed,
                            "Found no valid TR version in Server header.",
                            failLevel);
                    }

                    break;
                }

                // check version
            }
                
            if (!foundName) {
                Logger.BrowserSim.logConformity(
                    ConformityResult.failed,
                    "Found no valid Server header for " + name + ".",
                    failLevel);
            }
        } else {
            if (isStatusAction) {
                Logger.BrowserSim.logConformity(ConformityResult.failed, "Mandatory response header 'Server' is missing!", LogLevel.Warn);
            } else {
                Logger.BrowserSim.logConformity(ConformityResult.passed, "Response header 'Server' is missing.", LogLevel.Info);
            }
        }
        
        if ((!isStatusAction) && (url.contains("?Access-Control-Allow-Origin"))) {
            Logger.BrowserSim.logConformity(ConformityResult.failed,
                    "Only Status actions are allowed to contain response header 'Access-Control-Allow-Origin'!", LogLevel.Warn);
        }
    }
    
    private boolean isRedirect(RmiHttpResponse response) {
        // redirects have 3xx status code
        return response.statusCode / 100 == 3;
    }
    
    /**
     * What to do if an exception comes back from browsersimulator. Logging only until now. TODO Analyze result as soon as TestRunner/ expectedResults are
     * available.
     * 
     * @param response
     * @throws RemoteException
     */
    private void onHttpException(Exception e) throws RemoteException {
        Logger.BrowserSim.logException(e);
        testData.sendMessageToCallbacks(TestError.BrowserSimulator, e.getMessage(), SourceComponent.BROWSER_SIMULATOR, this);
    }
    
    public void stop() {
        browserSimulator = null;
        executors.shutdown();
    }
    
}
