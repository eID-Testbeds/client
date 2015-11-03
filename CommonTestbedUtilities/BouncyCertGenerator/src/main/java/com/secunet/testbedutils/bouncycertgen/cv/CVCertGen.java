package com.secunet.testbedutils.bouncycertgen.cv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import javax.xml.XMLConstants;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.secunet.testbedutils.cvc.cvcertificate.CVAuthorization;
import com.secunet.testbedutils.cvc.cvcertificate.CVAuthorizationAT;
import com.secunet.testbedutils.cvc.cvcertificate.CVAuthorizationIS;
import com.secunet.testbedutils.cvc.cvcertificate.CVAuthorizationST;
import com.secunet.testbedutils.cvc.cvcertificate.CVCertificate;
import com.secunet.testbedutils.cvc.cvcertificate.CVExtension;
import com.secunet.testbedutils.cvc.cvcertificate.CVExtensionData;
import com.secunet.testbedutils.cvc.cvcertificate.CVExtensionDataList;
import com.secunet.testbedutils.cvc.cvcertificate.CVExtensionType;
import com.secunet.testbedutils.cvc.cvcertificate.CertHolderRole;
import com.secunet.testbedutils.cvc.cvcertificate.CertificateDescription;
import com.secunet.testbedutils.cvc.cvcertificate.DataBuffer;
import com.secunet.testbedutils.cvc.cvcertificate.ECCCurves;
import com.secunet.testbedutils.cvc.cvcertificate.PrivateKeySource;
import com.secunet.testbedutils.cvc.cvcertificate.PublicKeySource;
import com.secunet.testbedutils.cvc.cvcertificate.TAAlgorithm;

/**
 * @class CVCertGen
 * @brief XML based CV certificate generation.
 * @author neunkirchen.bernd
 *
 */
public class CVCertGen {

    private static final String CVCONFIG_SCHEMA_FILE_NAME = "cv_schema.xsd";

    private static Logger logger = LogManager.getRootLogger();

    protected Schema m_xsd = null;

    protected File baseDir = null;
    private Date m_relDate = null;

    /**
     * Initializes CV certificate generator.
     */
    public CVCertGen() {
        this(null);
    }

    /**
     * Initializes CV certificate generator.
     *
     * @param baseDir Base directory for certificate generation.
     */
    public CVCertGen(File baseDir) {
        if (baseDir != null && baseDir.exists()) {
            this.baseDir = baseDir;
        } else {
            this.baseDir = new File(System.getProperty("user.dir"));
        }

        // loading xml schema
        Source xsd = new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream(CVCONFIG_SCHEMA_FILE_NAME));
        if (xsd != null) {
            SchemaFactory sF = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            try {
                m_xsd = sF.newSchema(xsd);
            } catch (SAXException e) {
                logger.warn(e.getMessage());
                m_xsd = null;
            }
        } else {
            logger.warn("Unable to locate configuration schema.");
        }

        // set now as relative date
        m_relDate = new Date();

        // add provider
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Sets logger.
     *
     * @param logger The logger.
     */
    public static void setLogger(Logger logger) {
        CVCertGen.logger = logger;
    }

    /**
     * Checks if given XML file is valid to CV certificate configuration schema.
     *
     * @param xmlFilePath Given XML file.
     * @return True if valid, else false.
     */
    public boolean checkXML(String xmlFilePath) {
        return checkXML(new File(xmlFilePath));
    }

    /**
     * Checks if given XML file is valid to CV certificate configuration schema.
     *
     * @param xmlFile Given XML file.
     * @return True if valid, else false.
     */
    public boolean checkXML(File xmlFile) {
        boolean valid = false;

        if (m_xsd != null) {
            Source xml = new StreamSource(xmlFile);

            try {
                m_xsd.newValidator().validate(xml);
                valid = true;
            } catch (IOException | SAXException e) {
                logger.warn(e.getMessage());
            }
        } else {
            logger.error("No XSD set to validate XML configuration file.");
        }

        return valid;
    }

