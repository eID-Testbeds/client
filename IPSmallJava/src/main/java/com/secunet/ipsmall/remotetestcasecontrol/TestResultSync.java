package com.secunet.ipsmall.remotetestcasecontrol;

import com.secunet.ipsmall.test.ITestProtocolCallback;

/**
 * Stores test result in synchronizable object
 */
public class TestResultSync {
    
    ITestProtocolCallback.TestResult result = null;
    
    public void notifyResult(ITestProtocolCallback.TestResult result) {
        synchronized (this) {
            this.result = result;
            this.notify();
        }
    }
    
    public ITestProtocolCallback.TestResult waitForResult() {
        ITestProtocolCallback.TestResult tmpResult = null;
        synchronized (this) {
            try {
                this.wait();
                tmpResult = this.result;
                this.result = null;
            } catch (InterruptedException ex) {
            }
        }
        
        return tmpResult;
    }
}
