package com.secunet.testbedutils.cvc.cvcertificate;

import java.util.BitSet;

import com.secunet.testbedutils.cvc.cvcertificate.exception.CVBufferNotEmptyException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVDecodeErrorException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVInvalidOidException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVTagNotFoundException;


/**
 * @brief This class is the base class for the different certificate authorizations
 *
 *
 * @author meier.marcus
 *
 */
public abstract class CVAuthorization {

	protected BitSet m_Authorization = null;
	private DataBuffer instanceOid = null;

	/**
	 * @brief constructor
	 *
	 */
	public CVAuthorization()
	{
		m_Authorization = new BitSet(size()*8);
		setRole(CertHolderRole.CVCA);
		setRole(CertHolderRole.Terminal);
	}
	
	/**
	 * Returns the OID for this instance, if set
	 * @return {@link DataBuffer} containing the instance OID, or <i>null</i> if it was not set 
	 */
	public DataBuffer instanceOid() {
		return instanceOid;
	}
	
	/**
	 * Set the OID for this specific instance
	 * @param instanceOid
	 */
	public void setInstanceOid(DataBuffer instanceOid) {
		this.instanceOid = instanceOid;
	}

	/**
	 * @brief This method returns the size of the Authorization data
	 *
	 * @return return the size of the Authorization
	 */
	abstract public int size();

	/**
	 * @brief This method identifies the terminal type which this object belongs to
	 *
	 * @return returns the terminal type
	 */
	abstract public TermType getTermType();

	/**
	 * @brief This method returns the authorization role of the certificate
	 *
	 * @return Certificate role
	 */
	public CertHolderRole getRole()
	{
		int offset = (((size() -1) * 8));
		BitSet role = m_Authorization.get(offset +6,offset +8);
		byte[] roleByte = toByteArray(role,1);

		switch((int)roleByte[0])
		{
		case 0:
			return CertHolderRole.Terminal;

		case 1:
			return CertHolderRole.DVforeign;

		case 2:
			return CertHolderRole.DVdomestic;

		case 3:
			return CertHolderRole.CVCA;
		}
		return CertHolderRole.Terminal;
	}

	/**
	 * @brief This method sets the certificate role
	 *
	 * @param role
	 */
	public void setRole(CertHolderRole role)
	{
		int offset = (((size() -1) * 8));

		switch(role)
		{
		case Terminal:
			m_Authorization.set(offset+6,false);
			m_Authorization.set(offset+7,false);
			break;
		case DVforeign:
			m_Authorization.set(offset+6,true);
			m_Authorization.set(offset+7,false);
			break;
		case DVdomestic:
			m_Authorization.set(offset+6,false);
			m_Authorization.set(offset+7,true);
			break;
		case  CVCA:
			m_Authorization.set(offset+6,true);
			m_Authorization.set(offset+7,true);
			break;
		}
	}

	/**
	 * @brief This method enables and disables the authorization of an certificate
	 *
	 * @param authFlag consigns the authorization
	 * @param value consigns whether or not this authorization is enabled or disabled
	 */
	public void setAuth(int authFlag,boolean value)
	{
		m_Authorization.set(authFlag,value);
	}

	/**
	 * @brief This method returns the status of the given authorization
	 *
	 * @param authFlag consigns the authorization flag which we want to know
	 * @return returns true when the given flag is enabled else false
	 */
	public boolean getAuth(int authFlag)
	{
		return m_Authorization.get(authFlag);
	}

	/**
	 *
	 * @brief this method generates the authorization data structure
	 *
	 * @return returns a DataBuffer with the authorization
	 */
	public DataBuffer genAuth()
	{
		DataBuffer out = new DataBuffer();
		TLV.append(out,CVCertificate.s_CvDataTag,toByteArray(m_Authorization,size()));
		return out;
	}

	/**
	 * @brief This method parse the authorization raw data
	 *
	 * @param rawData consigns the raw data
	 * @throws CVTagNotFoundException
	 * @throws CVBufferNotEmptyException
	 * @throws CVDecodeErrorException
	 */
	public void parseAuth(DataBuffer rawData) throws CVTagNotFoundException, CVBufferNotEmptyException, CVDecodeErrorException
	{
		DataBuffer data = new DataBuffer(rawData);

	    TLV extrData = TLV.extract(data);
		if(extrData.getTag() == CVCertificate.s_CvDataTag)
      	{
			if(extrData.getLength() != size())
  			{
				throw new CVDecodeErrorException();
  			}

			m_Authorization = fromByteArray(extrData.getValue().toByteArray());
      	}
		else
      	{
      		//m_rLog << "0x53 Discretionary data tag not found" << std::endl;
			throw new CVTagNotFoundException("0x53");
      	}

		if(data.size() > 0)
		{
		  	// Error message
			//m_rLog << "Error: " << (unsigned int)data.size() << " bytes left in buffer" << std::endl;
			throw new CVBufferNotEmptyException();
		}
	}

	public void maskAuthBits(CVAuthorization otherAuth) throws CVInvalidOidException
	{
		if(getTermType() != otherAuth.getTermType())
		{
			throw new CVInvalidOidException();
		}

		int size = (size() * 8) - 2;

		for(int i = 0;i< size;i++)
		{
			setAuth(i, getAuth(i) & otherAuth.getAuth(i));
		}
	}


	/**
	 *
	 * @brief Returns a bit set containing the values in bytes.
	 *
     * The byte-ordering of bytes must be big-endian which means the most significant bit is in element 0.
	 *
	 * @param bytes consigns the byte array
	 * @return returns the bit set object
	 */
    static public BitSet fromByteArray(byte[] bytes) {
        BitSet bits = new BitSet();
        for (int i=0; i<bytes.length*8; i++) {
            if ((bytes[bytes.length-i/8-1]&(1<<(i%8))) > 0) {
                bits.set(i);
            }
        }
        return bits;
    }

    /**
     *
     * @brief Returns a byte array of at least length 1.
     *
     * The most significant bit in the result is guaranteed not to be a 1
     * (since BitSet does not support sign extension).
     * The byte-ordering of the result is big-endian which means the most significant bit is in element 0.
     * The bit at index 0 of the bit set is assumed to be the least significant bit.
     *
     * @param bits consigns the bit set object
     * @param resultByteSize consigns the byte size of the result
     * @return returns the byte array
     */
    static public byte[] toByteArray(BitSet bits, int resultByteSize) {

        byte[] bytes = new byte[resultByteSize];
        for (int i=0; i<bits.length(); i++) {
            if (bits.get(i)) {
                bytes[bytes.length-i/8-1] |= 1<<(i%8);
            }
        }
        return bytes;
    }


    public static String getText(int cvAuthorization)
    {
    	return "";
    }
}