    /**
     * Generates CV certificates from XML structure.
     *
     * @param xmlFilePath Path to XML file.
     * @param save Save certificate in files?
     * @return CV certificate list.
     * @throws Exception
     */
    public ArrayList<CVCertificate> generateFromXML(String xmlFilePath, boolean save) throws Exception {
        return generateFromXML(new File(xmlFilePath), save);
    }

    /**
     * Generates CV certificates from XML structure.
     *
     * @param xmlFile XML file.
     * @param save Save certificate in files?
     * @return CV certificate list.
     * @throws Exception
     */
    public ArrayList<CVCertificate> generateFromXML(File xmlFile, boolean save) throws Exception {
        Document xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
        return generateFromXML(xml, save);
    }

    /**
     * Generates CV certificates from XML structure.
     *
     * @param xml XML document.
     * @param save Save certificate in files?
     * @return CV certificate list.
     */
    public ArrayList<CVCertificate> generateFromXML(Document xml, boolean save) {
        ArrayList<CVCertificate> certs = null;
        Hashtable<String, KeyInfo> keys = null;

        if (xml != null) {
            certs = new ArrayList<CVCertificate>();

            NodeList rootChilds = xml.getFirstChild().getChildNodes();
            for (int i = 0; i < rootChilds.getLength(); i++) {
                switch (rootChilds.item(i).getNodeName()) {
                    case "keys": // checking/generating keys
                        keys = new Hashtable<String, KeyInfo>();

                        NodeList keysChilds = rootChilds.item(i).getChildNodes();
                        for (int j = 0; j < keysChilds.getLength(); j++) {
                            if (keysChilds.item(j).getNodeName().equals("key")) {
                                KeyInfo key = null;
                                try {
                                    key = generateKeyFromNode(keysChilds.item(j), save);
                                } catch (Exception e) {
                                    logger.warn(e.getMessage());
                                }

                                if (key != null) {
                                    keys.put(key.getName(), key);
                                    logger.debug("added keys " + key.getName());
                                } else {
                                    logger.warn("unable to generated key ...");
                                }
                            }
                        }
                        break;
                    case "cert": // generating certificates
                        CVCertificate cert = null;
                        try {
                            cert = generateCertFromNode(rootChilds.item(i), keys, save);
                        } catch (Exception e) {
                            logger.warn(e.getMessage());
                        }

                        if (cert != null) {
                            certs.add(cert);
                            logger.debug("added certificate (Holder Reference) " + cert.getCertHolderRef());
                        } else {
                            logger.warn("unable to generated certificate ...");
                        }
                        break;
                }
            }
        }

        return certs;
    }

    /**
     * Generates (or loads) key pairs from XML node.
     *
     * @param node XML node.
     * @param save Save keys in files?
     * @return Key pair.
     * @throws Exception
     */
    protected KeyInfo generateKeyFromNode(Node node, boolean save) throws Exception {
        KeyInfo key = null;
        boolean create = false;

        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            if (attributes.item(i).getNodeName().equals("name")) {
                key = new KeyInfo(attributes.item(i).getNodeValue());
            } else if (attributes.item(i).getNodeName().equals("create")) {
                create = parseXSBoolean(attributes.item(i).getNodeValue());
            }
        }

