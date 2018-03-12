package com.secunet.ipsmall.test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;

import com.secunet.ipsmall.eac.sm.TestAPDUHandler.TestApdu;
import com.secunet.testbedutils.utilities.Base64Util;
import com.secunet.testbedutils.utilities.CommonUtil;
import com.secunet.testbedutils.utilities.VariableParser.VariableProvider;

public class VariableSessionParameter implements VariableProvider 
{
	
	public final static String c_eCardSessionID = "ecard.sessionid";
	public final static String c_eCardSessionPSK = "ecard.sessionpsk";
	public final static String c_eCardNewMessageID = "ecard.new.messageid";
	public final static String c_eCardStartPAOSMessageID = "ecard.startpaos.messageid";
	public final static String c_eCardStartPAOSSessionID = "ecard.startpaos.sessionid";
	public final static String c_eCardStartPAOSConnectionHandle = "ecard.startpaos.connectionhandle";
	public final static String c_eCardStartPAOSContextHandle = "ecard.startpaos.contexthandle";
	public final static String c_eCardStartPAOSSlotHandle = "ecard.startpaos.slothandle";
	public final static String c_eCardEphemeralPublicKey = "ecard.ephemeral.public.key";
	public final static String c_eCardChallengeSignature = "ecard.challenge.signature";
	public final static String c_eCardTransmitAPDU = "ecard.transmit.apdu.";
	
	
	ITestSession m_sessionData;
	
	ArrayList<String> m_allVars = new ArrayList<String>();
	VariableParameter m_testParameter;
	
	public VariableSessionParameter(ITestSession session) throws IllegalArgumentException, IllegalAccessException
	{
		m_sessionData = session;
		m_testParameter = new VariableParameter(session.getTestData());
		
		Field[] declaredFields = VariableSessionParameter.class.getDeclaredFields();
		for (Field field : declaredFields) {
		    if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) 
		    {
		    	m_allVars.add((String)field.get(null));
		    }
		}
	}
	
	
	@Override
	public String getValue(String varname) throws Exception 
	{
		if(c_eCardNewMessageID.equals(varname))
		{
			return Base64Util.encodeHEX(CommonUtil.convertUUID(UUID.randomUUID()));
		}
		if(c_eCardStartPAOSMessageID.equals(varname))
		{
			return m_sessionData.getStartPaos().getMessageID();
		}
		
		if(c_eCardStartPAOSSessionID.equals(varname))
		{
			return m_sessionData.getStartPaos().getSessionID();
		}
		
		if(c_eCardStartPAOSSlotHandle.equals(varname))
		{
			return m_sessionData.getStartPaos().getSlotHandle();
		}
		
		if(c_eCardStartPAOSConnectionHandle.equals(varname))
        {
            return m_sessionData.getStartPaos().getConnectionHandle();
        }
		
		if(c_eCardStartPAOSContextHandle.equals(varname))
		{
			return m_sessionData.getStartPaos().getContextHandle();
		}
		
		if(c_eCardEphemeralPublicKey.equals(varname))
		{    
		    boolean useRawKey = m_sessionData.getTestData().getUseRawEphemeralPublicKey();
			return Base64Util.encodeHEX(m_sessionData.getEphemeralPublicKey(useRawKey)).toLowerCase();
		}
		
		if(c_eCardSessionID.equals(varname))
		{
			return m_sessionData.getSessionID();
		}
		
		if(c_eCardSessionPSK.equals(varname))
		{
			return Base64Util.encodeHEX(m_sessionData.getPSKKey());
		}
		
		if(c_eCardChallengeSignature.equals(varname))
		{
			return Base64Util.encodeHEX(m_sessionData.getChallengeSiganture()).toLowerCase();
		}
		
		if(varname.startsWith(c_eCardTransmitAPDU))
		{
			String testName = varname.replace(c_eCardTransmitAPDU, "");
			
			return m_sessionData.getTransmitAPDU(TestApdu.getEnum(testName));
		}
		// TODO ADD MORE HERE

		return m_testParameter.getValue(varname);
	}

	@Override
	public boolean checkVarName(String substring) 
	{	
		if(substring.startsWith(c_eCardTransmitAPDU))
		{
			String testName = substring.replace(c_eCardTransmitAPDU, "");
			return (TestApdu.getEnum(testName) != null);
		}
		
		
		if(!m_allVars.contains(substring))
		{
			return m_testParameter.checkVarName(substring); 
		}
		
		return true;
	}

}
