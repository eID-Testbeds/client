package com.secunet.testbedutils.bouncycertgen;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

import com.secunet.testbedutils.bouncycertgen.cv.CVCertGen;
import com.secunet.testbedutils.cvc.cvcertificate.CVCertificate;

/**
 * @author Lukasz Kubik, secunet
 *
 */
public class StandaloneBouncyCertificateGenerator {
	private static final Logger logger = Logger
			.getLogger(StandaloneBouncyCertificateGenerator.class.getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int type = 0;
		ArrayList<String> remainingArguments = new ArrayList<String>();
		for(String s: args) {
			if("-cv".equalsIgnoreCase(s)) {
				if(type == 0) {
					type = 1;
				} else {
					type = 4;
					break;
				}					
			} else if("-x509".equalsIgnoreCase(s)) {
				if(type == 0) {
					type = 2;
				} else {
					type = 4;
					break;
				}
			} else {
				remainingArguments.add(s);
			}
		}
		if(type == 0) {
			System.out.println("No certificate type provided.");
			System.out.println();
			showHelp();
			logger.log(Level.WARNING,
					"No certificate type provided.");
		} else if(type == 4) {
			System.out.println("Only one type of certificates can be created at a time.");
			System.out.println();
			showHelp();
			logger.log(Level.WARNING,
					"Only one type of certificates can be created at a time.");
		} else if(type == 1) {
			generateCV(remainingArguments);
		} else if(type == 2) {
			generateX509(remainingArguments);
		}
	}
	
