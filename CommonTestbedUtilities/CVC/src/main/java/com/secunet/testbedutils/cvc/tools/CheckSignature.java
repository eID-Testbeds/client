package com.secunet.testbedutils.cvc.tools;

import com.secunet.testbedutils.cvc.cvcertificate.CVCertificate;
import com.secunet.testbedutils.cvc.cvcertificate.CVPubKeyHolder;
import com.secunet.testbedutils.cvc.cvcertificate.DataBuffer;
import java.io.File;
import java.security.KeyFactory;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Checks Signature of certificate chain.
 *
 * @author neunkirchen.bernd
 */
public class CheckSignature {

    /**
     * Checks signature of certificate.
     *
     * @param holderCertFile Certificate file containing the signature to check.
     * @param authCertFile Certificate file of signing authority.
     * @return True if signature is valid, else false.
     */
    public static boolean check(File holderCertFile, File authCertFile) {
        return check(holderCertFile, authCertFile, null);
    }

    /**
     * Checks signature of certificate.
     *
     * @param holderCertFile Certificate file containing the signature to check.
     * @param authCertFile Certificate file of signing authority.
     * @param rootCertFile Certificate file (root) containing domain parameters.
     * @return True if signature is valid, else false.
     */
    public static boolean check(File holderCertFile, File authCertFile, File rootCertFile) {
        CVCertificate holderCert = Utils.loadCVCertificate(holderCertFile);
        CVCertificate authCert = Utils.loadCVCertificate(authCertFile);
        CVCertificate rootCert = Utils.loadCVCertificate(rootCertFile); // if file null, cert also null

        return check(holderCert, authCert, rootCert);
    }

    /**
     * Checks signature of certificate.
     *
     * @param holderCert Certificate containing the signature to check.
     * @param authCert Certificate of signing authority.
     * @return True if signature is valid, else false.
     */
    public static boolean check(CVCertificate holderCert, CVCertificate authCert) {
        return check(holderCert, authCert, null);
    }

    /**
     * Checks signature of certificate.
     *
     * @param holderCert Certificate containing the signature to check.
     * @param authCert Certificate of signing authority.
     * @param rootCert Certificate (root) containing domain parameters.
     * @return True if signature is valid, else false.
     */
    public static boolean check(CVCertificate holderCert, CVCertificate authCert, CVCertificate rootCert) {
        boolean result = false;

        if (holderCert.getCertAuthRef().equals(authCert.getCertHolderRef())) {
            System.out.println("Checking signature of " + holderCert.getCertHolderRef() + " against public key of " + authCert.getCertHolderRef() + " ...");
            if (rootCert != null) {
                try {
                    System.out.println("Using domain parameters of " + rootCert.getCertHolderRef() + " ...");
                    result = holderCert.checkSign(authCert, rootCert);
                } catch (Exception e) {
                    System.out.println("Error while checking signature of " + holderCert.getCertHolderRef() + ": " + e.getMessage());
                }
            } else {
                try {
                    result = holderCert.checkSign(authCert);
                    checkEncoding(holderCert.getSignature(), authCert.getPublicKey());
                } catch (Exception e) {
                    System.out.println("Error while checking signature of " + holderCert.getCertHolderRef() + ": " + e.getMessage());
                }
            }
        } else {
            System.out.println(holderCert.getCertHolderRef() + " was not signed by " + authCert.getCertHolderRef() + ".");
        }

        holderCert.getSignature().toByteArray();
        authCert.getPublicKey();

        return result;
    }

    /**
     * Checks encoding of signature in relation to signing key.
     * @param signature The signature.
     * @param pubKey The public key.
     */
    private static void checkEncoding(DataBuffer signature, CVPubKeyHolder pubKey) {

        AsymmetricBlockCipher cipher = null;

        switch (pubKey.getAlgorithm()) {
            case RSA_v1_5_SHA_1:
                try {
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
                    RSAPublicKey rsaPubKey = (RSAPublicKey) keyFactory.generatePublic(pubKey.getRSAKey());

                    cipher = new RSAEngine();
                    cipher = new org.bouncycastle.crypto.encodings.PKCS1Encoding(cipher);

                    cipher.init(false, (AsymmetricKeyParameter) PublicKeyFactory.createKey(rsaPubKey.getEncoded()));
                } catch (Exception e) {
                    System.out.println("Unable to initialize RSA for given public key: " + e.toString());
                    cipher = null;
                }
                break;
            default:
                break;
        }

        if (cipher != null) {
            try {
                byte[] rawSig = signature.toByteArray();
                // just perfrom process block to see if any exceptions are thown.
                cipher.processBlock(rawSig, 0, rawSig.length);
            } catch (Exception e) {
                System.out.println("Invalid signature format :" + e.toString());
            }
        }
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
            String holderCertFilename = null;
            String authCertFilename = null;
            String rootCertFilename = null;

            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-?":
                        showHelp();
                        return;
                    case "-H":
                        if (i + 1 >= args.length || args[i + 1].startsWith("-")) {
                            System.out.println("No parameter for argument: " + args[i]);
                            showHelp();
                            return;
                        }
                        holderCertFilename = args[++i];
                        break;
                    case "-A":
                        if (i + 1 >= args.length || args[i + 1].startsWith("-")) {
                            System.out.println("No parameter for argument: " + args[i]);
                            showHelp();
                            return;
                        }
                        authCertFilename = args[++i];
                        break;
                    case "-R":
                        if (i + 1 >= args.length || args[i + 1].startsWith("-")) {
                            System.out.println("No parameter for argument: " + args[i]);
                            showHelp();
                            return;
                        }
                        rootCertFilename = args[++i];
                        break;
                }
            }

            if (holderCertFilename == null || holderCertFilename.isEmpty()) {
                System.out.println("No holder holder certificate file given.");
                return;
            }

            if (authCertFilename == null || authCertFilename.isEmpty()) {
                System.out.println("No authority holder certificate file given.");
                return;
            }

            File rootCert = null;
            if (rootCertFilename != null && !rootCertFilename.isEmpty()) {
                rootCert = new File(rootCertFilename);
            }

            boolean isSigned = check(new File(holderCertFilename), new File(authCertFilename), rootCert);
            if (isSigned) {
                System.out.println("Signature is VALID.");
            } else {
                System.out.println("Signature is NOT VALID.");
            }
        }

    }

    /**
     * Shows help text.
     */
    private static void showHelp() {
        String runCmd = "java -jar " + CheckSignature.class.getSimpleName();

        System.out.println();
        System.out.println("CommandLine Interface to check signature of a CV certificate.");

        // Usage
        System.out.println();
        System.out.println("Usage:");
        System.out.println(" " + runCmd + " -H <holder certificate file> -A <authority certificate file> (-R <root certificate file>)");

        // Parameter
        System.out.println();
        System.out.println("-?\tShows this help.");
        System.out.println("-H\tCV certificate file to check signature.");
        System.out.println("-A\tCV certificate file of signing authority.");
        System.out.println("-R\tCV certificate file containing domain parameters.");

        System.out.println();
    }
}
