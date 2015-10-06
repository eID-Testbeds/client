package com.secunet.testbedutils.cvc.tools;

import com.secunet.testbedutils.cvc.cvcertificate.CVCertificate;
import com.secunet.testbedutils.cvc.cvcertificate.DataBuffer;
import java.io.File;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Exports public key of a CV certificate.
 *
 * @author neunkirchen.bernd
 */
public class ExportPublicKey {

    private static final String PUBLICKEY_SUFFIX = "_PUBKEY.bin";
    
    /**
     * Exports public key of given certtificate.
     * @param certFile Certificate file.
     * @return Public key file.
     */
    public static File export(File certFile) {
        File publicKeyDestination = new File(certFile.getAbsolutePath().substring(0, certFile.getAbsolutePath().lastIndexOf(".")) + PUBLICKEY_SUFFIX);
        return export(certFile, publicKeyDestination);
    }
    
    /**
     * Exports public key of given certtificate.
     * @param certFile Certificate file.
     * @param publicKeyDestination Destinaltion of public key.
     * @return Public key file.
     */
    public static File export(File certFile, File publicKeyDestination) {
        File result = publicKeyDestination;
        
        if (certFile.exists()) {
            // load certificate
            CVCertificate cert = Utils.loadCVCertificate(certFile);

            // generate public key and save
            if (cert != null) {
                try {
                    DataBuffer pubKey = new DataBuffer(cert.getPublicKey().getPublicKey().getEncoded());
                    System.out.println("Encoded public key:");
                    System.out.println(pubKey.asHex());
                    System.out.println();
                    pubKey.writeToFile(result.getAbsolutePath());
                    System.out.println("Saved as: " + result.getAbsolutePath());
                } catch (Exception e) {
                    System.out.println("Unable to save certificates public key:" + e.getMessage());
                    result = null;
                }
            }
        } else {
            System.out.println("Input file does not exist: " + certFile.getAbsolutePath());
        }

        return result;
    }

    /**
     * Main function.
     *
     * @param args
     */
    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());

        if (args.length < 1) {
            System.out.println("No arguments ...");
            showHelp();
        } else {
            if (args[0].equals("-?")) {
                showHelp();
            } else {
                export(new File(args[0]));
            }
        }

    }

    /**
     * Shows help text.
     */
    private static void showHelp() {
        String runCmd = "java -jar " + ExportPublicKey.class.getSimpleName();

        System.out.println();
        System.out.println("CommandLine Interface for exporting public key of a CV certificate.");

        // Usage
        System.out.println();
        System.out.println("Usage:");
        System.out.println(" " + runCmd + " <certificate file>");

        // Parameter
        System.out.println();
        System.out.println("-?\tShows this help.");
        System.out.println("<file>\tCV-Certificate containing the public key to export.");

        System.out.println();
    }
}
