package com.secunet.ipsmall.test;

import com.secunet.ipsmall.log.IModuleLogger.ConformityResult;
import com.secunet.ipsmall.log.IModuleLogger.LogLevel;
import com.secunet.ipsmall.log.Logger;

/**
 * Represents a test result warning, if test result is undetermined.
 */
public class TestResultWarning {
	private String message;
	
	/**
	 * Creates a new test result warning.
	 * 
	 * @param message Warning message.
	 */
	public TestResultWarning(String message) {
		this.message = message;
		Logger.TestRunner.logConformity(ConformityResult.undetermined, message, LogLevel.Warn);
	}

	/**
	 * Gets message of test result warning
	 * @return The message.
	 */
	public String getMessage() {
		return message;
	}
}
