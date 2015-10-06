package com.secunet.ipsmall.tobuilder;

import com.secunet.ipsmall.GlobalSettings;
import com.secunet.ipsmall.log.IModuleLogger;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.util.FileUtils;
import com.secunet.testbedutils.bouncycertgen.GeneratedCertificate;
import com.secunet.testbedutils.bouncycertgen.cv.CVCertGen;
import com.secunet.testbedutils.bouncycertgen.x509.CertificateDefinition;
import com.secunet.testbedutils.bouncycertgen.x509.CertificateDefinitions;
import com.secunet.testbedutils.bouncycertgen.x509.X509CertificateFactory;
import com.secunet.testbedutils.cvc.cvcertificate.CVCertificate;
import com.secunet.testbedutils.cvc.cvcertificate.DataBuffer;
import com.secunet.testbedutils.utilities.JaxBUtil;

import java.io.*;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.MiscPEMGenerator;
import org.bouncycastle.openssl.PKCS8Generator;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator;
import org.bouncycastle.operator.OutputEncryptor;

/**
 * Setup to generate certificates.
 */
public class CertificateSetup implements ITestObjectSetup {

    private static final String SCRIPT_PATH = "TestObjectBuilder" + File.separator + "certgen.bat";

    private static final String SUFFIX_X509_CERTIFICATE_FILE = ".der";
    private static final String SUFFIX_X509_KEY_FILE = "_KEY.der";

    private final TestObjectSettings settings;
    private boolean isSetUp = false;

    private final File certGenDir;
    private final File certsDestDir;

    /**
     * Creates setup to generate certificates.
     *
     * @param settings TestObject settings.
     */
    public CertificateSetup(TestObjectSettings settings) {
        this.settings = settings;
        certGenDir = new File(settings.getTestObjectDir(), GlobalSettings.getTOCertificateGenerationDir());
        certsDestDir = new File(settings.getTestObjectDir(), GlobalSettings.getTOCertificatesDir());
    }

    @Override
    public void runSetup() throws Exception {
        Logger.TestObjectBuilder.logState("Generating certificates ...");

        // prepare certificate generation
        if (settings != null) {
            try {
                FileUtils.copyDir(new File(settings.getTestbedDir(), certGenDir.getName()), certGenDir, true);
                Logger.TestObjectBuilder.logState("Copy of certificate generation directory placed in TestObject.");

                File cvcaDir = settings.getCVCADir();
                if (cvcaDir != null && cvcaDir.exists()) {
                    FileUtils.copyDir(cvcaDir, new File(certGenDir, "CVCA"), true);
                    Logger.TestObjectBuilder.logState("Copy of CVCA directory placed in TestObject.");
                }

                File sslcaDir = settings.getSSLCADir();
                if (sslcaDir != null && sslcaDir.exists()) {
                    FileUtils.copyDir(sslcaDir, new File(certGenDir, "SSLCA"), true);
                    Logger.TestObjectBuilder.logState("Copy of SSL CA directory placed in TestObject.");
                }

                // check if destination directory exists
                if (!certsDestDir.exists()) {
                    certsDestDir.mkdirs();
                }
            } catch (IOException e) {
                Logger.TestObjectBuilder.logState("Error while coping certificate generation configuration: " + e.getMessage(), IModuleLogger.LogLevel.Error);
            }

            // clear destination
            // generate X509 certificates
            generateX509Certs();

            // generate CV certificates
            generateCVCerts();

            // copy certificate
            try {
                FileUtils.copyDir(new File(certGenDir, "certs"), new File(settings.getTestObjectDir(), GlobalSettings.getTOTestsCommonDir()), true);
                Logger.TestObjectBuilder.logState("Certificates copied to Common directory.");
            } catch (IOException e) {
                Logger.TestObjectBuilder.logState("Error while coping certificates: " + e.getMessage(), IModuleLogger.LogLevel.Error);
            }
        }

        Logger.TestObjectBuilder.logState("Certificates generated.");

        isSetUp = true;
    }

