package com.secunet.ipsmall.test;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.bouncycastle.asn1.x509.Certificate;
import org.junit.Before;
import org.junit.Test;

public class FileBasedTestDataTest {
    
    private FileBasedTestData fbtd = null;
    
    @Before
    public void setUp() throws Exception {
        URL toUrl = getClass().getResource("/TestObjectExample");
        Path toPath = Paths.get(toUrl.toURI());
        File toFile = toPath.toFile();
        URL certUrl = getClass().getResource("/TestObjectExample/Tests/Common/ServerCertificate.der");
        Path certPath = Paths.get(certUrl.toURI());
        File certFile = certPath.toFile();
        URL keyUrl = getClass().getResource("/TestObjectExample/Tests/Common/PrivateKey.der");
        Path keyPath = Paths.get(keyUrl.toURI());
        File keyFile = keyPath.toFile();
        assertNotNull("Certificate could not be found.", certFile);
        assertNotNull("Key could not be found.", keyFile);
        
        String testName = "Module_0/Default";
        File testObjFolder = toFile;
        fbtd = new FileBasedTestData(testName, testObjFolder);
        assertNotNull("FileBasedTestData could not be loaded.", fbtd);
    }
    
    @Test
    public void testReadCertificateBC() {
        try {
            Certificate cert = fbtd.readCertificateBC("ServerCertificate.der");
            System.out.println(cert.getIssuer().toString());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
}
