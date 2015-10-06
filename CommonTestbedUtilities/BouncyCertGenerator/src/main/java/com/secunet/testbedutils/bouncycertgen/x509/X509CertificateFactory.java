package com.secunet.testbedutils.bouncycertgen.x509;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import com.secunet.testbedutils.bouncycertgen.GeneratedCertificate;
import com.secunet.testbedutils.bouncycertgen.KeyGenerator;

/**
 * @author Lukasz Kubik, secunet
 *
 */
public class X509CertificateFactory {
	private static final Logger logger = Logger.getLogger(X509CertificateFactory.class.getName());

	/**
	 * Create a x509 certificate using the provided data.
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
	 * @param {@link CertificateDefinition} certificateDefinition - The
	 *        definition file for the certificate
	 * @return {@link CertKeysPair}
	 */
	public static GeneratedCertificate createX509(CertificateDefinition certificateDefinition) {
		String issuer = buildIssuerOrSubjectString(certificateDefinition, true);
		String subject = buildIssuerOrSubjectString(certificateDefinition, false);
		X500Name issuerX509 = new X500Name(issuer);
		X500Name subjectX509 = new X500Name(subject);
		// check if a serial number was provided
		BigInteger serial = (certificateDefinition.getSerialNumber() != null) ? certificateDefinition.getSerialNumber() : BigInteger.valueOf(42);
		// check if a start date was provided
		Date notBefore = (certificateDefinition.getNotBefore() != null) ? certificateDefinition.getNotBefore().toGregorianCalendar().getTime() : new Date(
				System.currentTimeMillis());
		// check if a an expiration date was provided
		Date notAfter = (certificateDefinition.getNotAfter() != null) ? certificateDefinition.getNotAfter().toGregorianCalendar().getTime() : new Date(
				System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 6000);
		// set up the signature parameters
		KeyPair pair = null;
		String sigType = null;
		// check if a key file was provided
		if (certificateDefinition.getKeyFile() != null) {
			File f = new File(certificateDefinition.getKeyFile().getValue());
			PEMParser parser = null;
			try {
				parser = new PEMParser(new FileReader(f));
				Object object = parser.readObject();
				JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
				// without PKCS#8, but with password
				if (object instanceof PEMEncryptedKeyPair) {
					if (certificateDefinition.getKeyFile().getPassword() != null) {
						PEMDecryptorProvider decrypter = new JcePEMDecryptorProviderBuilder().build(certificateDefinition.getKeyFile().getPassword()
								.toCharArray());
						pair = converter.getKeyPair(((PEMEncryptedKeyPair) object).decryptKeyPair(decrypter));
					} else {
						logger.log(Level.SEVERE, "No password provided for the key file " + f.getAbsolutePath() + ". A new key pair will be generated instead.");
					}
				}
				// without PKCS#8 or password
				else if (object instanceof PEMKeyPair) {
					pair = converter.getKeyPair((PEMKeyPair) object);
				} else {
					logger.log(Level.SEVERE, "The provided key file format for the signature could not be recognized. Filename: " + f.getAbsolutePath()
							+ ". A new key pair will be generated instead.");
				}
				if (pair != null && "RSA".equals(pair.getPrivate().getAlgorithm())) {
					sigType = "RSA";
				} else if (pair != null && "ECDSA".equals(pair.getPrivate().getAlgorithm())) {
					sigType = "ECDSA";
				}
			} catch (IOException e) {
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
				logger.log(Level.SEVERE,
						"Could not read key file " + f.getAbsolutePath() + ". Error:" + System.getProperty("line.separator") + trace.toString());
				e.printStackTrace();
			} finally {
				try {
					if (parser != null) {
						parser.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// no key file was provided
		else {
			// check if a signature algorithm was chosen, if not default to RSA
			if (certificateDefinition.getAlgorithmID() != null) {
				if (certificateDefinition.getAlgorithmID().getECDSA() != null) {
					sigType = "ECDSA";
				} else if (certificateDefinition.getAlgorithmID().getRSA() != null) {
					sigType = "RSA";
				}
			} else {
				sigType = "RSA";
			}
		}
		ContentSigner sigGen = null;
		try {
			if (sigType != null && sigType.equals("RSA")) {
				if (pair == null) {
					pair = KeyGenerator.generateRSAPair(certificateDefinition.getAlgorithmID().getRSA().getKeylength());
				}
				sigGen = new JcaContentSignerBuilder(certificateDefinition.getAlgorithmID().getRSA().getValue().toString().replace("_", "")).setProvider("BC")
						.build(pair.getPrivate());
			} else if (sigType != null && sigType.equals("ECDSA")) {
				if (pair == null) {
					pair = KeyGenerator.generateECPair(certificateDefinition.getAlgorithmID().getECDSA().getCurve());
				}
				sigGen = new JcaContentSignerBuilder(certificateDefinition.getAlgorithmID().getECDSA().getValue().toString().replace("_", ""))
						.setProvider("BC").build(pair.getPrivate());
			}
		} catch (OperatorCreationException e) {
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.log(Level.SEVERE, "The signature could not be generated:" + System.getProperty("line.separator") + trace.toString());
		}
		// build the certificate
		X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(issuerX509, serial, notBefore, notAfter, subjectX509, pair.getPublic());
		// add the extensions
		addExtensions(certificateDefinition, builder);
		X509CertificateHolder holder = builder.build(sigGen);
		X509Certificate certificate = null;
		try {
			JcaX509CertificateConverter converter = new JcaX509CertificateConverter();
			converter.setProvider("BC");
			certificate = converter.getCertificate(holder);
		} catch (CertificateException e) {
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.log(Level.SEVERE, "The certificate could not be extracted:" + System.getProperty("line.separator") + trace.toString());
		}
		if (certificate != null) {
			return new GeneratedCertificate(certificateDefinition, certificate, pair);
		} else {
			return null;
		}
	}

	/**
	 * Build the extensions as defined in 'definitions' and add them to the
	 * certificate builder
	 * 
	 * @param {@link CertificateDefinition} definition - The definition file for
	 *        the certificate
	 * @param {@link X509v3CertificateBuilder} builder - The certificate builder
	 *        instance
	 */
	private static void addExtensions(CertificateDefinition definition, X509v3CertificateBuilder builder) {
		// keyUsage
		if (definition.getExtensions() != null && definition.getExtensions().getKeyUsage() != null) {
			int[] mask = buildBitmaskExtension(definition.getExtensions().getKeyUsage(), KeyUsage.class);
			// add the keyUsage extension
			try {
				builder.addExtension(Extension.keyUsage, (mask[0] == 1), new KeyUsage(mask[1]));
			} catch (IOException e) {
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
				logger.log(Level.WARNING, "Could not create the certificate extension" + System.getProperty("line.separator") + trace.toString());
			}
		}
		// basicConstraints
		if (definition.getExtensions() != null && definition.getExtensions().getBasicConstraints() != null) {
			BasicConstraintsType bc = definition.getExtensions().getBasicConstraints();
			// add the basicConstraints extension
			try {
				if (bc.getPathLenConstraint() == null) {
					builder.addExtension(Extension.basicConstraints, bc.isCritical(), new BasicConstraints(bc.isCA()));
				} else {
					builder.addExtension(Extension.basicConstraints, bc.isCritical(), new BasicConstraints(bc.getPathLenConstraint()));
				}
			} catch (IOException e) {
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
				logger.log(Level.WARNING, "Could not create the certificate extension" + System.getProperty("line.separator") + trace.toString());
			}
		}
		// extendedKeyUsage
		if (definition.getExtensions() != null && definition.getExtensions().getExtendedKeyUsage() != null) {
			Set<Object> oSet = buildObjectExtension(definition.getExtensions().getExtendedKeyUsage(), KeyPurposeId.class);
			Boolean isCritical = false;
			KeyPurposeId[] ids = new KeyPurposeId[oSet.size() - 1];
			int i = 0;
			for (Object o : oSet) {
				if (o instanceof Boolean) {
					isCritical = (Boolean) o;
				} else if (o instanceof KeyPurposeId) {
					ids[i++] = (KeyPurposeId) o;
				}
			}
			ExtendedKeyUsage ekUsage = new ExtendedKeyUsage(ids);
			try {
				builder.addExtension(Extension.extendedKeyUsage, isCritical, ekUsage);
			} catch (CertIOException e) {
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
				logger.log(Level.WARNING, "Could not build extended key usage extension:" + System.getProperty("line.separator") + trace.toString());
			}
		}
		// subjectAltName
		if (definition.getExtensions() != null && definition.getExtensions().getSubjectAltName() != null) {
			String generalNameString = definition.getExtensions().getSubjectAltName().getGeneralName().value();
			DERIA5String derEncoded = null;
			if ("rfc822Name".equals(generalNameString) || "dNSName".equals(generalNameString) || "uniformResourceIdentifier".equals(generalNameString)) {
				derEncoded = new DERIA5String(definition.getExtensions().getSubjectAltName().getValue());
			}
			try {
				if (derEncoded != null) {
					builder.addExtension(Extension.subjectAlternativeName, definition.getExtensions().getSubjectAltName().isCritical(), derEncoded);
				}
			} catch (CertIOException e) {
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
				logger.log(Level.WARNING, "Could not build subject alternative name extension:" + System.getProperty("line.separator") + trace.toString());
			}
		}
		// issuerAltName
		if (definition.getExtensions() != null && definition.getExtensions().getIssuerAltName() != null) {
			String generalNameString = definition.getExtensions().getIssuerAltName().getGeneralName().value();
			DERIA5String derEncoded = null;
			if ("rfc822Name".equals(generalNameString) || "dNSName".equals(generalNameString) || "uniformResourceIdentifier".equals(generalNameString)) {
				derEncoded = new DERIA5String(definition.getExtensions().getSubjectAltName().getValue());
			}
			try {
				if (derEncoded != null) {
					builder.addExtension(Extension.issuerAlternativeName, definition.getExtensions().getIssuerAltName().isCritical(), derEncoded);
				}
			} catch (CertIOException e) {
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
				logger.log(Level.WARNING, "Could not build issuer alternative name extension:" + System.getProperty("line.separator") + trace.toString());
			}
		}
	}

	/**
	 * Build a set of extension objects. This set also contains one boolean for
	 * the "isCritical" flag, parse carefully!
	 * 
	 * @param {@link Object} x509extensionType The extension object on which to
	 *        work
	 * @param {@link Class<?>} bcClass The class of the x509extensionType object
	 * @return {@link Set<Object>} Set of objects containing the data as well as
	 *         the "isCritical"-flag
	 */
	private static Set<Object> buildObjectExtension(Object x509extensionType, Class<?> bcClass) {
		Boolean isCritical = false;
		Set<Object> objectSet = new HashSet<Object>();
		for (Method m : x509extensionType.getClass().getDeclaredMethods()) {
			try {
				String methodName = getMethodName(m);
				if (methodName.startsWith("is")) {
					if (methodName.startsWith("isCritical")) {
						isCritical = (Boolean) m.invoke(x509extensionType);
						objectSet.add(isCritical);
					} else {
						// remove "is"
						methodName = methodName.substring(2);
						// only need to proceed if the value is set to true
						if (m.invoke(x509extensionType) != null && (Boolean) m.invoke(x509extensionType)) {
							// check all fields until the correct value is found
							for (Field field : bcClass.getFields()) {
								String fieldname = getCleanFieldName(field);
								if (fieldname.equalsIgnoreCase(methodName)) {
									objectSet.add(field.get(x509extensionType));
								}
							}
						}
					}
				}
			} catch (IllegalAccessException e) {
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
				logger.log(Level.WARNING, "Could not load x.509 extension: Illegal Access Exception." + System.getProperty("line.separator") + trace.toString());
			} catch (IllegalArgumentException e) {
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
				logger.log(Level.WARNING,
						"Could not load x.509 extension: Illegal Argument Exception." + System.getProperty("line.separator") + trace.toString());
			} catch (InvocationTargetException e) {
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
				logger.log(Level.WARNING,
						"Could not load x.509 extension: Invocation Target Exception." + System.getProperty("line.separator") + trace.toString());
			}
		}
		return objectSet;
	}

	/**
	 * Get the clean name of the given field, e.g. <i>id_kp_xyz -> xyz </i>
	 * 
	 * @param {@link Field} f The field to parse
	 * @return {@link String} The cleaned name of the field
	 */
	private static String getCleanFieldName(Field f) {
		if (f != null && !f.getName().contains("_"))
			return f.getName();
		String fieldName = f.getName();
		int underscoreIndex = fieldName.lastIndexOf("_");
		if (underscoreIndex != -1) {
			fieldName = fieldName.substring(underscoreIndex + 1, fieldName.length());
		}
		return fieldName;
	}

	/**
	 * Create the bit mask for an extension from the definition.
	 * 
	 * @param {@link Object} x509extensionType The extension object on which to
	 *        work
	 * @param {@link Class<?>} bcClass The class of the x509extensionType object
	 * @return @link [0] if the extension is critical [1] the actual bitmask
	 */
	private static int[] buildBitmaskExtension(Object x509extensionType, Class<?> bcClass) {
		boolean isCritical = false;
		int bits = 0;
		for (Method m : x509extensionType.getClass().getDeclaredMethods()) {
			try {
				String methodName = getMethodName(m);
				if (methodName.startsWith("is")) {
					if (methodName.startsWith("isCritical")) {
						isCritical = (Boolean) m.invoke(x509extensionType);
					} else {
						// only need to proceed if the value is set to true
						if (m.invoke(x509extensionType) != null && (Boolean) m.invoke(x509extensionType)) {
							// check all fields until the correct value is found
							for (Field field : bcClass.getFields()) {
								if (field.getType().isAssignableFrom(int.class)) {
									// check if the name of the field equals the
									// name of the method
									if (methodName.substring(2, methodName.length()).toLowerCase().startsWith(field.getName().toLowerCase())) {
										bits |= field.getInt(bcClass);
										break;
									}
								}
							}
						}
					}
				}
			} catch (IllegalAccessException e) {
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
				logger.log(Level.WARNING, "Could not load x.509 extension: Illegal Access Exception." + System.getProperty("line.separator") + trace.toString());
			} catch (IllegalArgumentException e) {
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
				logger.log(Level.WARNING,
						"Could not load x.509 extension: Illegal Argument Exception." + System.getProperty("line.separator") + trace.toString());
			} catch (InvocationTargetException e) {
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
				logger.log(Level.WARNING,
						"Could not load x.509 extension: Invocation Target Exception." + System.getProperty("line.separator") + trace.toString());
			}
		}
		int[] retval = new int[2];
		retval[0] = ((isCritical) ? 1 : 0);
		retval[1] = bits;
		return retval;
	}

	/**
	 * Get the name of the given method without any pre- or suffixes (e.g.
	 * package names and <i>()</i>)
	 * 
	 * @param {@link Method} m The method to work on
	 * @return @link {@link String} The clean name of the method
	 */
	private static String getMethodName(Method m) {
		String methodName = m.toString();
		methodName = methodName.substring(0, methodName.lastIndexOf("("));
		methodName = methodName.substring((methodName.lastIndexOf(".") + 1), methodName.length());
		return methodName;
	}

	/**
	 * Build the issuer or subject string based on the provided certificate
	 * definition
	 * 
	 * @param {@link CertificateDefinition} input The certificate definition XML
	 *        to parse
	 * @param {@link boolean} issuer Whether to create an issuer or a subject
	 *        string
	 * @return {@link String} The issuer / subject string generated using the
	 *         {@link CertificateDefinition} data
	 */
	private static String buildIssuerOrSubjectString(CertificateDefinition input, boolean issuer) {
		String result = "";
		if (issuer) {
			if (input.getIssuer().getCommonName() != null) {
				result += "CN=" + input.getIssuer().getCommonName() + ", ";
			}
			if (input.getIssuer().getCountry() != null) {
				result += "C=" + input.getIssuer().getCountry() + ", ";
			}
			if (input.getIssuer().getOrganization() != null) {
				result += "O=" + input.getIssuer().getOrganization() + ", ";
			}
			if (input.getIssuer().getOrganizationalUnit() != null) {
				result += "OU=" + input.getIssuer().getOrganizationalUnit() + ", ";
			}
			if (input.getIssuer().getState() != null) {
				result += "ST=" + input.getIssuer().getState() + ", ";
			}
			if (input.getIssuer().getDistinguishedNameQualifier() != null) {
				result += "dnQualifier=" + input.getIssuer().getDistinguishedNameQualifier() + ", ";
			}
			if (input.getIssuer().getSerialNumber() != null) {
				result += "serialNumber=" + input.getIssuer().getSerialNumber() + ", ";
			}
			if (input.getIssuer().getLocality() != null) {
				result += "L=" + input.getIssuer().getLocality() + ", ";
			}
			if (input.getIssuer().getTitle() != null) {
				result += "title=" + input.getIssuer().getTitle() + ", ";
			}
			if (input.getIssuer().getSurname() != null) {
				result += "SN=" + input.getIssuer().getSurname() + ", ";
			}
			if (input.getIssuer().getGivenName() != null) {
				result += "GN=" + input.getIssuer().getGivenName() + ", ";
			}
			if (input.getIssuer().getPseudonym() != null) {
				result += "pseudonym=" + input.getIssuer().getPseudonym() + ", ";
			}
			if (input.getIssuer().getGenerationQualifier() != null) {
				result += "generationQualifier=" + input.getIssuer().getGenerationQualifier() + ", ";
			}
			if (input.getIssuer().getInitials() != null) {
				result += "initials=" + input.getIssuer().getInitials() + ", ";
			}
		}
		// creating subject string
		else {
			if (input.getSubject().getCommonName() != null) {
				result += "CN=" + input.getSubject().getCommonName() + ", ";
			}
			if (input.getSubject().getCountry() != null) {
				result += "C=" + input.getSubject().getCountry() + ", ";
			}
			if (input.getSubject().getOrganization() != null) {
				result += "O=" + input.getSubject().getOrganization() + ", ";
			}
			if (input.getSubject().getOrganizationalUnit() != null) {
				result += "OU=" + input.getSubject().getOrganizationalUnit() + ", ";
			}
			if (input.getSubject().getState() != null) {
				result += "ST=" + input.getSubject().getState() + ", ";
			}
			if (input.getSubject().getDistinguishedNameQualifier() != null) {
				result += "dnQualifier=" + input.getSubject().getDistinguishedNameQualifier() + ", ";
			}
			if (input.getSubject().getSerialNumber() != null) {
				result += "serialNumber=" + input.getSubject().getSerialNumber() + ", ";
			}
			if (input.getSubject().getLocality() != null) {
				result += "L=" + input.getSubject().getLocality() + ", ";
			}
			if (input.getSubject().getTitle() != null) {
				result += "title=" + input.getSubject().getTitle() + ", ";
			}
			if (input.getSubject().getSurname() != null) {
				result += "SN=" + input.getSubject().getSurname() + ", ";
			}
			if (input.getSubject().getGivenName() != null) {
				result += "GN=" + input.getSubject().getGivenName() + ", ";
			}
			if (input.getSubject().getPseudonym() != null) {
				result += "pseudonym=" + input.getSubject().getPseudonym() + ", ";
			}
			if (input.getSubject().getGenerationQualifier() != null) {
				result += "generationQualifier=" + input.getSubject().getGenerationQualifier() + ", ";
			}
			if (input.getSubject().getInitials() != null) {
				result += "initials=" + input.getSubject().getInitials() + ", ";
			}
		}
		// remove the trailing comma
		result = result.substring(0, result.length() - 2);
		return result;
	}

}