    @Override
    public boolean IsSetUp() {
        return isSetUp;
    }

    /**
     * Runs shell script for certificate generation.
     */
    private void runCertificateGenerationScript() {
        if (certGenDir != null && certGenDir.exists()) {
            Process p;
            try {
                File script = new File(settings.getTestbedDir(), SCRIPT_PATH);
                Logger.TestObjectBuilder.logState("Running certificate generation script: " + script.getAbsolutePath() + " ...", IModuleLogger.LogLevel.Debug);
                p = Runtime.getRuntime().exec("cmd /c " + script.getAbsolutePath() + " \"" + settings.getTestbedDir().getAbsolutePath() + "\" \"" + certGenDir.getAbsolutePath() + "\" " + settings.getTestDate());

                String line;
                try (BufferedReader inStd = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                    while ((line = inStd.readLine()) != null) {
                        Logger.CertificateGeneration.logState("TLS (script): " + line);
                    }
                }

                p.waitFor();
            } catch (IOException | InterruptedException e) {
                Logger.TestObjectBuilder.logState("Error while running process: " + e.getMessage(), IModuleLogger.LogLevel.Error);
            }
        }
    }

    /**
     * Generates X509 certificates.
     */
    private void generateX509Certs() {
        Security.addProvider(new BouncyCastleProvider());

        X509CertificateFactory.setLogger(Logger.CertificateGeneration.getJavaLogger("X.509"));

        // get date
        Date relativeDate;
        DateFormat dateFor = new SimpleDateFormat("yyyy-MM-dd");
        try {
            relativeDate = dateFor.parse(settings.getTestDate());
        } catch (ParseException e) {
            relativeDate = null;
            Logger.TestObjectBuilder.logState("Invalid date format, using current date for CV certificate generation ...", IModuleLogger.LogLevel.Warn);
        }

        GeneratedCertificate[] rootCerts;
        // generate root SSLCA certificate
        File sslcaDir = new File(certGenDir, "SSLCA");
        if (!(new File(sslcaDir, "TR03124-2-TLS-CA-RSA_KEY.pem")).exists()
                && !(new File(sslcaDir, "TR03124-2-TLS-CA-ECDSA_KEY.pem")).exists()
                && !(new File(sslcaDir, "TR03124-2-TLS-CA-DSA_KEY.pem")).exists()) { // create only if SSLCA does not exists
            rootCerts = generateX509CertsFromConfig(new File(certGenDir, "TLS_rootSSLCA.xml"), null, sslcaDir, relativeDate, ".crt", "_KEY.pem", true);
        } else {
            rootCerts = new GeneratedCertificate[]{
                X509CertificateFactory.loadX509(null, new File(sslcaDir, "TR03124-2-TLS-CA-RSA.crt"), new File(sslcaDir, "TR03124-2-TLS-CA-RSA_KEY.pem"), "1234"),
                X509CertificateFactory.loadX509(null, new File(sslcaDir, "TR03124-2-TLS-CA-ECDSA.crt"), new File(sslcaDir, "TR03124-2-TLS-CA-ECDSA_KEY.pem"), "1234"),
                X509CertificateFactory.loadX509(null, new File(sslcaDir, "TR03124-2-TLS-CA-DSA.crt"), new File(sslcaDir, "TR03124-2-TLS-CA-DSA_KEY.pem"), "1234"),};
        }

        // generate X509 sets
        File[] certGenDirFiles = certGenDir.listFiles();
        for (File certGenDirFile : certGenDirFiles) {
            if (certGenDirFile.getName().matches("TLS_SET_.+\\.xml")) {
                generateX509CertsFromConfig(certGenDirFile, rootCerts, certsDestDir, relativeDate, false);
            }
        }
    }

