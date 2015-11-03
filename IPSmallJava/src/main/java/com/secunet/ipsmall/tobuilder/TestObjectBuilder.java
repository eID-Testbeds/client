package com.secunet.ipsmall.tobuilder;

import com.secunet.ipsmall.log.IModuleLogger;
import com.secunet.ipsmall.log.Logger;

public class TestObjectBuilder {
    
    /**
     * Main method
     * 
     * @param args CommandLine arguments
     */
    public static void main(String[] args) {       
        String xmlFile = "";
        String template = "";
        String cvca = "";
        String sslca = "";
        String testbed = "";
        String trXML = "";
        boolean setDefault = false;

        if (args.length < 1) {
            Logger.Global.logState("No arguments ...", IModuleLogger.LogLevel.Fatal);
            showHelp();
            return;
        } else {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                case "-?":
                    showHelp();
                    return;
                case "-xin":
                    if (i + 1 >= args.length || args[i + 1].startsWith("-")) {
                        System.err.println("No parameter for argument: " + args[i]);
                        showHelp();
                        return;
                    }
                    xmlFile = args[++i];
                    break;
                case "-template":
                    if (i + 1 >= args.length || args[i + 1].startsWith("-")) {
                        System.err.println("No parameter for argument: " + args[i]);
                        showHelp();
                        return;
                    }
                    template = args[++i];
                    break;
                case "-cvca":
                    if (i + 1 >= args.length || args[i + 1].startsWith("-")) {
                        System.err.println("No parameter for argument: " + args[i]);
                        showHelp();
                        return;
                    }
                    cvca = args[++i];
                    break;
                case "-sslca":
                    if (i + 1 >= args.length || args[i + 1].startsWith("-")) {
                        System.err.println("No parameter for argument: " + args[i]);
                        showHelp();
                        return;
                    }
                    sslca = args[++i];
                    break;
                case "-testbed":
                    if (i + 1 >= args.length || args[i + 1].startsWith("-")) {
                        System.err.println("No parameter for argument: " + args[i]);
                        showHelp();
                        return;
                    }
                    testbed = args[++i];
                    break;
                case "-trXML":
                    if (i + 1 >= args.length || args[i + 1].startsWith("-")) {
                        System.err.println("No parameter for argument: " + args[i]);
                        showHelp();
                        return;
                    }
                    trXML = args[++i];
                    break;
                case "-default":
                    setDefault = true;
                    break;
                default:
                    Logger.Global.logState("Unknown argument: " + args[i], IModuleLogger.LogLevel.Fatal);
                    showHelp();
                    return;
                }
            }
        }
        
        if (!xmlFile.isEmpty()) {
        	try {
	            BuildTestObject builder = new BuildTestObject(xmlFile, template, testbed, trXML, setDefault);
                if (!cvca.isEmpty()) {
                    builder.useGivenCVCA(cvca);
                }
                if (!sslca.isEmpty()) {
                    builder.useGivenSSLCA(sslca);
                }
                builder.create();
        	} catch (Exception e) {
                Logger.Global.logException(e);
        	}
        } else {
            Logger.Global.logState("No XML file given.", IModuleLogger.LogLevel.Fatal);
        }
    }
    
    /**
     * Shows help text.
     */
    private static void showHelp()
    {
        String runCmd = "java -jar " + TestObjectBuilder.class.getSimpleName();
        
        System.out.println();
        System.out.println("CommandLine Interface for building TestObject folder from ICS.");
        
        // Usage
        System.out.println();
        System.out.println("Usage:");
        System.out.println(" " + runCmd + " -?");
        System.out.println(" " + runCmd + " -xin <file> [-template <path> -cvca <path> -sslca <path> -testbed <path> -trXML <path> -default]");
        
        // Parameter
        System.out.println();
        System.out.println("-?\t\tShows this help.");
        System.out.println("-xin\t\tLoads ICS XML configuration file.");
        System.out.println("-template\tLoads template for spezific configuration of TestObject.");
        System.out.println("-cvca\tLoads given CVCA for TestObject.");
        System.out.println("-sslca\tLoads given SSLCA for TestObject.");
        System.out.println("-testbed\tSet path to testbed.");
        System.out.println("-trXML\t\tSet path to TR-03124 XML Files.");
        System.out.println("-default\t\tSet generated TestObject as default.");
        
        System.out.println();
    }
}