        // getting parameter
        if (key != null) {
            NodeList keyChilds = node.getChildNodes();
            for (int i = 0; i < keyChilds.getLength(); i++) {
                switch (keyChilds.item(i).getNodeName()) {
                    case "filePrivateKey":
                        key.setFilePrivateKey(keyChilds.item(i).getTextContent());
                        break;
                    case "filePublicKey":
                        key.setFilePublicKey(keyChilds.item(i).getTextContent());
                        break;
                    case "algorithm":
                        key.setAlgorithm(keyChilds.item(i).getTextContent());
                        break;
                    case "ecdsa": // ECDSA parameter
                        key.setType(keyChilds.item(i).getNodeName().toUpperCase());
                        key.setKeyParamSpec(parseECAlgorithm(keyChilds.item(i).getTextContent()));
                        break;
                    case "rsa": // RSA parameter
                        key.setType(keyChilds.item(i).getNodeName().toUpperCase());
                        BigInteger rsaPublicExpo = RSAKeyGenParameterSpec.F4; // default
                        int rsaKeyLength = 0;

                        NodeList rsaChilds = keyChilds.item(i).getChildNodes();
                        for (int j = 0; j < rsaChilds.getLength(); j++) {
                            switch (rsaChilds.item(j).getNodeName()) {
                                case "publicExpo":
                                    rsaPublicExpo = new BigInteger(rsaChilds.item(j).getTextContent());
                                    break;
                                case "length":
                                    String str = rsaChilds.item(j).getTextContent();
                                    rsaKeyLength = Integer.parseInt(str);
                            }
                        }

                        key.setKeyParamSpec(new RSAKeyGenParameterSpec(rsaKeyLength, rsaPublicExpo));
                        break;
                }
            }

            if (create) {
                // generating key pair
                KeyPairGenerator keyGen = KeyPairGenerator.getInstance(key.getType(), "BC");

                SecureRandom random = new SecureRandom();
                keyGen.initialize(key.getKeyParamSpec(), random);
                key.setKeyPair(keyGen.generateKeyPair());

                if (save) {
                    // saving keys
                    File privKeyFile = new File(baseDir, key.getFilePrivateKey());
                    if (!privKeyFile.getParentFile().exists()) {
                        privKeyFile.getParentFile().mkdirs();
                    }

                    DataBuffer privKey = new DataBuffer(key.getKeyPair().getPrivate().getEncoded());
                    privKey.writeToFile(privKeyFile.getAbsolutePath());

                    if (key.getFilePublicKey() != null) {
                        File pubKeyFile = new File(baseDir, key.getFilePublicKey());
                        if (!pubKeyFile.getParentFile().exists()) {
                            pubKeyFile.getParentFile().mkdirs();
                        }

                        DataBuffer pubKey = new DataBuffer(key.getKeyPair().getPublic().getEncoded());
                        pubKey.writeToFile(pubKeyFile.getAbsolutePath());
                    }

                    logger.debug("key " + key.getName() + " saved.");
                }
            } else {
                // loading keys
                KeyFactory kf = KeyFactory.getInstance(key.getType(), "BC");
                PrivateKey privKey = null;
                PublicKey pubKey = null;

                File privKeyFile = new File(baseDir, key.getFilePrivateKey());
                if (privKeyFile.exists()) {
                    DataBuffer dbKey = DataBuffer.readFromFile(privKeyFile.getAbsolutePath());
                    if (dbKey != null) {
                        privKey = kf.generatePrivate(new PKCS8EncodedKeySpec(dbKey.toByteArray()));
                    } else {
                        logger.warn("Unable to load private key of " + key.getName() + ".");
                        return null;
                    }
                } else {
                    logger.warn("Private key of " + key.getName() + " does not exist.");
                    return null;
                }

                if (key.getFilePublicKey() != null) {
                    File pubKeyFile = new File(baseDir, key.getFilePublicKey());
                    if (pubKeyFile.exists()) {
                        DataBuffer dbKey = DataBuffer.readFromFile(pubKeyFile.getAbsolutePath());
                        if (dbKey != null) {
                            pubKey = kf.generatePublic(new X509EncodedKeySpec(dbKey.toByteArray()));
                        } else {
                            logger.warn("Unable to load public key of " + key.getName() + ".");
                            return null;
                        }
                    } else {
                        logger.warn("Public key of " + key.getName() + " does not exist.");
                        return null;
                    }
                } else {
                    pubKey = null;
                }

                key.setKeyPair(new KeyPair(pubKey, privKey));
            }
        }

