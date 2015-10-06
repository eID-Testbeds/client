package com.secunet.ipsmall.eac.sm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.params.KeyParameter;

import com.secunet.ipsmall.eac.cv.ByteHelper;
import com.secunet.ipsmall.eac.cv.TLVObject;
import com.secunet.ipsmall.log.Logger;


public class SecureMessaging {
	public enum SymmetricCipher {
		UNKNOWN,
		DESEDE,
		AES
	}
	
	private SymmetricCipher symmetricCipher = null;
	private int blockLength = 0;
	private SecretKey encryptionKey = null;
	private SecretKey macKey = null;
	private byte[] sendSequenceCounter;
	
	boolean log = false;

	public SecureMessaging(SymmetricCipher symmetricCipher, SecretKey encryptionKey, SecretKey macKey) throws EIDCryptoException {
		this.symmetricCipher = symmetricCipher;
		switch( symmetricCipher ) {
			case AES: {
				blockLength = 16;
				break;
			}
			case DESEDE: {
				blockLength = 8;
				break;
			}
			default:
				throw new EIDCryptoException( "unknown cipher" );
		}
		this.encryptionKey = encryptionKey;
		this.macKey = macKey;
		this.sendSequenceCounter = new byte[ blockLength ];
	}
	
	public void nextAPDU() {
		addSSC( 1 );
	}
	
	private void addSSC( int a ) {
		for( int i = sendSequenceCounter.length - 1 ; i >= 0 && a > 0 ; i-- ) {
			a += sendSequenceCounter[i] & 0xFF;
			sendSequenceCounter[i] = (byte) a;
			a >>>= 8;
		}
	}
	
	public CommandAPDU encrypt( CommandAPDU c) throws EIDCryptoException {
	    return encrypt(c, false);
	}

	public CommandAPDU encrypt( CommandAPDU c, boolean dataBERTLVencoded ) throws EIDCryptoException {
		try {
			byte[] encryptedDataObject = null;
			byte[] expectedLengthDataObject = null;
			byte[] checksumDataObject = null;
			boolean useExtendedLength = false;
			
			// construct data object 0x87 = data
			byte[] plainData = c.getData();
			if( plainData.length > 0 ) {
				if( plainData.length >= 256 ) useExtendedLength = true;
				byte[] encData = crypt( Cipher.ENCRYPT_MODE, plainData );
				if (dataBERTLVencoded) { // use 85 if plain data are BER-TLV encoded
				    TLVObject do85 = new TLVObject( 0x85, encData );
				    encryptedDataObject = do85.toBytes();
				} else { // else use 87 with padding byte
				    ByteArrayOutputStream baos = new ByteArrayOutputStream();
        			baos.write( 0x01 ); // padding-content indicator byte
        			baos.write( encData );
        			TLVObject do87 = new TLVObject( 0x87, baos.toByteArray() );
        			encryptedDataObject = do87.toBytes();
				}
			}
			// construct data object 0x97 = expected length of response
			int ne = c.getNe();
			if( ne > 0 ) {
				if( ne > 256 ) useExtendedLength = true;
				if( useExtendedLength ) {
					// use long le
					byte[] longExpectedLength = new byte[]{ 0x00, 0x00 };
					if( ne <= 65535) {
						longExpectedLength[0] = (byte)((ne >>> 8) & 0xFF);
						longExpectedLength[1] = (byte)(ne & 0xFF);
					}
					TLVObject do97 = new TLVObject( 0x97, longExpectedLength );
					expectedLengthDataObject = do97.toBytes();
				}
				else {
					// use short le
					TLVObject do97 = new TLVObject( 0x97, (ne <= 255)?( (byte)ne ):( (byte)0 ) );
					expectedLengthDataObject = do97.toBytes();
				}
			}
			
			// construct data object 0x8E = checksum
			ByteArrayOutputStream headerBaos = new ByteArrayOutputStream();
			headerBaos.write( c.getCLA() | 0x0C );
			headerBaos.write( c.getINS() );
			headerBaos.write( c.getP1() );
			headerBaos.write( c.getP2() );
			ByteArrayOutputStream bodyBaos = new ByteArrayOutputStream();
			if( null != encryptedDataObject ) bodyBaos.write( encryptedDataObject );
			if( null != expectedLengthDataObject ) bodyBaos.write( expectedLengthDataObject );
			byte[] checksum = calculateChecksum( headerBaos.toByteArray(), bodyBaos.toByteArray() );
			TLVObject do8e = new TLVObject( 0x8E, checksum );
			checksumDataObject = do8e.toBytes();
			
			// construct encrypted command APDU
			ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
			if( null != encryptedDataObject ) dataStream.write( encryptedDataObject );
			if( null != expectedLengthDataObject ) dataStream.write( expectedLengthDataObject );
			dataStream.write( checksumDataObject );
			// note: set expected length of encrypted APDU always to maximum
			CommandAPDU out = new CommandAPDU( c.getCLA() | 0x0C, c.getINS(), c.getP1(), c.getP2(), dataStream.toByteArray(), ( useExtendedLength ? 65536 : 256 ) );
			return out;
		} catch (IOException e) {
			throw new EIDCryptoException(e);
		}
	}
	
