package com.secunet.testbedutils.bouncycertgen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.secunet.testbedutils.bouncycertgen.cv.CVCertGen;
import com.secunet.testbedutils.bouncycertgen.x509.CertificateDefinition;
import com.secunet.testbedutils.bouncycertgen.x509.CertificateDefinitions;
import com.secunet.testbedutils.bouncycertgen.x509.X509CertificateFactory;
import com.secunet.testbedutils.cvc.cvcertificate.CVCertificate;
import com.secunet.testbedutils.utilities.JaxBUtil;
import org.bouncycastle.asn1.x500.X500Name;

/**
 * @author Lukasz Kubik, secunet AG
 *
 */
public class BouncyCertificateGenerator implements CertificateGenerator {

    private static final Logger logger = LogManager.getRootLogger();
    private static BouncyCertificateGenerator generator = null;

    // hide the constructor
    private BouncyCertificateGenerator() {
    }

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
     * be used for generating the certificates
     * @return {@link List<X509Certificate>} A list of X509 certificates
     */
    @Override
    public List<GeneratedCertificate> makex509Certificates(List<String> xmlPaths) {
        List<GeneratedCertificate> certificates = new ArrayList<>();
        // note: each XML may contain an infinite number of certificate
        // definitions
        for (String path : xmlPaths) {
            certificates.addAll(makex509Certificate(path));
        }
        return certificates;
    }

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
    @Override
    public List<GeneratedCertificate> makex509Certificate(String definition) {
        List<GeneratedCertificate> certificates = new ArrayList<>();
        // note: each XML may contain an infinite number of certificate
        // definitions
        CertificateDefinitions certificateDefinitions = JaxBUtil.unmarshal(definition, CertificateDefinitions.class);
        for (CertificateDefinition certificateDefinition : certificateDefinitions.getCertificateDefinition()) {
            // get signer certificate and key pair from list
            GeneratedCertificate certSigner = null;
            X500Name issuerName = new X500Name(X509CertificateFactory.buildIssuerOrSubjectString(certificateDefinition, true));
            for (GeneratedCertificate signer : certificates) {
                X500Name issuerSubjectName = new X500Name(signer.getCertificate().getSubjectX500Principal().getName());
                if (issuerSubjectName.equals(issuerName)) {
                    certSigner = signer;
                    break;
                }
            }

            // create certificate holder
            GeneratedCertificate generatedCertificate = X509CertificateFactory.createX509(certificateDefinition, certSigner);
            certificates.add(generatedCertificate);
        }
        return certificates;
    }

    /**
     * Read the X509Certificate from the given path
     *
     * @param {@link String} path The path to the certificate
     * @return {@link X509Certificate} The X509 certificate read from the file
     */
    @Override
    public X509Certificate readFromFileSystem(String path) {
        CertificateFactory certFactory;
        try {
            certFactory = CertificateFactory.getInstance("X.509", "BC");
            InputStream stream = new FileInputStream(path);
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(stream);
            return cert;
        } catch (CertificateException | NoSuchProviderException e) {
            logger.error("Could not read x509 certificate: " + e.getMessage());
        } catch (FileNotFoundException e) {
            StringWriter trace = new StringWriter();
            e.printStackTrace(new PrintWriter(trace));
            logger.error("The certificate could not be loaded from file the file " + path + ":" + System.getProperty("line.separator") + trace.toString());
        }
        return null;
    }

    /**
     * Generate CV certificate(s) using the provided XML file
     * <p>
     * - The XML file may contain one or more certificates
     *
     * @param xmlPath
     * @return
     */
    @Override
    public List<CVCertificate> makeCVCertifcates(String xmlPath) {
        CVCertGen gen = new CVCertGen();
        List<CVCertificate> certList = null;
        // Check XML file
        if (gen.checkXML(xmlPath)) {
            try {
                certList = gen.generateFromXML(xmlPath, true);
            } catch (Exception e) {
                StringWriter trace = new StringWriter();
                e.printStackTrace(new PrintWriter(trace));
                logger.error("The CV certificate(s) could not be generated:" + System.getProperty("line.separator") + trace.toString());
            }
        }
        return certList;
    }

    /**
     * Return the generator instance
     *
     * @return
     */
    public static BouncyCertificateGenerator getGenerator() {
        if (generator == null) {
            generator = new BouncyCertificateGenerator();
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        }
        return generator;
    }

}
