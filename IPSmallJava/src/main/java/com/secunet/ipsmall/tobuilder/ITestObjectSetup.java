package com.secunet.ipsmall.tobuilder;

/**
 * Interface for test object setup, which sets up testcase modifications.
 *
 */
public interface ITestObjectSetup {
	
	/**
	 * Performs setup.
	 * 
	 * @throws Exception Exception if setup could not performed.
	 */
	public void runSetup() throws Exception;
	
	/**
	 * Indicates if setup was performed.
	 * 
	 * @return True if setup was performed.
	 */
	public boolean IsSetUp();
}
