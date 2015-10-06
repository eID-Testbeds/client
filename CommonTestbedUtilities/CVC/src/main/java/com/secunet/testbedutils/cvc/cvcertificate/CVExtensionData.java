package com.secunet.testbedutils.cvc.cvcertificate;



/**
 * @brief This structure is an abstract data description of the extension
 * @author meier.marcus
 * @version 1.0
 * @created 27-Aug-2009 14:18:45
 */
public class CVExtensionData {

	/**
	 * < defines the extension content
	 */
	private CVExtensionType type;
	/**
	 * < hash of the extension description or first hash of the sector id key
	 */
	private DataBuffer hash1 = null;
	/**
	 * < second hash of the sector id key
	 */
	private DataBuffer hash2 = null;

	/**
	 * @brief constructor
	 *
	 */
	public CVExtensionData(){}

	/**
	 * @brief This method returns the stored type 
	 * 
	 * @return returns the type
	 */
	public CVExtensionType getType()
	{
		return type;
	}

	/**
	 * 
	 * @param newVal consigns the extension type
	 */
	public void setType(CVExtensionType newVal){
		type = newVal;
	}

	/**
	 * 
	 * 
	 * @return returns the first hash
	 */
	public DataBuffer getHash1(){
		return hash1;
	}

	/**
	 * 
	 * @param newVal sets the first hash
	 */
	public void setHash1(DataBuffer newVal){
		hash1 = newVal;
	}

	/**
	 * 
	 * 
	 * @return returns the second hash buffer
	 */
	public DataBuffer getHash2(){
		return hash2;
	}

	/**
	 * 
	 * @param newVal consigns the second hash buffe
	 */
	public void setHash2(DataBuffer newVal){
		hash2 = newVal;
	}

}