    /**
     * Generates CV certificates.
     */
    private void generateCVCerts() {
        CVCertGen.setLogger(Logger.CertificateGeneration.getLog4JLogger("CV"));

        // initialize CV certificate generator
        CVCertGen cvCertGen = new CVCertGen(certGenDir);

        // set date
        DateFormat dateFor = new SimpleDateFormat("yyyy-MM-dd");
        try {
            cvCertGen.setDate(dateFor.parse(settings.getTestDate()));
        } catch (ParseException e) {
            Logger.TestObjectBuilder.logState("Invalid date format, using current date for CV certificate generation ...", IModuleLogger.LogLevel.Warn);
        }

        // generate root CVCA certificate
        File cvcaDir = new File(certGenDir, "CVCA");
        if (!(new File(cvcaDir, "DECVCAeIDCT00001.pkcs8")).exists()) { // create only if CVCA does not exists
            generateCVCertsFromConfig(cvCertGen, new File(certGenDir, "CV_rootCVCA.xml"));
        }

        // copy root CVCA certificate
        try {
            FileUtils.copyFile(new File(cvcaDir, "DECVCAeIDCT00001.pkcs8"), new File(certsDestDir, "CERT_CV_CVCA_1_KEY.pkcs8"), true);
            FileUtils.copyFile(new File(cvcaDir, "DECVCAeIDCT00001.cvcert"), new File(certsDestDir, "CERT_CV_CVCA_1.cvcert"), true);
            File cvcaHexSource = new File(cvcaDir, "DECVCAeIDCT00001.cvcert.hex");
            if (cvcaHexSource.exists()) {
                FileUtils.copyFile(cvcaHexSource, new File(certsDestDir, "CERT_CV_CVCA_1.cvcert.hex"), true);
            }
        } catch (IOException e) {
            Logger.TestObjectBuilder.logState("Error while copying cvca certificate: " + e.getMessage(), IModuleLogger.LogLevel.Error);
        }

        // generate root and link certificates for SET 4
        // get number of existing link certificates
        File[] cvcaFiles = cvcaDir.listFiles();
        int numberOfLinkKeys = 0;
        for (File cvcaFile : cvcaFiles) {
            if (cvcaFile.getName().matches("CERT_CV_LINK_4_\\d+_KEY\\.pkcs8")) {
                numberOfLinkKeys++;
            }
        }
        if (numberOfLinkKeys == 0) { // create only if no link certificate exists
            generateCVCertsFromConfig(cvCertGen, new File(certGenDir, "CV_rootCVCA_SET_4.xml"));
            // update certificate list
            cvcaFiles = cvcaDir.listFiles();
        }

        // copy link CVCA certificates
        try {
            FileUtils.copyFile(new File(cvcaDir, "DECVCAeIDCT00001.pkcs8"), new File(certsDestDir, "CERT_CV_CVCA_4_1_KEY.pkcs8"), true);
            FileUtils.copyFile(new File(cvcaDir, "DECVCAeIDCT00001.cvcert"), new File(certsDestDir, "CERT_CV_CVCA_4_1.cvcert"), true);
            File cvcaHexSource = new File(cvcaDir, "DECVCAeIDCT00001.cvcert.hex");
            if (cvcaHexSource.exists()) {
                FileUtils.copyFile(cvcaHexSource, new File(certsDestDir, "CERT_CV_CVCA_4_1.cvcert.hex"), true);
            }

            for (File cvcaFile : cvcaFiles) {
                if (cvcaFile.getName().matches("CERT_CV_CVCA_4_\\d+.*")
                        || cvcaFile.getName().matches("CERT_CV_LINK_4_\\d+.*")) {
                    FileUtils.copyFile(cvcaFile, new File(certsDestDir, cvcaFile.getName()), true);
                }
            }
        } catch (IOException e) {
            Logger.TestObjectBuilder.logState("Error while copying link certificates: " + e.getMessage(), IModuleLogger.LogLevel.Error);
        }

        // copy sector key
        try {
            FileUtils.copyFile(new File(certGenDir, "TerminalCertificateSectorKey.bin"), new File(certsDestDir, "SectorKey.bin"), true);
        } catch (IOException e) {
            Logger.TestObjectBuilder.logState("Error while copying public sector key: " + e.getMessage(), IModuleLogger.LogLevel.Error);
        }

        // generate CV sets
        File[] certGenDirFiles = certGenDir.listFiles();
        for (File certGenDirFile : certGenDirFiles) {
            if (certGenDirFile.getName().matches("CV_SET_.+\\.xml")) {
                generateCVCertsFromConfig(cvCertGen, certGenDirFile);
            }
        }

        // perform special settings
        // copy CERT_CV_DV_1_A to CERT_CV_DV_1_B and modify signature
        try {
            DataBuffer certDV1B = DataBuffer.readFromFile((new File(certsDestDir, "CERT_CV_DV_1_A.cvcert").getAbsolutePath()));
            byte lastByte = certDV1B.get(certDV1B.size() - 1);
            certDV1B.erase(certDV1B.size() - 1, 1);
            certDV1B.append((byte) (~lastByte & 0xff)); // invert
            certDV1B.writeToFile((new File(certsDestDir, "CERT_CV_DV_1_B.cvcert").getAbsolutePath()));

            FileUtils.copyFile(new File(certsDestDir, "CERT_CV_DV_1_A_KEY.pkcs8"), new File(certsDestDir, "CERT_CV_DV_1_B_KEY.pkcs8"), true);
        } catch (IOException e) {
            Logger.TestObjectBuilder.logState("Error while copying and manipulating certificate: " + e.getMessage(), IModuleLogger.LogLevel.Error);
        }

        // copy key of CERT_CV_LINK_2_A to CERT_CV_CVCA_2_B
        try {
            FileUtils.copyFile(new File(certsDestDir, "CERT_CV_LINK_2_A_KEY.pkcs8"), new File(certsDestDir, "CERT_CV_CVCA_2_B_KEY.pkcs8"), true);
        } catch (IOException e) {
            Logger.TestObjectBuilder.logState("Error while copying file: " + e.getMessage(), IModuleLogger.LogLevel.Error);
        }
    }

