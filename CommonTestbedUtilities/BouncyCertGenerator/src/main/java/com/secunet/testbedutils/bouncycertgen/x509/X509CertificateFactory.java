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
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import com.secunet.testbedutils.bouncycertgen.GeneratedCertificate;
import com.secunet.testbedutils.bouncycertgen.KeyGenerator;
import com.secunet.testbedutils.bouncycertgen.x509.CRLDistributionPointsType.DistributionPoint;
import com.secunet.testbedutils.bouncycertgen.x509.GeneralNamesType.GeneralName;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.jce.provider.X509CertParser;
import org.bouncycastle.x509.util.StreamParsingException;

/**
 * @author Lukasz Kubik, secunet
 *
 */
public class X509CertificateFactory {

    private enum AlgorithmID {

        ECDSA,
        RSA,
        DSA,
    }

    private static Logger logger = Logger.getLogger(X509CertificateFactory.class.getName());

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
     *
     * @param {@link CertificateDefinition} certificateDefinition - The
     * definition file for the certificate
     * @return {@link CertKeysPair}
     */
    public static GeneratedCertificate createX509(CertificateDefinition certificateDefinition) {
        return createX509(certificateDefinition, null, null);
    }

    /**
     * Create a x509 certificate using the provided data.
     * <p>
     * - If no signature algorithm is provided, SHA256withRSA with a bit length
     * of 2048 bit will be used.
     * <p>
     * - If no key file is provided, a new key pair will be created
     * <p>
     * - If no begin date is provided, relative date will be used
     * <p>
     * - If no expiration date is provided, it will be set to 6000 days from now
     *
     * @param {@link CertificateDefinition} certificateDefinition - The
     * definition file for the certificate
     * @param {@link GeneratedCertificate} signer - Signer of the certificate
     * (if null, certificate will be self signed)
     * @return {@link CertKeysPair}
     */
    public static GeneratedCertificate createX509(CertificateDefinition certificateDefinition, GeneratedCertificate signer) {
        return createX509(certificateDefinition, signer, null);
    }

    /**
     * Create a x509 certificate using the provided data.
     * <p>
     * - If no signature algorithm is provided, SHA256withRSA with a bit length
     * of 2048 bit will be used.
     * <p>
     * - If no key file is provided, a new key pair will be created
     * <p>
     * - If no begin date is provided, relative date will be used
     * <p>
     * - If no expiration date is provided, it will be set to 6000 days from now
     *
     * @param {@link CertificateDefinition} certificateDefinition - The
     * definition file for the certificate
     * @param relativeDate - Relative date of certificate (if null, current date
     * will be used)
     * @return {@link CertKeysPair}
     */
    public static GeneratedCertificate createX509(CertificateDefinition certificateDefinition, Date relativeDate) {
        return createX509(certificateDefinition, null, relativeDate);
    }

