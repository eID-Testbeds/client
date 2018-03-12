package com.secunet.ipsmall.cardsimulation;

import com.secunet.testbedutils.cvc.cvcertificate.CVCertificate;
import com.secunet.testbedutils.cvc.cvcertificate.DataBuffer;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CardPersonalizationCLI {
	
	private enum Simulation {
		PersoSim
	}
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
        
        Simulation sim = null;
        String[] simParams = null;
        CVCertificate cvca = null;
        Date cardDate = null;
        String pin = null;
        
        // parse parameters
        if (args.length < 1) {
            System.out.println("No arguments ...");
            showHelp();
        } else {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                case "-?":
                    showHelp();
                    return;
                case "-sim":
                    if (i + 1 >= args.length || args[i + 1].startsWith("-")) {
                        System.out.println("No parameter for argument: " + args[i]);
                        showHelp();
                        return;
                    }
                    String simParam = args[++i];
                    if (simParam.startsWith("PersoSim")) {
                    	sim = Simulation.PersoSim;
                    	simParams = simParam.split(";");
                    } else {
            			System.out.println("Invalid parameter for argument: " + simParam);
            			showHelp();
                        return;
                    }
                    break;
                case "-cvca":
                    if (i + 1 >= args.length || args[i + 1].startsWith("-")) {
                        System.out.println("No parameter for argument: " + args[i]);
                        showHelp();
                        return;
                    }
                    File certFile = new File(args[++i]);
                    try {
                    	DataBuffer rawCert = DataBuffer.readFromFile(certFile.getAbsolutePath());
						cvca = new CVCertificate(rawCert);
					} catch (Exception e) {
						System.out.println("Unable to parse certificate file: " + certFile.getAbsolutePath());
						showHelp();
                        return;
					}
                    break;
                case "-date":
                    if (i + 1 >= args.length || args[i + 1].startsWith("-")) {
                        System.out.println("No parameter for argument: " + args[i]);
                        showHelp();
                        return;
                    }
                    String dateParam = args[++i];
                    DateFormat dateFor = new SimpleDateFormat("yyyy-MM-dd");
                    try {
						cardDate = dateFor.parse(dateParam);
					} catch (ParseException e) {
						System.out.println("Unable to parse date: " + dateParam);
						showHelp();
                        return;
					}
                    break;
                case "-pin":
                    if (i + 1 >= args.length || args[i + 1].startsWith("-")) {
                        System.out.println("No parameter for argument: " + args[i]);
                        showHelp();
                        return;
                    }
                    pin = args[++i];
                    break;
                default:
                    System.out.println("Unknown argument: " + args[i]);
                    showHelp();
                    return;
                }
            }
        }
        
        try {
	        // personalize
	        if (sim != null) {
	        	ICardPersonalization cardPerso = null;
		        switch (sim) {
		        	case PersoSim: {
		        		if (simParams.length >= 3)
		        			cardPerso = new PersoSimPersonalization(new File(simParams[1]), new File(simParams[2]));
		        		else
		        			throw new Exception("Invalid number of parameters!");
	
		        	} break;
		        }
		        
		        if (cardPerso != null) {
		        	cardPerso.addTrustpoint(cvca);
		        	cardPerso.addCardDate(cardDate);
		        	cardPerso.addCardPIN(pin);
		        	
		        	cardPerso.personalizeCard();
		        	System.out.println("PersoSim profile created: " + simParams[2]);
		        } else
		        	throw new Exception("Card personalization failed!");
	        } else
	        	throw new Exception("Invalid simulation type!");
        } catch (Exception e) {
        	System.out.println("Error occured: " + e.getMessage());
        }
        	
    }
    
    /**
     * Shows help text.
     */
    private static void showHelp()
    {
        String runCmd = "java -jar " + CardPersonalizationCLI.class.getSimpleName();
        
        System.out.println();
        System.out.println("CommandLine Interface for personalizition of a card simulation");
        
        // Usage
        System.out.println();
        System.out.println("Usage:");
        System.out.println(" " + runCmd + " -?");
        
        // Parameter
        System.out.println();
        System.out.println("-?\tShows this help.");
        System.out.println("-sim\tSimulation type (parameters splitet by ;):");
        System.out.println("\t\t\tPersoSim;<path to template file>;<path to new configuration profile file>");
        System.out.println("-cvca\tPersonalizes card with cvcertificate file <path to certificate>.");
        System.out.println("-date\tPersonalizes card with date <date>. Format: yyyy-MM-dd");
        System.out.println("-pin\tPersonalizes card with pin.<pin>");
        
        System.out.println();
    }
}