    /**
     * Generates CV certificates from configuration file.
     *
     * @param cvCertGen Certificate generator.
     * @param certConfig Configuration file.
     */
    private void generateCVCertsFromConfig(CVCertGen cvCertGen, File certConfig) {
        if (cvCertGen != null && certConfig != null && certConfig.exists()) {
            if (cvCertGen.checkXML(certConfig)) {
                ArrayList<CVCertificate> certs = null;
                try {
                    certs = cvCertGen.generateFromXML(certConfig, true);
                } catch (Exception e) {
                    Logger.TestObjectBuilder.logState("Error while generating certificates for " + certConfig.getName() + ": " + e.getMessage(), IModuleLogger.LogLevel.Error);
                }
                if (certs != null) {
                    Logger.TestObjectBuilder.logState(certs.size() + " Certificate(s) generated for " + certConfig.getName() + ".");
                }
            } else {
                Logger.TestObjectBuilder.logState("Invalid CV certificate configuration file: " + certConfig.getAbsolutePath(), IModuleLogger.LogLevel.Error);
            }
        }
    }

    /**
     * Generates X.509 certificates from configuration file.
     *
     * @param certConfig Configuration file.
     * @param signers List of possible signer.
     * @param destDir Destination directory.
     * @param relativeDate Relative date.
     * @return List of crtificates and key pairs.
     */
    private GeneratedCertificate[] generateX509CertsFromConfig(File certConfig, GeneratedCertificate[] signers, File destDir, Date relativeDate, boolean encodePEM) {
        return generateX509CertsFromConfig(certConfig, signers, destDir, relativeDate, SUFFIX_X509_CERTIFICATE_FILE, SUFFIX_X509_KEY_FILE, encodePEM);
    }

