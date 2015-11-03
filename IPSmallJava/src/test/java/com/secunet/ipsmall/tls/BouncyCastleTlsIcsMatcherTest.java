package com.secunet.ipsmall.tls;

import static org.junit.Assert.*;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import com.secunet.bouncycastle.crypto.tls.SignatureAndHashAlgorithm;
import com.secunet.ipsmall.GlobalSettings;
import com.secunet.ipsmall.tobuilder.ics.TLSVersionType;
import com.secunet.ipsmall.tobuilder.ics.TR031242ICS;
import com.secunet.testbedutils.utilities.JaxBUtil;

public class BouncyCastleTlsIcsMatcherTest {

    private BouncyCastleTlsIcsMatcher matcher = null;
    
    
    @Before
    public void setUp() throws Exception {
        // get ICS file
        URL icsUrl = getClass().getResource("/TestObjectExample/" + GlobalSettings.getICSFileName());
        Path icsPath = Paths.get(icsUrl.toURI());
        File icsFile = icsPath.toFile();

        // get XSD as stream
        InputStream xsd = getClass().getResourceAsStream(GlobalSettings.getICSSchemaFileName());
        
        // parse ICS xml
        TR031242ICS ics = JaxBUtil.unmarshal(icsFile, TR031242ICS.class, xsd);

        matcher = new BouncyCastleTlsIcsMatcher(ics);
        assertNotNull("matcher could not be constructed.", matcher);
    }
    
    @Test
    public void testMatchCipherSuitesPositive() {
        String[] clientCipherSuiteStrings = {
                "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
                "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
                "TLS_DHE_RSA_WITH_AES_256_GCM_SHA384",
                "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384",
                "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA",
                "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384",
                "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA",
                "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256",
                "TLS_DHE_RSA_WITH_AES_256_CBC_SHA",
                "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
                "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
                "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256",
                "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256",
                "TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
                "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",
                "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
                "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256",
                "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA",
                "TLS_DHE_DSS_WITH_AES_128_CBC_SHA256",
                "TLS_DHE_DSS_WITH_AES_128_CBC_SHA",
                "TLS_DHE_DSS_WITH_AES_128_GCM_SHA256",
                "TLS_DHE_DSS_WITH_AES_256_GCM_SHA384",
                "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256",
                "TLS_DHE_DSS_WITH_AES_256_CBC_SHA",
                "TLS_EMPTY_RENEGOTIATION_INFO_SCSV"
        };
       
        assertTrue(testMatchCipherSuites(clientCipherSuiteStrings));
        
    }
    
    @Test
    public void testMatchCipherSuitesNegativeTooFew() {
        String[] clientCipherSuiteStrings = {
                "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
                "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
                "TLS_DHE_RSA_WITH_AES_256_GCM_SHA384",
                "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384",
                "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA",
                "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384",
                "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA",
                "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256",
                "TLS_DHE_RSA_WITH_AES_256_CBC_SHA",
                "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
                "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
                "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256",
                "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256",
                "TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
                "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",
                "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
                "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256",
                "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA",
                "TLS_DHE_DSS_WITH_AES_128_CBC_SHA256",
                "TLS_DHE_DSS_WITH_AES_128_CBC_SHA",
                "TLS_DHE_DSS_WITH_AES_128_GCM_SHA256",
                "TLS_DHE_DSS_WITH_AES_256_GCM_SHA384",
                "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256"
        };
        
        assertFalse(testMatchCipherSuites(clientCipherSuiteStrings));
    }

    @Test
    public void testMatchCipherSuitesNegativeTooMany() {
        String[] clientCipherSuiteStrings = {
                "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
                "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
                "TLS_DHE_RSA_WITH_AES_256_GCM_SHA384",
                "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384",
                "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA",
                "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384",
                "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA",
                "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256",
                "TLS_DHE_RSA_WITH_AES_256_CBC_SHA",
                "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
                "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
                "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256",
                "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256",
                "TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
                "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",
                "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
                "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256",
                "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA",
                "TLS_DHE_DSS_WITH_AES_128_CBC_SHA256",
                "TLS_DHE_DSS_WITH_AES_128_CBC_SHA",
                "TLS_DHE_DSS_WITH_AES_128_GCM_SHA256",
                "TLS_DHE_DSS_WITH_AES_256_GCM_SHA384",
                "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256",
                "TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256", 
                "TLS_EMPTY_RENEGOTIATION_INFO_SCSV"
        };
        
        assertFalse(testMatchCipherSuites(clientCipherSuiteStrings));
    }