        return key;
    }

    /**
     * Generates CV certificate from XML node.
     *
     * @param node XML node.
     * @param keys Table of keys used for certificate generation.
     * @param save Save certificate in file?
     * @return CV certificate.
     * @throws Exception
     */
    protected CVCertificate generateCertFromNode(Node node, Hashtable<String, KeyInfo> keys, boolean save) throws Exception {
        CVCertificate cert = new CVCertificate();
        File outputFile = null;
        KeyInfo signKey = null;
        KeyInfo publicKey = null;
        Node certDescNode = null;
        ArrayList<DataBuffer> sectorKeys = new ArrayList<DataBuffer>();

        boolean createAdditionalHexFile = false;

        NodeList certChilds = node.getChildNodes();
        for (int i = 0; i < certChilds.getLength(); i++) {
            switch (certChilds.item(i).getNodeName()) {
                case "profileId":
                    cert.setProfileId(Integer.parseInt(certChilds.item(i).getTextContent()));
                    break;
                case "certAuthRef":
                    cert.setCertAuthRef(certChilds.item(i).getTextContent());
                    break;
                case "publicKey": {
                    boolean domainParam = false;
                    NamedNodeMap attributes = certChilds.item(i).getAttributes();
                    for (int j = 0; j < attributes.getLength(); j++) {
                        if (attributes.item(j).getNodeName().equals("domainParam")) {
                            domainParam = parseXSBoolean(attributes.item(j).getNodeValue());
                        }
                    }

                    String str = certChilds.item(i).getTextContent();
                    publicKey = keys.get(str);
                    if (publicKey != null) {
                        cert.getPublicKey().setAlgorithm(parseTAAlgorithm(publicKey.getAlgorithm()));
                        cert.getPublicKey().setIncludeDomainParam(domainParam);

                        PublicKey key = publicKey.getKeyPair().getPublic();
                        cert.getPublicKey().setKeySource(new PublicKeySource(key));
                    } else {
                        logger.warn("Public key not found in key list!");
                        return null;
                    }
                }
                break;
                case "certHolderRef":
                    cert.setCertHolderRef(certChilds.item(i).getTextContent());
                    break;
                case "certHolderAuth": {
                    CVAuthorization usedAuth = null;
                    DataBuffer oid = null;

                    NamedNodeMap attributes = certChilds.item(i).getAttributes();
                    for (int j = 0; j < attributes.getLength(); j++) {
                        if (attributes.item(j).getNodeName().equals("type")) {
                            switch (attributes.item(j).getNodeValue()) {
                                case "AT":
                                    usedAuth = new CVAuthorizationAT();
                                    break;
                                case "IS":
                                    usedAuth = new CVAuthorizationIS();
                                    break;
                                case "ST":
                                    usedAuth = new CVAuthorizationST();
                                    break;
                            }
                        } else if (attributes.item(j).getNodeName().equals("forceOID")) {
                            oid = new DataBuffer();
                            oid.fromHexBinary(attributes.item(j).getNodeValue());
                        }
                    }

                    cert.getCertHolderAuth().setAuth(usedAuth);

                    if (oid != null) {
                        cert.getCertHolderAuth().getAuth().setInstanceOid(oid);
                    }

                    NodeList authChilds = certChilds.item(i).getChildNodes();
                    for (int j = 0; j < authChilds.getLength(); j++) {
                        switch (authChilds.item(j).getNodeName()) {
                            case "role":
                                switch (authChilds.item(j).getTextContent()) {
                                    case "CVCA":
                                        cert.getCertHolderAuth().getAuth().setRole(CertHolderRole.CVCA);
                                        break;
                                    case "DV_DOMESTIC":
                                        cert.getCertHolderAuth().getAuth().setRole(CertHolderRole.DVdomestic);
                                        break;
                                    case "DV_FOREIGN":
                                        cert.getCertHolderAuth().getAuth().setRole(CertHolderRole.DVforeign);
                                        break;
                                    case "TERMINAL":
                                        cert.getCertHolderAuth().getAuth().setRole(CertHolderRole.Terminal);
                                        break;
                                }
                                break;
                            case "writeDG17":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Write_DG17, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "writeDG18":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Write_DG18, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "writeDG19":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Write_DG19, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "writeDG20":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Write_DG20, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "writeDG21":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Write_DG21, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG1":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG1, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG2":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG2, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG3":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG3, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG4":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG4, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG5":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG5, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG6":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG6, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG7":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG7, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG8":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG8, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG9":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG9, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG10":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG10, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG11":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG11, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG12":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG12, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG13":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG13, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG14":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG14, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG15":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG15, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG16":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG16, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG17":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG17, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG18":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG18, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG19":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG19, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG20":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG20, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG21":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG21, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "installQualifiedCertificate":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_InstallQulifiedCertificate, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "installCertificate":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_InstallCertificate, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "pinManagement":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_PINManagement, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "canAllowed":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_CANAllowed, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "privilegedTerminal":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_PrivilegedTerminal, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "restrictedIdentification":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_RestrictedIdentification, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "communityIDVerification":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_CommunityIDVerification, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "ageVerification":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_AgeVerification, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readEPassDG3":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationIS.auth_Read_DG3, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readEPassDG4":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationIS.auth_Read_DG4, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "generateQualifiedElectronicSignature":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationST.auth_GenerateQualifiedSignature, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "generateElectronicSignature":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationST.auth_GenerateSignature, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                        }
                    }
                }
                break;
                case "effDate": {
                    Date date = DatatypeConverter.parseDate(certChilds.item(i).getTextContent()).getTime();
                    cert.getEffDate().setDate(date);
                }
                break;
                case "effDateOffset": {
                    int days = Integer.parseInt(certChilds.item(i).getTextContent());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(m_relDate); // Use given date as relative date
                    cal.add(Calendar.DATE, days);
                    cert.getEffDate().setDate(cal.getTime());
                }
                break;
                case "expDate": {
                    Date date = DatatypeConverter.parseDate(certChilds.item(i).getTextContent()).getTime();
                    cert.getExpDate().setDate(date);
                }
                break;
                case "expDateOffset": {
                    int days = Integer.parseInt(certChilds.item(i).getTextContent());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(cert.getEffDate().getDate()); // Use effDate as relative date
                    cal.add(Calendar.DATE, days);
                    cert.getExpDate().setDate(cal.getTime());
                }
                break;
                case "extensions": {
                    NodeList extensionsChilds = certChilds.item(i).getChildNodes();
                    for (int j = 0; j < extensionsChilds.getLength(); j++) {
                        switch (extensionsChilds.item(j).getNodeName()) {
                            case "description":
                                certDescNode = extensionsChilds.item(j); // If commCerts shall be integrated in certificate description, not possible until signKey fetched. So save for later processing ...
                                break;
                            case "terminalSector":
                                sectorKeys.add(getSectorKeyFromNode(extensionsChilds.item(j)));
                                break;
                        }
                    }
                }
                break;
                case "signKey": {
                    signKey = keys.get(certChilds.item(i).getTextContent());
                    if (signKey != null) {
                        cert.getSignKey().setAlgorithm(parseTAAlgorithm(signKey.getAlgorithm()));

                        PrivateKey key = signKey.getKeyPair().getPrivate();
                        cert.getSignKey().setKeySource(new PrivateKeySource(key));
                    } else {
                        logger.warn("Private key not found in key list!");
                        return null;
                    }
                }
                break;
                case "outputFile": {
                    outputFile = new File(baseDir, certChilds.item(i).getTextContent());

                    NamedNodeMap attributes = certChilds.item(i).getAttributes();
                    for (int j = 0; j < attributes.getLength(); j++) {
                        if (attributes.item(j).getNodeName().equals("createAdditionalHexFile")) {
                            createAdditionalHexFile = parseXSBoolean(attributes.item(j).getNodeValue());
                        }
                    }
                }
                break;
            }
        }

        // create extensions
        CVExtensionDataList extDataList = new CVExtensionDataList();

        if (certDescNode != null) {
            CertificateDescription desc = generateDescFromNode(certDescNode, parseTAAlgorithm(signKey.getAlgorithm()), save); // use signkey information for hash algorithm
            if (desc != null) {
                CVExtensionData descExt = new CVExtensionData();
                descExt.setType(CVExtensionType.extDescription);

                DataBuffer hash = DataBuffer.generateHash(desc.generate(), parseTAAlgorithm(signKey.getAlgorithm())); // use signkey information for hash algorithm
                descExt.setHash1(hash);
                extDataList.add(descExt);
            } else {
                logger.warn("unable to generated/get certificate description ...");
            }
        }

        for (int i = 0; i < sectorKeys.size(); i++) {
            if (sectorKeys.get(i) != null) {
                CVExtensionData descExt = new CVExtensionData();
                descExt.setType(CVExtensionType.extSector);

                DataBuffer hash = DataBuffer.generateHash(sectorKeys.get(i), parseTAAlgorithm(signKey.getAlgorithm())); // use signkey information for hash algorithm
                descExt.setHash1(hash);
                extDataList.add(descExt);
            }
        }

        if (extDataList.size() > 0) {
            CVExtension ext = new CVExtension();
            ext.setExtensions(extDataList);
            cert.setExtension(ext);
        }

        // save certificate
        DataBuffer rawCert = cert.generateCert();

        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
        }

        rawCert.writeToFile(outputFile.getAbsolutePath());

        if (createAdditionalHexFile) {
            PrintWriter hexFileWriter = new PrintWriter(outputFile.getAbsolutePath() + ".hex");
            hexFileWriter.write(rawCert.asHexBinary());
            hexFileWriter.close();
        }

        return cert;
    }

    /**
     * Generates certificate description from XML node.
     *
     * @param node XML node
     * @param algorithm Algorithm information for hashing commCertificates.
     * @param save Save description in file?
     * @return Certificate description.
     * @throws Exception
     */
    protected CertificateDescription generateDescFromNode(Node node, TAAlgorithm algorithm, boolean save) throws Exception {
        CertificateDescription desc = new CertificateDescription();
        File descFile = null;
        boolean importCert = false;

        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            if (attributes.item(i).getNodeName().equals("import")) {
                importCert = parseXSBoolean(attributes.item(i).getNodeValue());
            }
        }

        NodeList descChilds = node.getChildNodes();
        for (int i = 0; i < descChilds.getLength(); i++) {
            switch (descChilds.item(i).getNodeName()) {
                case "issuerName":
                    desc.setIssuerName(descChilds.item(i).getTextContent());
                    break;
                case "issuerURL":
                    desc.setIssuerURL(descChilds.item(i).getTextContent());
                    break;
                case "subjectName":
                    desc.setSubjectName(descChilds.item(i).getTextContent());
                    break;
                case "subjectURL":
                    desc.setSubjectURL(descChilds.item(i).getTextContent());
                    break;
                case "fileTermsOfUsage": {
                    File termsFile = new File(baseDir, descChilds.item(i).getTextContent());

                    if (termsFile.exists()) {
                        if (termsFile.getName().endsWith(".txt")) { // PLAINTEXT
                            try {
                                String terms = getTextFromFile(termsFile);
                                desc.setPlainText(terms);
                            } catch (IOException e) {
                                logger.warn("Unable to read terms of Usage from file " + termsFile.getName());
                            }
                        } else if (termsFile.getName().endsWith(".html")) { // HTML
                            try {
                                String terms = getTextFromFile(termsFile);
                                desc.setHTML(terms);
                            } catch (IOException e) {
                                logger.warn("Unable to read terms of Usage from file " + termsFile.getName());
                            }
                        } else if (termsFile.getName().endsWith(".pdf")) { // PDF
                            try {
                                DataBuffer terms = DataBuffer.readFromFile(termsFile.getAbsolutePath());
                                desc.setPDF(terms);
                            } catch (IOException e) {
                                logger.warn("Unable to read terms of Usage from file " + termsFile.getName());
                            }
                        } else {
                            logger.warn("Unknown Terms of Usage file format: " + termsFile.getName());
                        }
                    } else {
                        logger.warn("Terms of Usage file " + termsFile.getName() + " does not exist!");
                    }
                }
                break;
                case "termsOfUsage": {
                    String terms = descChilds.item(i).getTextContent();
                    desc.setPlainText(terms);
                }
                break;
                case "redirectURL":
                    desc.setRedirectURL(descChilds.item(i).getTextContent());
                    break;
                case "commCerts": {
                    NodeList commCertsChilds = descChilds.item(i).getChildNodes();
                    for (int j = 0; j < commCertsChilds.getLength(); j++) {
                        switch (commCertsChilds.item(j).getNodeName()) {
                            case "fileCommCert": {
                                File commCertFile = new File(baseDir, commCertsChilds.item(j).getTextContent());
                                if (commCertFile.exists()) {
                                    try {
                                        DataBuffer commCert = DataBuffer.readFromFile(commCertFile.getAbsolutePath());
                                        DataBuffer commHash = DataBuffer.generateHash(commCert, algorithm);
                                        desc.addCommCertificates(commHash);
                                    } catch (IOException e) {
                                        logger.warn("Unable to read certificate from file " + commCertFile.getName());
                                    }
                                } else {
                                    logger.warn("Certificate file " + commCertFile.getName() + " does not exist!");
                                }
                            }
                            break;
                        }
                    }
                }
                break;
                case "fileDescription":
                    descFile = new File(baseDir, descChilds.item(i).getTextContent());
                    break;
            }
        }

        // save description
        if (!importCert) {
            if (descFile != null && save) {
                DataBuffer rawDesc = desc.generate();

                if (!descFile.getParentFile().exists()) {
                    descFile.getParentFile().mkdirs();
                }

                rawDesc.writeToFile(descFile.getAbsolutePath());

                logger.debug("description " + descFile.getName() + " saved.");
            }
        } else {
            if (descFile != null) {
                try {
                    DataBuffer rawDesc = DataBuffer.readFromFile(descFile.getAbsolutePath());
                    desc = new CertificateDescription(rawDesc);

                    logger.debug("description " + descFile.getName() + " loaded.");
                } catch (IOException e) {
                    logger.warn("Unable to read certificate description from file " + descFile.getName());
                }
            }
        }

        return desc;
    }

    /**
     * Gets sector keys from XML node.
     *
     * @param node XML node.
     * @return Sector key.
     * @throws Exception
     */
    protected DataBuffer getSectorKeyFromNode(Node node) throws Exception {
        DataBuffer out = null;

        NodeList terminalSectorChilds = node.getChildNodes();
        for (int i = 0; i < terminalSectorChilds.getLength(); i++) {
            switch (terminalSectorChilds.item(i).getNodeName()) {
                case "fileSectorPublicKey": {
                    File terminalSectorFile = new File(baseDir, terminalSectorChilds.item(i).getTextContent());
                    if (terminalSectorFile.exists()) {
                        try {
                            out = DataBuffer.readFromFile(terminalSectorFile.getAbsolutePath());
                        } catch (IOException e) {
                            logger.warn("Unable to read terminal sector key from file " + terminalSectorFile.getName());
                        }
                    } else {
                        logger.warn("Terminal sector key file " + terminalSectorFile.getName() + " does not exist!");
                    }
                }
                break;
            }
        }

        return out;
    }

    /**
     * Reads text from file to a string object.
     *
     * @param file The file.
     * @return File content.
     * @throws FileNotFoundException
     * @throws IOException
     */
    private String getTextFromFile(File file) throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(file.getAbsolutePath());
        int character;
        StringBuffer buff = new StringBuffer();
        while ((character = fr.read()) != -1) {
            buff.append((char) character);
        }
        fr.close();

        return buff.toString();
    }

    /**
     * Parses a xs:boolean value.
     *
     * @param str String to parse.
     * @return True if string is "true" or "1"
     */
    private boolean parseXSBoolean(String str) {
        return (str.equals("true") || str.equals("1"));
    }

    /**
     * Parses EC algorithm parameter from string.
     *
     * @param algorithm Algorithm name.
     * @return EC algorithm parameter.
     */
    protected AlgorithmParameterSpec parseECAlgorithm(String algorithm) {
        switch (algorithm) {
            case "ASN1::secp112r1":
                return ECCCurves.ASN1SECP112R1.getECParameter();
            case "ASN1::secp128r1":
                return ECCCurves.ASN1SECP128R1.getECParameter();
            case "ASN1::secp160r1":
                return ECCCurves.ASN1SECP160R1.getECParameter();
            case "ASN1::secp160k1":
                return ECCCurves.ASN1SECP160K1.getECParameter();
            case "ASN1::secp160r2":
                return ECCCurves.ASN1SECP160R2.getECParameter();
            case "ASN1::secp192k1":
                return ECCCurves.ASN1SECP192K1.getECParameter();
            case "ASN1::secp192r1":
                return ECCCurves.ASN1SECP192R1.getECParameter();
            case "ASN1::secp224k1":
                return ECCCurves.ASN1SECP224K1.getECParameter();
            case "ASN1::secp224r1":
                return ECCCurves.ASN1SECP224R1.getECParameter();
            case "ASN1::secp256k1":
                return ECCCurves.ASN1SECP256K1.getECParameter();
            case "ASN1::secp256r1":
                return ECCCurves.ASN1SECP256R1.getECParameter();
            case "ASN1::secp384r1":
                return ECCCurves.ASN1SECP384R1.getECParameter();
            case "ASN1::secp521r1":
                return ECCCurves.ASN1SECP521R1.getECParameter();
            case "BRAINPOOL::p160r1":
                return ECCCurves.BRAINPOOLP160R1.getECParameter();
            case "BRAINPOOL::p160t1":
                return ECCCurves.BRAINPOOLP160T1.getECParameter();
            case "BRAINPOOL::p192r1":
                return ECCCurves.BRAINPOOLP192R1.getECParameter();
            case "BRAINPOOL::p192t1":
                return ECCCurves.BRAINPOOLP192T1.getECParameter();
            case "BRAINPOOL::p224r1":
                return ECCCurves.BRAINPOOLP224R1.getECParameter();
            case "BRAINPOOL::p224t1":
                return ECCCurves.BRAINPOOLP224T1.getECParameter();
            case "BRAINPOOL::p256r1":
                return ECCCurves.BRAINPOOLP256R1.getECParameter();
            case "BRAINPOOL::p256t1":
                return ECCCurves.BRAINPOOLP256T1.getECParameter();
            case "BRAINPOOL::p320r1":
                return ECCCurves.BRAINPOOLP320R1.getECParameter();
            case "BRAINPOOL::p320t1":
                return ECCCurves.BRAINPOOLP320T1.getECParameter();
            case "BRAINPOOL::p384r1":
                return ECCCurves.BRAINPOOLP384R1.getECParameter();
            case "BRAINPOOL::p384t1":
                return ECCCurves.BRAINPOOLP384T1.getECParameter();
            case "BRAINPOOL::p512r1":
                return ECCCurves.BRAINPOOLP512R1.getECParameter();
            case "BRAINPOOL::p512t1":
                return ECCCurves.BRAINPOOLP512T1.getECParameter();
        }

        return null;
    }

    /**
     * Parses TA algorithm from string.
     *
     * @param algorithm Algorithm name.
     * @return TA algorithm.
     */
    protected TAAlgorithm parseTAAlgorithm(String algorithm) {
        switch (algorithm) {
            case "TA_RSA_v1_5_SHA_1":
                return TAAlgorithm.RSA_v1_5_SHA_1;
            case "TA_RSA_v1_5_SHA_256":
                return TAAlgorithm.RSA_v1_5_SHA_256;
            case "TA_RSA_PSS_SHA_1":
                return TAAlgorithm.RSA_PSS_SHA_1;
            case "TA_RSA_PSS_SHA_256":
                return TAAlgorithm.RSA_PSS_SHA_256;
            case "TA_ECDSA_SHA_1":
                return TAAlgorithm.ECDSA_SHA_1;
            case "TA_ECDSA_SHA_224":
                return TAAlgorithm.ECDSA_SHA_224;
            case "TA_ECDSA_SHA_256":
                return TAAlgorithm.ECDSA_SHA_256;
        }

        return null;
    }

    /**
     * Sets relative date.
     *
     * @param date The date to set.
     */
    public void setDate(Date date) {
        if (date != null) {
            this.m_relDate = date;
        }
    }
}