    /**
     * Create a x509 certificate using the provided data.
     * <p>
     * - If no signature algorithm is provided, SHA256withRSA with a bit length
     * of 2048 bit will be used.
     * <p>
     * - If no key file is provided, a new key pair will be created
     * <p>
     * - If no begin date is provided, relative date will be used
     * <p>
     * - If no expiration date is provided, it will be set to 6000 days from now
     *
     * @param {@link CertificateDefinition} certificateDefinition - The
     * definition file for the certificate
     * @param {@link GeneratedCertificate} signer - Signer of the certificate
     * (if null, certificate will be self signed)
     * @param relativeDate - Relative date of certificate (if null, current date
     * will be used)
     * @return {@link CertKeysPair}
     */
    public static GeneratedCertificate createX509(CertificateDefinition certificateDefinition, GeneratedCertificate signer, Date relativeDate) {
        // set relative date to current date if not set.
        if (relativeDate == null) {
            relativeDate = new Date(System.currentTimeMillis());
        }

        // set up certificate attributes
        String issuer = buildIssuerOrSubjectString(certificateDefinition, true);
        String subject = buildIssuerOrSubjectString(certificateDefinition, false);

        X500Principal issuerX509;
        if (signer != null && signer.getCertificate() != null) {
            issuerX509 = signer.getCertificate().getSubjectX500Principal();
        } else {
            issuerX509 = new X500Principal(issuer);
        }
        X500Principal subjectX509 = new X500Principal(subject);
        
        // get serial number
        UUID rndUUID = UUID.randomUUID();
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(rndUUID.getMostSignificantBits());
        bb.putLong(rndUUID.getLeastSignificantBits());
        BigInteger serial = new BigInteger(bb.array());
        if (certificateDefinition.getSerialNumber() != null) {
            serial = certificateDefinition.getSerialNumber();
        } else if (certificateDefinition.getSerialNumberHex() != null) {
            serial = new BigInteger(certificateDefinition.getSerialNumberHex());
        }

        // check if a start date was provided      
        Date notBefore;
        if (certificateDefinition.getNotBefore() != null) {
            notBefore = certificateDefinition.getNotBefore().toGregorianCalendar().getTime();
        } else if (certificateDefinition.getNotBeforeOffset() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(relativeDate);
            cal.add(Calendar.DATE, certificateDefinition.getNotBeforeOffset());
            notBefore = cal.getTime();
        } else {
            notBefore = relativeDate;
        }

        // check if a an expiration date was provided
        Date notAfter;
        if (certificateDefinition.getNotAfter() != null) {
            notAfter = certificateDefinition.getNotAfter().toGregorianCalendar().getTime();
        } else if (certificateDefinition.getNotAfterOffset() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(notBefore);
            cal.add(Calendar.DATE, certificateDefinition.getNotAfterOffset());
            notAfter = cal.getTime();
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(notBefore);
            cal.add(Calendar.DATE, 6000);
            notAfter = cal.getTime();
        }

        // generate/load key pair for certificate
        KeyPair pair = null;
        // check if a key file was provided
        if (certificateDefinition.getKeyFile() != null) {
            File f = new File(certificateDefinition.getKeyFile().getValue());
            String password = certificateDefinition.getKeyFile().getPassword();
            pair = loadKeyPairFromFile(f, password);
        }

        if (pair == null) {
            AlgorithmID keyType = null;
            int keyLength = 0;
            String curveName = "";
            // check if a signature algorithm was chosen, if not default to RSA
            if (certificateDefinition.getAlgorithmID() != null) {
                if (certificateDefinition.getAlgorithmID().getECDSA() != null) {
                    keyType = AlgorithmID.ECDSA;
                    curveName = certificateDefinition.getAlgorithmID().getECDSA().getCurve();
                } else if (certificateDefinition.getAlgorithmID().getRSA() != null) {
                    keyType = AlgorithmID.RSA;
                    keyLength = certificateDefinition.getAlgorithmID().getRSA().getKeylength();
                } else if (certificateDefinition.getAlgorithmID().getDSA() != null) {
                    keyType = AlgorithmID.DSA;
                    keyLength = certificateDefinition.getAlgorithmID().getDSA().getKeylength();
                }
            } else {
                keyType = AlgorithmID.RSA;
                keyLength = 2048;
            }

            if (keyType != null) {
                logger.log(Level.FINE, "Generating key pair for " + certificateDefinition.getName() + " ...");
                switch (keyType) {
                    case ECDSA:
                        pair = KeyGenerator.generateECPair(curveName);
                        break;
                    case RSA:
                        pair = KeyGenerator.generateRSAPair(keyLength);
                        break;
                    case DSA:
                        pair = KeyGenerator.generateDSAPair(keyLength);
                        break;
                }
            }
        }

        // set up the signature parameters
        KeyPair signpair;
        String signatureAlgorithm = null;
        if (signer == null) {
            signpair = pair;
            switch (AlgorithmID.valueOf(signpair.getPrivate().getAlgorithm())) {
                case ECDSA:
                    signatureAlgorithm = certificateDefinition.getAlgorithmID().getECDSA().getValue().toString().replace("_", "");
                    break;
                case RSA:
                    signatureAlgorithm = certificateDefinition.getAlgorithmID().getRSA().getValue().toString().replace("_", "");
                    break;
                case DSA:
                    signatureAlgorithm = certificateDefinition.getAlgorithmID().getDSA().getValue().toString().replace("_", "");
                    break;
            }
        } else {
            signpair = signer.getKeyPair();
            signatureAlgorithm = signer.getCertificate().getSigAlgName();
        }

        ContentSigner sigGen = null;
        try {
            JcaContentSignerBuilder signerBuilder = new JcaContentSignerBuilder(signatureAlgorithm);
            sigGen = signerBuilder.setProvider("BC").build(signpair.getPrivate());
        } catch (Exception e) {
            StringWriter trace = new StringWriter();
            e.printStackTrace(new PrintWriter(trace));
            logger.log(Level.SEVERE, "The signature could not be generated:" + System.getProperty("line.separator") + trace.toString());
        }

        if (sigGen == null) {
            return null;
        }

        // build the certificate
        logger.log(Level.FINE, "Building certificate for " + certificateDefinition.getName() + " ...");
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

        if (certificate == null) {
            return null;
        }

        return new GeneratedCertificate(certificateDefinition, certificate, pair);
    }