	public ResponseAPDU decrypt( ResponseAPDU r ) throws EIDCryptoException {
		byte[] securedResponseData = r.getData();
		int outerSW1 = r.getSW1();
		int outerSW2 = r.getSW2();

		//parse TLV objects in response data
		LinkedList<TLVObject> tlvList = TLVObject.generateMultipleFromBytes( securedResponseData );
		if( tlvList.size() < 2 ) throw new EIDCryptoException( "SM response ADPU contains less than 2 TLV objects" );
		if( tlvList.size() > 4 ) throw new EIDCryptoException( "SM response ADPU contains more than 4 TLV objects" );

		TLVObject firstTLVObject = tlvList.pop();
		TLVObject encryptedDataTLVObject = null;
		TLVObject statusWordTLVObject = null;
		TLVObject checksumTLVObject = null;
		// first data object could be encrypted data or status word
		if( firstTLVObject.getTag() == 0x87 ) {
			// response contains encrypted data
			encryptedDataTLVObject = firstTLVObject;
			// next data object must be status word
			statusWordTLVObject = tlvList.pop();
		}
		else {
			// response contains no encrypted data
			statusWordTLVObject = firstTLVObject;
		}
		if( tlvList.size() == 1 ) {
			checksumTLVObject = tlvList.pop();
		}
		else {
			throw new EIDCryptoException( "SM response ADPU contains no checksum data object" );
		}

		// check objects
		if( null == statusWordTLVObject ) throw new EIDCryptoException( "SM response ADPU contains no status word data object" );
		if( statusWordTLVObject.getTag() != 0x99 ) throw new EIDCryptoException( "SM response ADPU contains status word data object with illegal tag " + statusWordTLVObject.getTag() );
		if( null == checksumTLVObject ) throw new EIDCryptoException( "SM response ADPU contains no checksum data object" );
		if( checksumTLVObject.getTag() != 0x8E ) throw new EIDCryptoException( "SM response ADPU contains checksum data object with illegal tag " + checksumTLVObject.getTag() );
		
		// check status word
		if( ( null == statusWordTLVObject.value ) || ( statusWordTLVObject.value.length != 2 ) ) throw new EIDCryptoException( "SM response ADPU contains incorrectly encoded status word bytes" );
		if( ( statusWordTLVObject.value[0] != (byte)outerSW1 ) || ( statusWordTLVObject.value[1] != (byte)outerSW2 )) throw new EIDCryptoException( "SM response ADPU contains different status word bytes" );
		
		// decrypt data if available
		byte[] decryptedResponseData = null;
		if( null != encryptedDataTLVObject ) {
			if( encryptedDataTLVObject.value.length < 1 || encryptedDataTLVObject.value[0] != 0x01 ) throw new EIDCryptoException( "SM response ADPU contains invalid data padding" );
			decryptedResponseData = crypt( Cipher.DECRYPT_MODE, Arrays.copyOfRange( encryptedDataTLVObject.value, 1, encryptedDataTLVObject.value.length ) );
			if( null == decryptedResponseData || decryptedResponseData.length < 1 ) throw new EIDCryptoException( "SM response ADPU contains no data in 0x87 data object" );
		}			

		// check MAC checksum
		if( ( null == checksumTLVObject.value ) || ( checksumTLVObject.value.length != 8 ) ) throw new EIDCryptoException( "SM response ADPU contains incorrectly encoded checksum bytes" );
		// calculate checksum over sent data, skipping last 10 bytes which must contain the checksum data object 0x8E
		byte[] checksum = calculateChecksum( Arrays.copyOfRange( securedResponseData, 0, securedResponseData.length -10 ) );
		if( ! Arrays.equals( checksumTLVObject.value, checksum ) ) throw new EIDCryptoException( "SM response ADPU contains wrong checksum" );

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			if( null != decryptedResponseData) baos.write( decryptedResponseData );
			baos.write( outerSW1 );
			baos.write( outerSW2 );
			ResponseAPDU out = new ResponseAPDU( baos.toByteArray() );
			return out;
		} catch (IOException e) {
			throw new EIDCryptoException(e);
		}
	}
	
	private byte[] crypt( int encryptionMode, byte[] data ) throws EIDCryptoException {
		if(log ) Logger.EAC.logState(( encryptionMode == Cipher.ENCRYPT_MODE ? "Data plain    : " : "Data encrypted: " ) + ByteHelper.toHexString( data ) );
		if(log ) Logger.EAC.logState("Send Sequence Counter plain: " + ByteHelper.toHexString( sendSequenceCounter ) );
		byte[] crypted = null;
		try {
			switch( symmetricCipher ) {
			case DESEDE:
				//notTODO SM with 3DES is not used on test cards
				break;
			case AES:
				// prepare IV
				Cipher cipherIV = Cipher.getInstance( "AES/ECB/NoPadding" ); // one block with CBC and zero IV => ECB 
				cipherIV.init( Cipher.ENCRYPT_MODE, encryptionKey ); 
				byte[] iv = cipherIV.doFinal( sendSequenceCounter );
				if(log ) Logger.EAC.logState("Send Sequence Counter encrypted: " + ByteHelper.toHexString( iv ) );
	
				// do cryptographic work
				Cipher cipher = Cipher.getInstance( "AES/CBC/NoPadding" ); 
				cipher.init( encryptionMode, encryptionKey, new IvParameterSpec(iv) );
				if( encryptionMode == Cipher.ENCRYPT_MODE ) {
					crypted = cipher.doFinal( pad( data ) );
				}
				else {
					crypted = unpad( cipher.doFinal( data ) );
				}
				break;
			default:
				throw new EIDCryptoException( "unsupported symmetric cipher" );
			}
		} catch (NoSuchAlgorithmException e) {
			throw new EIDCryptoException(e);
		} catch (NoSuchPaddingException e) {
			throw new EIDCryptoException(e);
		} catch (InvalidKeyException e) {
			throw new EIDCryptoException(e);
		} catch (IllegalBlockSizeException e) {
			throw new EIDCryptoException(e);
		} catch (BadPaddingException e) {
			throw new EIDCryptoException(e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new EIDCryptoException(e);
		}
		
		if(log ) Logger.EAC.logState(( encryptionMode == Cipher.ENCRYPT_MODE ? "Data encrypted: " : "Data plain    : " ) + ByteHelper.toHexString( crypted ) );
		return crypted;
	}

	private byte[] calculateChecksum( byte[] header, byte[] body ) throws EIDCryptoException {
		if(log ) Logger.EAC.logState("command APDU header bytes: " + ByteHelper.toHexString( header ) );
		if(log ) Logger.EAC.logState("command APDU body bytes : " + ByteHelper.toHexString( body ) );
		try {
			ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
			dataStream.write( sendSequenceCounter ); // SSC always has block length
			//pad header
			byte[] paddedHeader = pad( header );
			dataStream.write( paddedHeader );
			//pad body
			if( body.length > 0 ) {
				byte[] paddedBody = pad( body );
				dataStream.write( paddedBody );
			}
			
			byte[] macContent = dataStream.toByteArray();
			if(log ) Logger.EAC.logState("macContent bytes: " + ByteHelper.toHexString( macContent ) );
			return calculateMAC( macContent );
		} catch (IOException e) {
			throw new EIDCryptoException( e );
		}
	}
	
	private byte[] calculateChecksum( byte[] responseData ) throws EIDCryptoException {
		if(log ) Logger.EAC.logState("response APDU data bytes: " + ByteHelper.toHexString( responseData ) );
		try {
			ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
			dataStream.write( sendSequenceCounter ); // SSC always has block length
			//pad response data
			byte[] paddedResponse = pad( responseData );
			dataStream.write( paddedResponse );
			
			byte[] macContent = dataStream.toByteArray();
			if(log ) Logger.EAC.logState("macContent bytes: " + ByteHelper.toHexString( macContent ) );
			return calculateMAC( macContent );
		} catch (IOException e) {
			throw new EIDCryptoException( e );
		}
	}
	
	private byte[] calculateMAC( byte[] macContent ) throws EIDCryptoException {
		try {
			switch( symmetricCipher ) {
				case AES: {
			        CMac cmac = new CMac( new AESEngine(), 64 );
					cmac.init( new KeyParameter( macKey.getEncoded() ) );
					cmac.update( macContent, 0, macContent.length );
					byte[] out = new byte[ cmac.getMacSize() ];
					cmac.doFinal( out, 0 );
					return out;
				}
				case DESEDE: {
			        Mac mac = Mac.getInstance( "ISO9797ALG3WITHISO7816-4PADDING" );
			        mac.init( macKey );
			        return mac.doFinal( macContent );
				}
				default:
					throw new EIDCryptoException( "unknown cipher" );
			}
		} catch (NoSuchAlgorithmException e) {
			throw new EIDCryptoException( e );
		} catch (InvalidKeyException e) {
			throw new EIDCryptoException( e );
		}
	}
	
	private byte[] pad( byte[] data ) throws EIDCryptoException {
		if( null == data ) return null;
		int numBlocks = data.length / blockLength + 1; // one padding byte is always added!!
		int newLength = blockLength * numBlocks;
		byte[] padded = Arrays.copyOf( data, newLength );
		padded[ data.length ] = (byte)0x80;
		return padded;
	}

	private byte[] unpad( byte[] data ) throws EIDCryptoException{
		if( null == data ) return null;
		int newLength = data.length;
		while( newLength > 0 ) {
			newLength--;
			if( data[ newLength ] == (byte)0x80 ) break; // start of padding
			if( data[ newLength ] != (byte)0x00 ) newLength = -1; // invalid padding
		}
		if( newLength < 0  ) throw new EIDCryptoException( "invalid padding" );
		return Arrays.copyOf( data, newLength );
	}
}