    @Test
    public void testMatchCipherSuitesPositiveDifferentOrder() {
        String[] clientCipherSuiteStrings = {
                "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
                "TLS_DHE_RSA_WITH_AES_256_GCM_SHA384",
                "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
                "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384",
                "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA",
                "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384",
                "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA",
                "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256",
                "TLS_DHE_RSA_WITH_AES_256_CBC_SHA",
                "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
                "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
                "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256",
                "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256",
                "TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
                "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",
                "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
                "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256",
                "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA",
                "TLS_DHE_DSS_WITH_AES_128_CBC_SHA256",
                "TLS_DHE_DSS_WITH_AES_128_CBC_SHA",
                "TLS_DHE_DSS_WITH_AES_128_GCM_SHA256",
                "TLS_DHE_DSS_WITH_AES_256_GCM_SHA384",
                "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256",
                "TLS_DHE_DSS_WITH_AES_256_CBC_SHA",
                "TLS_EMPTY_RENEGOTIATION_INFO_SCSV"
        };
       
        assertTrue(testMatchCipherSuites(clientCipherSuiteStrings));
        
    }
    
    @Test
    public void testMatchCipherSuitesPositiveNoRenegotiation() {
        String[] clientCipherSuiteStrings = {
                "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
                "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
                "TLS_DHE_RSA_WITH_AES_256_GCM_SHA384",
                "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384",
                "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA",
                "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384",
                "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA",
                "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256",
                "TLS_DHE_RSA_WITH_AES_256_CBC_SHA",
                "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
                "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
                "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256",
                "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256",
                "TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
                "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",
                "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
                "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256",
                "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA",
                "TLS_DHE_DSS_WITH_AES_128_CBC_SHA256",
                "TLS_DHE_DSS_WITH_AES_128_CBC_SHA",
                "TLS_DHE_DSS_WITH_AES_128_GCM_SHA256",
                "TLS_DHE_DSS_WITH_AES_256_GCM_SHA384",
                "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256",
                "TLS_DHE_DSS_WITH_AES_256_CBC_SHA"
        };
       
        assertTrue(testMatchCipherSuites(clientCipherSuiteStrings));
        
    }
    
    private boolean testMatchCipherSuites(String[] clientCipherSuiteStrings) {
        int[] clientCipherSuites = new int[clientCipherSuiteStrings.length];
        for(int i=0 ; i < clientCipherSuiteStrings.length ; i++) {
            clientCipherSuites[i] = BouncyCastleTlsHelper.convertCipherSuiteStringToInt(clientCipherSuiteStrings[i]);
        }

        return matcher.matchCipherSuites(true, TLSVersionType.TLS_12, clientCipherSuites);
    }

    
    @Test
    public void testMatchSignatureAndHashAlgorithmsPositive() {
        String[] clientSignatureAndHashAlgorithmStrings = {
                "SHA512withRSA",
                "SHA512withDSA",
                "SHA512withECDSA",
                "SHA384withRSA",
                "SHA384withDSA",
                "SHA384withECDSA",
                "SHA256withRSA",
                "SHA256withDSA",
                "SHA256withECDSA",
                "SHA224withRSA",
                "SHA224withDSA",
                "SHA224withECDSA",
                "SHA1withRSA",
                "SHA1withDSA",
                "SHA1withECDSA"
        };
        
        assertTrue(testMatchSignatureAndHashAlgorithms(clientSignatureAndHashAlgorithmStrings));
    }
    
    @Test
    public void testMatchSignatureAndHashAlgorithmsNegativeWrongOrder() {
        String[] clientSignatureAndHashAlgorithmStrings = {
                "SHA512withRSA",
                "SHA512withECDSA",
                "SHA512withDSA",
                "SHA384withRSA",
                "SHA384withDSA",
                "SHA384withECDSA",
                "SHA256withRSA",
                "SHA256withDSA",
                "SHA256withECDSA",
                "SHA224withRSA",
                "SHA224withDSA",
                "SHA224withECDSA",
                "SHA1withRSA",
                "SHA1withDSA",
                "SHA1withECDSA"
        };
        
        assertFalse(testMatchSignatureAndHashAlgorithms(clientSignatureAndHashAlgorithmStrings));
    }
    