    /**
     * Loads an X.509 certificate and key from files for an existing certificate
     * description.
     *
     * @param certificateDefinition - The certificate description.
     * @param cert - The PEM encoded certificate file.
     * @param key - The PEM encoded key file.
     * @param password - Password for key file.
     * @return GeneratedCertificate for loaded certificate opject.
     */
    public static GeneratedCertificate loadX509(CertificateDefinition certificateDefinition, File cert, File key, String password) {
        KeyPair pair = loadKeyPairFromFile(key, password);

        X509Certificate certificate = null;
        try {
            try (FileInputStream isCert = new FileInputStream(cert)) {
                X509CertParser certParser = new X509CertParser();
                certParser.engineInit(isCert);
                certificate = (X509Certificate) certParser.engineRead();
            }
        } catch (IOException | StreamParsingException e) {
            logger.log(Level.SEVERE, "Error while loading certificate from file " + cert.getAbsolutePath() + ": " + e.getMessage());
        }

        return new GeneratedCertificate(certificateDefinition, certificate, pair);
    }

    /**
     * Sets logger.
     *
     * @param logger The logger.
     */
    public static void setLogger(Logger logger) {
        X509CertificateFactory.logger = logger;
    }

    /**
     * Loads a PEM encoded key pair from file.
     *
     * @param keyFile - The Key file.
     * @param password - Password for key file.
     *
     * @return Key pair.
     */
    private static KeyPair loadKeyPairFromFile(File keyFile, String password) {
        KeyPair pair = null;
        if (keyFile != null && keyFile.exists()) {
            PEMParser parser = null;
            try {
                parser = new PEMParser(new FileReader(keyFile));
                Object object = parser.readObject();
                JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
                // without PKCS#8, but with password
                if (object instanceof PEMEncryptedKeyPair) {
                    if (password != null) {
                        PEMDecryptorProvider decrypter = new JcePEMDecryptorProviderBuilder().build(password.toCharArray());
                        pair = converter.getKeyPair(((PEMEncryptedKeyPair) object).decryptKeyPair(decrypter));
                    } else {
                        logger.log(Level.SEVERE, "No password provided for the key file " + keyFile.getAbsolutePath() + ". A new key pair will be generated instead.");
                    }
                } // without PKCS#8 or password
                else if (object instanceof PEMKeyPair) {
                    pair = converter.getKeyPair((PEMKeyPair) object);
                } else {
                    logger.log(Level.SEVERE, "The provided key file format for the signature could not be recognized. Filename: " + keyFile.getAbsolutePath()
                            + ". A new key pair will be generated instead.");
                }
            } catch (IOException e) {
                StringWriter trace = new StringWriter();
                e.printStackTrace(new PrintWriter(trace));
                logger.log(Level.SEVERE, "Could not read key file " + keyFile.getAbsolutePath() + ". Error:" + System.getProperty("line.separator") + trace.toString());
            } finally {
                try {
                    if (parser != null) {
                        parser.close();
                    }
                } catch (IOException e) {
                    StringWriter trace = new StringWriter();
                    e.printStackTrace(new PrintWriter(trace));
                    logger.log(Level.WARNING, "Error while reading key file " + keyFile.getAbsolutePath() + ". Error:" + System.getProperty("line.separator") + trace.toString());
                }
            }
        }

        return pair;
    }

