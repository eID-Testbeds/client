package com.secunet.testbedutils.utilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

/**
* @author Lukasz Kubik
*/
public class CertificateUtil {
	private static final Logger logger = LogManager.getRootLogger();
	
	static {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}
	
	/**
	 * Loads a {@link X509Certificate} from the given stream. In case of an error <i>null</i> is returned  
	 * @param stream
	 * @return
	 */
	public static X509Certificate loadX509Certificate(InputStream stream) {
		CertificateFactory factory;
		try {
			factory = CertificateFactory.getInstance("x.509");
			X509Certificate cert = (X509Certificate) factory.generateCertificate(stream);			
			return cert;
		} catch (CertificateException e) {
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Could not load certificate: " + System.getProperty("line.separator") + trace.toString());
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				// should really, REALLY not happen :)
			}
		}
		return null;
	}
	
	/**
	 * Load a {@link PrivateKey} using the provided path. In case of an error <i>null</i> is returned.
	 * @param filepath The path to the file
	 * @param keyAlg (optional) Only provide this if the key file is password protected
	 * @return
	 */
	public static PrivateKey loadPrivateKey(String filepath, String keyAlg) {
		try {
			return loadPrivateKey(Files.readAllBytes(Paths.get(filepath)), keyAlg);
		} catch (IOException e) {
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Could not load private key:" + System.getProperty("line.separator") + trace.toString());
		}
		return null;
	}
	
	/**
	 * Load a {@link PrivateKey} using the {@link InputStream}. In case of an error <i>null</i> is returned.
	 * @param filepath The path to the file
	 * @param keyAlg (optional) Only provide this if the key file is password protected
	 * @return
	 */
	public static PrivateKey loadPrivateKey(InputStream stream, String keyAlg) {
		return loadPrivateKey(loadBytes(stream), keyAlg);
	}
	
	/**
	 * Loads a PEM {@link KeyPair} from the given file. In case of an error <i>null</i> is returned.
	 * @param file {@link File} The file from which to load the key
	 * @param key (optional) Only provide this if the key file is password protected
	 * @return
	 */
	public static KeyPair loadKeyPair(File file, String ... password) {
		try {
			PEMParser parser = new PEMParser(new FileReader(file));
			loadKeyPair(parser, password);
		} catch (FileNotFoundException e) {
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("The certificate could not be extracted:" + System.getProperty("line.separator") + trace.toString());
		}
		return null;
	}
	
	/**
	 * Loads a PEM {@link KeyPair} from the given {@link InputStream}. In case of an error <i>null</i> is returned.
	 * @param file {@link File} The file from which to load the key
	 * @param key (optional) Only provide this if the key file is password protected
	 * @return
	 */
	public static KeyPair loadKeyPair(InputStream stream, String ... password) {
		InputStreamReader streamReader = new InputStreamReader(stream);
		PEMParser parser = new PEMParser(streamReader);
		return loadKeyPair(parser, password);
	}
	
	private static KeyPair loadKeyPair(PEMParser parser, String ... password) {
		KeyPair pair = null;
		try {
			Object object = parser.readObject();
			JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
			
			// without PKCS#8, but with password
			if (object instanceof PEMEncryptedKeyPair) {
				if (password[0] != null) {
					PEMDecryptorProvider decrypter = new JcePEMDecryptorProviderBuilder().build(password[0].toCharArray());
					pair = converter.getKeyPair(((PEMEncryptedKeyPair) object).decryptKeyPair(decrypter));
				} else {
					logger.error("No password provided for the key file.");
				}
			}
			// without PKCS#8 or password
			else if (object instanceof PEMKeyPair) {
				pair = converter.getKeyPair((PEMKeyPair) object);
			} else {
				logger.error("The provided key file format for the signature could not be recognized.");
			}
		} catch (FileNotFoundException e) {
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("The certificate could not be extracted:" + System.getProperty("line.separator") + trace.toString());
		} catch (PEMException e) {
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("The certificate could not be extracted:" + System.getProperty("line.separator") + trace.toString());
		} catch (IOException e) {
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("The certificate could not be extracted:" + System.getProperty("line.separator") + trace.toString());
		}
		
		return pair;
	}
	
	
	// load key from byte array
	private static PrivateKey loadPrivateKey(byte[] bytes, String keyAlg) {
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory factory;
		try {
			factory = KeyFactory.getInstance(keyAlg);
			return factory.generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException e) {
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Could not load private key:" + System.getProperty("line.separator") + trace.toString());
		} catch (InvalidKeySpecException e) {
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Could not load private key:" + System.getProperty("line.separator") + trace.toString());
		}
        return null;
	}
	
	// load bytes from input stream
	private static byte[] loadBytes(InputStream stream) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int readData;
		byte[] data = new byte[65536];

		try {
			while ((readData = stream.read(data, 0, data.length)) != -1) {
			  buffer.write(data, 0, readData);
			}
			buffer.flush();
		} catch (IOException e) {
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Could not load key input stream:" + System.getProperty("line.separator") + trace.toString());
		}
		return buffer.toByteArray();
	}

}
