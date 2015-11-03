package com.secunet.testbedutils.cvc.tools;

import com.secunet.testbedutils.cvc.cvcertificate.CVCertificate;
import com.secunet.testbedutils.cvc.cvcertificate.DataBuffer;
import java.io.File;

/**
 * Some CVCertificate utils.
 *
 * @author neunkirchen.bernd
 */
public class Utils {

    /**
     * Loads CV certificate from file.
     * @param certificateFile Certificate file.
     * @return CV certificate.
     */
    public static CVCertificate loadCVCertificate(File certificateFile) {
        CVCertificate result = null;

        if (certificateFile == null) {
            return null;
        }
        
        if (!certificateFile.exists()) {
            System.out.println("File does not exist: " + certificateFile.getAbsolutePath());
            return null;
        }

        try {
            DataBuffer rawCert = DataBuffer.readFromFile(certificateFile.getAbsolutePath());
            result = new CVCertificate(rawCert);
            System.out.println("Loaded " + result.getCertHolderRef() + " from " + certificateFile.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Unable to read CV certificate from file:" + e.getMessage());
            result = null;
        }

        return result;
    }
}