    /**
     * Build the extensions as defined in 'definitions' and add them to the
     * certificate builder
     *
     * @param {@link CertificateDefinition} definition - The definition file for
     * the certificate
     * @param {@link X509v3CertificateBuilder} builder - The certificate builder
     * instance
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
            ASN1Sequence generalNames = buildGeneralNames(definition.getExtensions().getSubjectAltName());
            try {
                if (generalNames != null) {
                    builder.addExtension(Extension.subjectAlternativeName, definition.getExtensions().getSubjectAltName().isCritical(), generalNames);
                }
            } catch (CertIOException e) {
                StringWriter trace = new StringWriter();
                e.printStackTrace(new PrintWriter(trace));
                logger.log(Level.WARNING, "Could not build subject alternative name extension:" + System.getProperty("line.separator") + trace.toString());
            }
        }
        // issuerAltName
        if (definition.getExtensions() != null && definition.getExtensions().getIssuerAltName() != null) {
            ASN1Sequence generalNames = buildGeneralNames(definition.getExtensions().getIssuerAltName());
            try {
                if (generalNames != null) {
                    builder.addExtension(Extension.issuerAlternativeName, definition.getExtensions().getIssuerAltName().isCritical(), generalNames);
                }
            } catch (CertIOException e) {
                StringWriter trace = new StringWriter();
                e.printStackTrace(new PrintWriter(trace));
                logger.log(Level.WARNING, "Could not build issuer alternative name extension:" + System.getProperty("line.separator") + trace.toString());
            }
        }
        // cRLDistributionPoints
        if (definition.getExtensions() != null && definition.getExtensions().getCRLDistributionPoints() != null) {
            ASN1EncodableVector distributionPointsASN1 = null;
            List<DistributionPoint> distributionPoints = definition.getExtensions().getCRLDistributionPoints().getDistributionPoint();
            if (distributionPoints != null) {
                distributionPointsASN1 = new ASN1EncodableVector();
                for (DistributionPoint DistributionPoint : distributionPoints) {
                    ASN1EncodableVector DistributionPointASN1 = new ASN1EncodableVector();
                    
                    // distributionPoint [0]
                    DistributionPointType distributionPoint = DistributionPoint.getDistributionPoint();
                    if (distributionPoint != null) {
                        ASN1EncodableVector distributionPointASN1 = new ASN1EncodableVector();
                        
                        // fullName [0]
                        ASN1Sequence fullNameASN1 = buildGeneralNames(distributionPoint.getFullName());
                        if (fullNameASN1 != null) {
                            distributionPointASN1.add(new DERTaggedObject(false, 0, fullNameASN1));
                        }
                        
                        // nameRelativeToCRLIssuer [1]
                        // Not implemented yet.

                        DistributionPointASN1.add(new DERTaggedObject(false, 0, new DERSequence(distributionPointASN1)));
                    }
                    
                    // reasons [1]
                    // Not implemented yet.
                    
                    // cRLIssuer [2]
                    // Not implemented yet.
                    
                    distributionPointsASN1.add(new DERSequence(DistributionPointASN1));
                }
            }

            try {
                if (distributionPointsASN1 != null) {
                    builder.addExtension(Extension.cRLDistributionPoints, definition.getExtensions().getCRLDistributionPoints().isCritical(), new DERSequence(distributionPointsASN1));
                }
            } catch (CertIOException e) {
                StringWriter trace = new StringWriter();
                e.printStackTrace(new PrintWriter(trace));
                logger.log(Level.WARNING, "Could not build issuer alternative name extension:" + System.getProperty("line.separator") + trace.toString());
            }
        }
    }

    /**
     * Builds ASN1 sequence from given generalNames object.
     *
     * @param generalNames - generalNames Object.
     * @return ASN1 Sequence
     */
    private static ASN1Sequence buildGeneralNames(GeneralNamesType generalNames) {
        ASN1EncodableVector generalNamesASN1 = null;
        if (generalNames != null) {
            generalNamesASN1 = new ASN1EncodableVector();
            List<GeneralName> generalNameList = generalNames.getGeneralName();
            for (GeneralName generalName : generalNameList) {
                GeneralNameTypeType type = generalName.getType();
                if (type != null) {
                    String value = type.value();
                    if (value != null) {
                        switch (value) {
                            case "rfc822Name":
                                generalNamesASN1.add(new DERTaggedObject(false, 1, new DERIA5String(generalName.getValue())));
                                break;
                            case "dNSName":
                                generalNamesASN1.add(new DERTaggedObject(false, 2, new DERIA5String(generalName.getValue())));
                                break;
                            case "uniformResourceIdentifier":
                                generalNamesASN1.add(new DERTaggedObject(false, 6, new DERIA5String(generalName.getValue())));
                                break;
                        }
                    }
                }
            }
        }

        if (generalNamesASN1 != null) {
            return new DERSequence(generalNamesASN1);
        }
        
        return null;
    }

    /**
     * Build a set of extension objects. This set also contains one boolean for
     * the "isCritical" flag, parse carefully!
     *
     * @param {@link Object} x509extensionType The extension object on which to
     * work
     * @param {@link Class<?>} bcClass The class of the x509extensionType object
     * @return {@link Set<Object>} Set of objects containing the data as well as
     * the "isCritical"-flag
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
        if (f != null && !f.getName().contains("_")) {
            return f.getName();
        }
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
     * work
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
     * to parse
     * @param {@link boolean} issuer Whether to create an issuer or a subject
     * string
     * @return {@link String} The issuer / subject string generated using the
     * {@link CertificateDefinition} data
     */
    public static String buildIssuerOrSubjectString(CertificateDefinition input, boolean issuer) {
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
        } // creating subject string
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