    /**
     * Generates X.509 certificates from configuration file.
     *
     * @param certConfig Configuration file.
     * @param signers List of possible signer.
     * @param destDir Destination directory.
     * @param relativeDate Relative date.
     * @param suffixCert Suffix/file ending of certificate file.
     * @param suffixKey Suffix/file ending of key file.
     * @return List of crtificates and key pairs.
     */
    private GeneratedCertificate[] generateX509CertsFromConfig(File certConfig, GeneratedCertificate[] signers, File destDir, Date relativeDate, String suffixCert, String suffixKey, boolean encodePEM) {
        List<GeneratedCertificate> certs = new ArrayList<>();
        if (certConfig != null && certConfig.exists()) {
            try {
                CertificateDefinitions certificateDefinitions = JaxBUtil.unmarshal(certConfig, CertificateDefinitions.class);
                for (CertificateDefinition certificateDefinition : certificateDefinitions.getCertificateDefinition()) {
                    // get signer certificate and key pair from list
                    GeneratedCertificate certSigner = null;
                    X500Name issuerName = new X500Name(X509CertificateFactory.buildIssuerOrSubjectString(certificateDefinition, true));
                    if (signers != null) {
                        for (GeneratedCertificate signer : signers) {
                            X500Name issuerSubjectName = new X500Name(signer.getCertificate().getSubjectX500Principal().getName());
                            if (issuerSubjectName.equals(issuerName)) {
                                certSigner = signer;
                                break;
                            }
                        }
                    }

                    // generate certificate
                    GeneratedCertificate generatedCertificate = X509CertificateFactory.createX509(certificateDefinition, certSigner, relativeDate);
                    certs.add(generatedCertificate);
                }
            } catch (Exception e) {
                Logger.TestObjectBuilder.logState("Error while generating certificates for " + certConfig.getName() + ": " + e.getMessage(), IModuleLogger.LogLevel.Error);
            }

            Logger.TestObjectBuilder.logState(certs.size() + " Certificate(s) generated for " + certConfig.getName() + ".");

            // cteate destination dir if not exists
            if (destDir != null) {
                if (!destDir.isDirectory()) {
                    destDir.mkdirs();
                }
            } else {
                Logger.TestObjectBuilder.logState("Invalid value for destination directory: " + destDir, IModuleLogger.LogLevel.Error);
            }

            for (GeneratedCertificate cert : certs) {
                // save key
                File privKeyFile = new File(destDir, cert.getDefinition().getName() + suffixKey);
                try {
                    if (encodePEM) {
                        try (JcaPEMWriter writer = new JcaPEMWriter(new FileWriter(privKeyFile))) {
                            writer.writeObject(cert.getKeyPair().getPrivate());
                        }
                    } else {
                        DataBuffer privKeyRaw = new DataBuffer(cert.getKeyPair().getPrivate().getEncoded());
                        privKeyRaw.writeToFile(privKeyFile.getAbsolutePath());
                    }
                } catch (IOException e) {
                    Logger.TestObjectBuilder.logState("Unable to save certificates key: " + e.getMessage(), IModuleLogger.LogLevel.Error);
                }

                // save certificate
                File certFile = new File(destDir, cert.getDefinition().getName() + suffixCert);
                try {
                    if (encodePEM) {
                        try (JcaPEMWriter writer = new JcaPEMWriter(new FileWriter(certFile))) {
                            writer.writeObject(cert.getCertificate());
                        }
                    } else {
                        DataBuffer certRaw = new DataBuffer(cert.getCertificate().getEncoded());
                        certRaw.writeToFile(certFile.getAbsolutePath());
                    }
                } catch (CertificateEncodingException | IOException e) {
                    Logger.TestObjectBuilder.logState("Unable to save certificates key: " + e.getMessage(), IModuleLogger.LogLevel.Error);
                }
            }
        }

        return certs.toArray(new GeneratedCertificate[certs.size()]);
    }
}