	// TODO implement switch for this in main()
	private static void dumpCertificate(String name, X509Certificate cert) {
		FileWriter certWriter;
		JcaPEMWriter certPEMwriter = null;
		try {
			// write certificate
			String certName = name + ".PEM";
			certWriter = new FileWriter(certName);
			certPEMwriter = new JcaPEMWriter(certWriter);
			certPEMwriter.writeObject(cert);
			certPEMwriter.flush();
			certPEMwriter.close();
			System.out.println("Successfully created certificate " + certName + ".");
		} catch (IOException e) {
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.log(Level.WARNING,
					"Writing one of the certificates or keys has failed:"
							+ System.getProperty("line.separator")
							+ trace.toString());
		} finally {
			try {
				if (certPEMwriter != null) {
					certPEMwriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void generateX509(List<String> args) {
		if (args.size() < 1) {
			logger.log(Level.SEVERE, "No input XML was specified.");
		} else {
			System.out.println("Beginning x509 certificate generation.");
			BouncyCertificateGenerator generator = BouncyCertificateGenerator
					.getGenerator();
			List<GeneratedCertificate> certificates = generator
					.makex509Certificates(args);
			System.out.println("Certificate generation complete, writing results...");
			for (GeneratedCertificate genCert : certificates) {
				FileWriter certWriter;
				JcaPEMWriter certPEMwriter = null;
				FileWriter keyWriter;
				JcaPEMWriter keyPEMwriter = null;
				try {
					// write certificate
					String certName = genCert.getDefinition().getName()
							+ ".PEM";
					certWriter = new FileWriter(certName);
					certPEMwriter = new JcaPEMWriter(certWriter);
					certPEMwriter.writeObject(genCert.getCertificate());
					certPEMwriter.flush();
					certPEMwriter.close();
					System.out.println("Successfully created certificate " + certName + ".");
					// write key if necessary
					if(genCert.getDefinition().getKeyFile() == null) {
						String keyName = genCert.getDefinition().getName()
								+ "_privKey.PEM";
						keyWriter = new FileWriter(keyName);
				        keyPEMwriter = new JcaPEMWriter(keyWriter);
				        keyPEMwriter.writeObject(genCert.getKeyPair().getPrivate());
				        keyPEMwriter.flush();
				        keyPEMwriter.close();
				        System.out.println("Successfully created private key file " + keyName + ".");
					}
				} catch (IOException e) {
					StringWriter trace = new StringWriter();
					e.printStackTrace(new PrintWriter(trace));
					logger.log(Level.WARNING,
							"Writing one of the certificates or keys has failed:"
									+ System.getProperty("line.separator")
									+ trace.toString());
				} finally {
					try {
						if (certPEMwriter != null) {
							certPEMwriter.close();
						}
						if (keyPEMwriter != null) {
							keyPEMwriter.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			System.out.println("Completed x509 certificate generation.");
		}
	}

	private static void generateCV(List<String> args) {
		String xmlFile = "";
        boolean checkOnly = false;
        Date refDate = null;
        
        DateFormat dateFor = new SimpleDateFormat("yyyy-MM-dd");
        
        CVCertGen gen = new CVCertGen();
        
        if (args.size() < 1) {
            System.out.println("No arguments ...");
            showHelp();
        } else {
            for (int i = 0; i < args.size(); i++) {
                switch (args.get(i)) {
                    case "-?":
                        showHelp();
                        return;
                    case "-xin":
                        if (i + 1 >= args.size() || args.get(i+1).startsWith("-")) {
                            System.out.println("No parameter for argument: " + args.get(i));
                            showHelp();
                            return;
                        }
                        xmlFile = args.get(++i);
                        break;
                    case "-date":
                        if (i + 1 >= args.size() || args.get(i+1).startsWith("-")) {
                            System.out.println("No parameter for argument: " + args.get(i));
                            showHelp();
                            return;
                        }
                        try {
                            refDate = dateFor.parse(args.get(++i));
                            gen.setDate(refDate);
                        } catch (ParseException e) {
                            System.out.println("Invalid date format: " + args.get(i));
                            showHelp();
                            return;
                        }
                        break;
                    case "-check":
                        checkOnly = true;
                        break;
                    default:
                        System.out.println("Unknown argument: " + args.get(i));
                        showHelp();
                        return;
                }
            }
        }
        
        if (!xmlFile.isEmpty()) {
            // Check XML file
            try {
                if (gen.checkXML(xmlFile)) {
                    if (!checkOnly) {
                        // Generating CV certificates
                        ArrayList<CVCertificate> certs = null;
                        try {
                            certs = gen.generateFromXML(xmlFile, true);
                        } catch (Exception e) {
                            System.out.println("Unable to generate certificates from " + xmlFile + ":");
                            System.out.println("\t" + e.getMessage());
                        }
                        
                        if (certs != null) {
                            System.out.println(certs.size() + " certificate(s) generated!");
                        } else
                            System.out.println("No certificates are generated!");
                    } else
                        System.out.println(xmlFile + " is valid!");
                } else
                    System.out.println(xmlFile + " is NOT valid!");
                
            } catch (Exception e) {
                System.out.println("Unable to check " + xmlFile + ":");
                System.out.println("\t" + e.getMessage());
            }
        }
	}

	/**
	 * Shows help text.
	 */
	private static void showHelp() {
		String runCmd = "java -jar "
				+ StandaloneBouncyCertificateGenerator.class.getSimpleName();

		System.out.println();
		System.out
				.println("CommandLine Interface for generating x509 and CV certificates using the Bouncy Certificate Generator.");

		// Usage
		System.out.println();
		System.out.println("Usage:");
		System.out.println(" " + runCmd + " -?");
		System.out
				.println(" " + runCmd + " -cv -xin \"<file>\" [-date yyyy-MM-dd]");
		System.out.println(" " + runCmd + " -cv -xin \"<file>\" [-check]");
		System.out.println(" " + runCmd + " -x509 \"<file1>\" \"<file2>\" ...");

		// Parameter
		System.out.println();
		System.out.println("-?\tShows this help.");
		System.out
		.println("-cv\t Create a CV certificate using the remaining arguments.");
		System.out
		.println("-x509\t Create a x509 certificate using the provided XML paths.");
		System.out
				.println("-xin\t Only CV certificates: Loads certificates XML configuration file. For designing configuration files refer to the schema defintion file for the certificate type.");
		System.out
				.println("-d\t Only CV certificates: Reference date for certificate generation. Format: yyyy-MM-dd");
		System.out
				.println("-check\t  Only CV certificates: Checks only certificates XML configuration file.");

		System.out.println();
	}

}
