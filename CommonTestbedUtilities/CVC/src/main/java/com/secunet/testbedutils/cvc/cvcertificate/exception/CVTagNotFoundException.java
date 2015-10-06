package com.secunet.testbedutils.cvc.cvcertificate.exception;

import java.math.BigInteger;

/**
 * tag not found exception
 * @author meier.marcus
 *
 */
public class CVTagNotFoundException extends CVBaseException {
	static final long serialVersionUID = 1;
	protected String m_Tag = "";
	/**
	 * 
	 * @brief constructor  
	 * 
	 * @param strTag consigns the missing tag
	 */
	public CVTagNotFoundException(String strTag)
	{
		super("res:com.secunet.cvca.exception.CVTagNotFoundException");
		m_Tag = strTag;	
	}
	/**
	 * 
	 * @brief constructor  
	 * 
	 * @param strTag consigns the missing tag
	 */
	public CVTagNotFoundException(int tag)
	{
		super("res:com.secunet.cvca.exception.CVTagNotFoundException");
	
		m_Tag = "0x" + BigInteger.valueOf(tag).toString(16);
	}
	/**
	 * 
	 * @brief
	 * 
	 * @return returns the stored tag as string
	 */
	public String getTag()
	{
		return m_Tag;
	}
}
