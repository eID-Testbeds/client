package com.secunet.testbedutils.bouncycertgen;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.secunet.testbedutils.cvc.cvcertificate.CVCertificate;
import com.secunet.testbedutils.cvc.cvcertificate.DataBuffer;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVAuthorityRefNotValidException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVBufferNotEmptyException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVDecodeErrorException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVInvalidDateException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVInvalidECPointLengthException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVInvalidKeySourceException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVInvalidOidException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVKeyTypeNotSupportedException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVMissingKeyException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVSignOpKeyMismatchException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVTagNotFoundException;
import com.secunet.testbedutils.utilities.Base64Util;
import com.secunet.testbedutils.utilities.FileUtils;

public interface CertificateGenerator {
	public static final Logger logger = LogManager.getRootLogger();
	
	/**
	 * Create a x509 certificates for each of the provided XML's.
	 * <p>
	 * - Each input XML may contain one or more certificates
	 * <p>
	 * - If no signature algorithm is provided, SHA256withRSA with a bit length
	 * of 2048 bit will be used.
	 * <p>
	 * - If no key file is provided, a new key pair will be created
	 * <p>
	 * - If no begin date is provided, System.currentTimeMillis() will be used
	 * <p>
	 * - If no expiration date is provided, it will be set to 6000 days from now
	 * <p>
	 * - If no serial number is provided, 42 will be used
	 * 
	 * @param {@link List<String>} xmlPaths The paths to the XML files that will
	 *        be used for generating the certificates
	 * @return {@link List<X509Certificate>} A list of X509 certificates
	 */
	public List<GeneratedCertificate> makex509Certificates(List<String> xmlPaths);

	/**
	 * Create a x509 certificates for the provided string. This string must
	 * contain the XML data structure as defined by the tls_schema.xsd
	 * <p>
	 * - Each input XML may contain one or more certificates
	 * <p>
	 * - If no signature algorithm is provided, SHA256withRSA with a bit length
	 * of 2048 bit will be used.
	 * <p>
	 * - If no key file is provided, a new key pair will be created
	 * <p>
	 * - If no begin date is provided, System.currentTimeMillis() will be used
	 * <p>
	 * - If no expiration date is provided, it will be set to 6000 days from now
	 * <p>
	 * - If no serial number is provided, 42 will be used
	 * 
	 * @param {@link String} definition The XML certificate definition string
	 * @return {@link List<X509Certificate>} A list of X509 certificates
	 */
	public List<GeneratedCertificate> makex509Certificate(String definition);

	/**
	 * Read the X509Certificate from the given path
	 * 
	 * @param {@link String} path The path to the certificate
	 * @return {@link X509Certificate} The X509 certificate read from the file
	 */
	public X509Certificate readFromFileSystem(String path);

	/**
	 * Generate CV certificate(s) using the provided XML file
	 * <p>
	 * - The XML file may contain one or more certificates
	 * 
	 * @param xmlPath
	 * @return
	 */
	public List<CVCertificate> makeCVCertifcates(String xmlPath);
	
	/**
	 * Read a X509Certificate from a Base64 encoded String 
	 * @param base64Encoded
	 * @return
	 * @throws CertificateException 
	 */
	public static X509Certificate x509FromBase64String(String base64Encoded) throws CertificateException {
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		InputStream stream = new ByteArrayInputStream(Base64Util.decode(base64Encoded));
		X509Certificate cert = (X509Certificate) certFactory.generateCertificate(stream);
		return cert;
	}
	
	/**
	 * Encode the given X509Certificate to base64
	 * 
	 * @param certificate
	 * @return
	 * @throws CertificateEncodingException 
	 */
	public static String x509ToBase64String(X509Certificate certificate) throws CertificateEncodingException {
		return Base64Util.encode(certificate.getEncoded());
	}
	
	/**
	 * Read a CV certificate from a Base64 encoded String 
	 * @param base64Encoded
	 * @return
	 * @throws CertificateException 
	 * @throws IOException 
	 * @throws CVInvalidECPointLengthException 
	 * @throws CVInvalidDateException 
	 * @throws CVDecodeErrorException 
	 * @throws CVInvalidOidException 
	 * @throws CVBufferNotEmptyException 
	 * @throws CVTagNotFoundException 
	 */
	public static CVCertificate cvFromBase64String(String base64Encoded) throws CertificateException, IOException, CVTagNotFoundException, CVBufferNotEmptyException, CVInvalidOidException, CVDecodeErrorException, CVInvalidDateException, CVInvalidECPointLengthException {
		InputStream stream = new ByteArrayInputStream(Base64Util.decode(base64Encoded));
		DataBuffer db = new DataBuffer(FileUtils.inputStreamToBytes(stream));
		CVCertificate cert = new CVCertificate(db);
		return cert;
	}
	
	/**
	 * Encode the given CV certificate to base64
	 * 
	 * @param certificate
	 * @return
	 * @throws CertificateEncodingException 
	 * @throws IOException 
	 */
	public static String cvToBase64String(CVCertificate certificate) throws CertificateEncodingException, IOException {
		try {
			return Base64Util.encode(certificate.generateCert().toByteArray());
		} catch (CVAuthorityRefNotValidException | CVInvalidKeySourceException | CVSignOpKeyMismatchException | CVInvalidOidException | CVMissingKeyException
				| CVKeyTypeNotSupportedException e) {
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Could not encode CV certificate: " + System.getProperty("line.separator") + trace.toString());
		}
		return null;
	}

}