    @Test
    public void testMatchSignatureAndHashAlgorithmsNegativeTooFew() {
        String[] clientSignatureAndHashAlgorithmStrings = {
                "SHA512withRSA",
                "SHA512withDSA",
                "SHA512withECDSA",
                "SHA384withRSA",
                "SHA384withDSA",
                "SHA384withECDSA",
                "SHA256withRSA",
                "SHA256withDSA",
                "SHA256withECDSA",
                "SHA224withRSA",
                "SHA224withDSA",
                "SHA224withECDSA",
                "SHA1withRSA",
                "SHA1withDSA"
        };
        
        assertFalse(testMatchSignatureAndHashAlgorithms(clientSignatureAndHashAlgorithmStrings));
    }
    
    @Test
    public void testMatchSignatureAndHashAlgorithmsNegativeTooMany() {
        String[] clientSignatureAndHashAlgorithmStrings = {
                "SHA512withRSA",
                "SHA512withDSA",
                "SHA512withECDSA",
                "SHA384withRSA",
                "SHA384withDSA",
                "SHA384withECDSA",
                "SHA256withRSA",
                "SHA256withDSA",
                "SHA256withECDSA",
                "SHA224withRSA",
                "SHA224withDSA",
                "SHA224withECDSA",
                "SHA1withRSA",
                "SHA1withDSA",
                "SHA1withECDSA",
                "MD5withRSA"
        };
        
        assertFalse(testMatchSignatureAndHashAlgorithms(clientSignatureAndHashAlgorithmStrings));
    }
    
    private boolean testMatchSignatureAndHashAlgorithms(String[] clientSignatureAndHashAlgorithmStrings) {
        SignatureAndHashAlgorithm[] clientSignatureAndHashAlgorithms = new SignatureAndHashAlgorithm[clientSignatureAndHashAlgorithmStrings.length];
        for(int i=0 ; i < clientSignatureAndHashAlgorithmStrings.length ; i++) {
            clientSignatureAndHashAlgorithms[i] = BouncyCastleTlsHelper.convertSignatureAndHashAlgorithmStringToClass(clientSignatureAndHashAlgorithmStrings[i]);
        }

        return matcher.matchSignatureAndHashAlgorithms(true, TLSVersionType.TLS_12, clientSignatureAndHashAlgorithms);
    }

    
    @Test
    public void testMatchEllipticCurvesPositive() {
        String[] clientSupportedEllipticCurveStrings = {
                "brainpoolP512r1",
                "brainpoolP384r1",
                "brainpoolP256r1",
                "secp384r1",
                "secp256r1",
                "secp224r1"
        };
        
        assertTrue(testMatchEllipticCurves(clientSupportedEllipticCurveStrings));
    }
    
    @Test
    public void testMatchEllipticCurvesNegativeWrongOrder() {
        String[] clientSupportedEllipticCurveStrings = {
                "brainpoolP512r1",
                "brainpoolP384r1",
                "secp384r1",
                "brainpoolP256r1",
                "secp256r1",
                "secp224r1"
        };
        
        assertFalse(testMatchEllipticCurves(clientSupportedEllipticCurveStrings));
    }
    
    @Test
    public void testMatchEllipticCurvesNegativeTooFew() {
        String[] clientSupportedEllipticCurveStrings = {
                "brainpoolP512r1",
                "brainpoolP384r1",
                "brainpoolP256r1",
                "secp384r1",
                "secp256r1"
        };
        
        assertFalse(testMatchEllipticCurves(clientSupportedEllipticCurveStrings));
    }
    
    @Test
    public void testMatchEllipticCurvesNegativeTooMany() {
        String[] clientSupportedEllipticCurveStrings = {
                "brainpoolP512r1",
                "brainpoolP384r1",
                "brainpoolP256r1",
                "secp384r1",
                "secp256r1",
                "secp224r1",
                "secp521r1"
        };
        
        assertFalse(testMatchEllipticCurves(clientSupportedEllipticCurveStrings));
    }

    private boolean testMatchEllipticCurves(String[] clientSupportedEllipticCurveStrings) {
        int[] clientSupportedEllipticCurves = new int[clientSupportedEllipticCurveStrings.length];
        for(int i=0 ; i < clientSupportedEllipticCurveStrings.length ; i++) {
            clientSupportedEllipticCurves[i] = BouncyCastleTlsHelper.convertNamedCurveStringToInt(clientSupportedEllipticCurveStrings[i]);
        }

        return matcher.matchEllipticCurves(true, TLSVersionType.TLS_12, clientSupportedEllipticCurves);
    }
}
