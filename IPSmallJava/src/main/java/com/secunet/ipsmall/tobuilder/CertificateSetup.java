package com.secunet.ipsmall.tobuilder;

import com.secunet.ipsmall.GlobalSettings;
import com.secunet.ipsmall.log.IModuleLogger;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.util.FileUtils;
import com.secunet.testbedutils.bouncycertgen.cv.CVCertGen;
import com.secunet.testbedutils.cvc.cvcertificate.CVCertificate;
import com.secunet.testbedutils.cvc.cvcertificate.DataBuffer;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Setup to generate certificates.
 */
public class CertificateSetup implements ITestObjectSetup {

    private static final String SCRIPT_PATH = "TestObjectBuilder" + File.separator + "certgen.bat";

    private TestObjectSettings settings;
    private boolean isSetUp = false;

    private File certGenDir;
    private File certsDestDir;
	
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

            // run scripts to generate SSL certificates
            runCertificateGenerationScript();

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
                BufferedReader inStd = new BufferedReader(new InputStreamReader(p.getInputStream()));
                while ((line = inStd.readLine()) != null) {
                    Logger.CertificateGeneration.logState("TLS (script): " + line);
                }
                inStd.close();

                p.waitFor();
            } catch (IOException | InterruptedException e) {
                Logger.TestObjectBuilder.logState("Error while running process: " + e.getMessage(), IModuleLogger.LogLevel.Error);
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
                if (cvcaFile.getName().matches("CERT_CV_CVCA_4_\\d+.*") ||
                        cvcaFile.getName().matches("CERT_CV_LINK_4_\\d+.*")) {
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
        DataBuffer certDV1B = null;
        try {
            certDV1B = DataBuffer.readFromFile((new File(certsDestDir, "CERT_CV_DV_1_A.cvcert").getAbsolutePath()));
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
     * Generates CV crtificates from configuration file.
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
                    Logger.TestObjectBuilder.logState("Error while generating certificates for " +  certConfig.getName() + ": " + e.getMessage(), IModuleLogger.LogLevel.Error);
                }
                if (certs != null) {
                    Logger.TestObjectBuilder.logState(certs.size() + " Certificate(s) generated for " + certConfig.getName() + ".");
                }
            } else {
                Logger.TestObjectBuilder.logState("Invalid CV certificate configuration file: " + certConfig.getAbsolutePath(), IModuleLogger.LogLevel.Error);
            }
        }
    }
}
