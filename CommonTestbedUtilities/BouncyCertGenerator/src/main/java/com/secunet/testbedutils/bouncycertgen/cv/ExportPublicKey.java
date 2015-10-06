package com.secunet.testbedutils.bouncycertgen.cv;

import java.io.File;
import java.security.Security;
import java.util.StringTokenizer;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.secunet.testbedutils.cvc.cvcertificate.CVCertificate;
import com.secunet.testbedutils.cvc.cvcertificate.DataBuffer;

// TODO Where is this used?
public class ExportPublicKey {
	
	public static void main(String[] args) {
		
		Security.addProvider(new BouncyCastleProvider());
		
		if (args.length < 1) {
			System.out.println("No arguments ...");
			showHelp();
		} else {
			switch (args[0]) {
			case "-?":
				showHelp();
				return;
			default: {
				File certFile = new File(args[0]);
				File keyFile = new File(cutExtension(certFile.getAbsolutePath()) + ".bin");
				if (args.length > 1) {
					keyFile = new File(args[1]);
				}
				
				
				if (certFile.exists()) {
					try {
						DataBuffer certBuf = DataBuffer.readFromFile(certFile.getAbsolutePath());
						CVCertificate cert = new CVCertificate(certBuf);
						
						DataBuffer pubKey = new DataBuffer(cert.getPublicKey().getPublicKey().getEncoded());
						pubKey.writeToFile(keyFile.getAbsolutePath());
						
						System.out.print("Public key exported to " + keyFile.getAbsolutePath());
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					
				} else
					System.err.print(args[0] + " not found.");
			} break;
			}
		}

	}
	
	/**
	 * Shows help text.
	 */
	private static void showHelp()
	{
		String runCmd = "java -jar " + ExportPublicKey.class.getSimpleName();
		
		System.out.println();
		System.out.println("Exports public key from certificate.");
		
		// Usage
		System.out.println();
		System.out.println("Usage:");
		System.out.println(" " + runCmd + " -?");
		System.out.println(" " + runCmd + " <certificatefile> [<keyfile>]");
		
		// Parameter
		System.out.println();
		System.out.println("-?\tShows this help.");
		
		System.out.println();
	}
	
	/**
	 * Removes extension from file name.
	 * @param fileName File name.
	 * @return File name without extension.
	 */
    private static String cutExtension(String fileName) {
        if (fileName == null)
            return fileName;
        String point = ".";
        StringTokenizer st = new StringTokenizer(fileName, ".");
        if (st.countTokens() < 3)
            return st.nextToken();
        StringBuffer ret = new StringBuffer();
        
        
        while(st.countTokens() > 1){
            ret.append(st.nextToken());
            if(st.countTokens() > 1){
                ret.append(point);
            }
        }
        
        return ret.toString();
    }

}
