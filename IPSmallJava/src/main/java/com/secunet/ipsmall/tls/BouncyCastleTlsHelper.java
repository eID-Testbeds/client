package com.secunet.ipsmall.tls;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.LinkedList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.crypto.agreement.DHStandardGroups;
import org.bouncycastle.crypto.params.DHParameters;

import com.secunet.bouncycastle.crypto.tls.Certificate;
import com.secunet.bouncycastle.crypto.tls.CipherSuite;
import com.secunet.bouncycastle.crypto.tls.ECPointFormat;
import com.secunet.bouncycastle.crypto.tls.HashAlgorithm;
import com.secunet.bouncycastle.crypto.tls.KeyExchangeAlgorithm;
import com.secunet.bouncycastle.crypto.tls.NamedCurve;
import com.secunet.bouncycastle.crypto.tls.ProtocolVersion;
import com.secunet.bouncycastle.crypto.tls.SignatureAlgorithm;
import com.secunet.bouncycastle.crypto.tls.SignatureAndHashAlgorithm;
import com.secunet.ipsmall.test.ITestData.PROTOCOLS;

public class BouncyCastleTlsHelper {
    
    /**
     * Converts a TLS cipher suite given as string to a corresponding integer value found in class CipherSuite.
     * There are no magic values to indicate a failed conversion, so an IllegalArgumentException is thrown instead.   
     * @param cipherSuite TLS cipher suite given as string
     * @return corresponding integer value found in class CipherSuite
     * @throws IllegalArgumentException if conversion fails
     */
    public static int convertCipherSuiteStringToInt(String cipherSuite) throws IllegalArgumentException {
        return convertBCEnumerationStringToInt(CipherSuite.class, cipherSuite);
    }
    
    /**
     * Converts a TLS cipher suite given as integer value found in class CipherSuite to a corresponding string.
     * @param cipherSuite TLS cipher suite given as integer value found in class CipherSuite
     * @return corresponding string or null, if conversion fails
     */
    public static String convertCipherSuiteIntToString(int cipherSuite) {
        return convertBCEnumerationIntToString(CipherSuite.class, cipherSuite);
    }
    
    /**
     * Converts a TLS named curve given as string to a corresponding integer value found in class NamedCurve.
     * There are no magic values to indicate a failed conversion, so an IllegalArgumentException is thrown instead.   
     * @param namedCurve TLS named curve given as string
     * @return corresponding integer value found in class NamedCurve
     * @throws IllegalArgumentException if conversion fails
     */
    public static int convertNamedCurveStringToInt(String namedCurve) throws IllegalArgumentException {
        return convertBCEnumerationStringToInt(NamedCurve.class, namedCurve);
    }
    
    /**
     * Converts a TLS named curve given as integer value found in class NamedCurve to a corresponding string.
     * @param namedCurve TLS named curve given as integer value found in class NamedCurve
     * @return corresponding string or null, if conversion fails
     */
    public static String convertNamedCurveIntToString(int namedCurve) {
        return convertBCEnumerationIntToString(NamedCurve.class, namedCurve);
    }
    
    /**
     * Returns all TLS named curves found in class NamedCurve as a list of strings.
     * @return list of strings
     */
    public static LinkedList<String> getAllNamedCurveStrings() {
        return getAllBCEnumerationStrings(NamedCurve.class);
    }
    
    /**
     * Converts a TLS key exchange algorithm given as string to a corresponding integer value found in class KeyExchangeAlgorithm.
     * There are no magic values to indicate a failed conversion, so an IllegalArgumentException is thrown instead.   
     * @param keyExchangeAlgorithm TLS key exchange algorithm given as string
     * @return corresponding integer value found in class KeyExchangeAlgorithm
     * @throws IllegalArgumentException if conversion fails
     */
    public static int convertKeyExchangeAlgorithmStringToInt(String keyExchangeAlgorithm) throws IllegalArgumentException {
        return convertBCEnumerationStringToInt(KeyExchangeAlgorithm.class, keyExchangeAlgorithm);
    }
    
    /**
     * Converts a TLS key exchange algorithm given as integer value found in class KeyExchangeAlgorithm to a corresponding string.
     * @param keyExchangeAlgorithm TLS key exchange algorithm given as integer value found in class KeyExchangeAlgorithm
     * @return corresponding string or null, if conversion fails
     */
    public static String convertKeyExchangeAlgorithmIntToString(int keyExchangeAlgorithm) {
        return convertBCEnumerationIntToString(KeyExchangeAlgorithm.class, keyExchangeAlgorithm);
    }
    
    /**
     * Converts a TLS signature algorithm given as string to a corresponding integer value found in class SignatureAlgorithm.
     * There are no magic values to indicate a failed conversion, so an IllegalArgumentException is thrown instead.   
     * @param keyExchangeAlgorithm TLS signature algorithm given as string
     * @return corresponding integer value found in class SignatureAlgorithm
     * @throws IllegalArgumentException if conversion fails
     */
    public static int convertSignatureAlgorithmStringToInt(String signatureAlgorithm) throws IllegalArgumentException {
        return convertBCEnumerationStringToInt(SignatureAlgorithm.class, signatureAlgorithm);
    }
    
    /**
     * Converts a TLS signature algorithm given as integer value found in class SignatureAlgorithm to a corresponding string.
     * @param keyExchangeAlgorithm TLS signature algorithm given as integer value found in class SignatureAlgorithm
     * @return corresponding string or null, if conversion fails
     */
    public static String convertSignatureAlgorithmIntToString(int signatureAlgorithm) {
        return convertBCEnumerationIntToString(SignatureAlgorithm.class, signatureAlgorithm);
    }

    /**
     * Converts a TLS ECPointFormat (from ECPointFormat extension) given as string to a corresponding integer value found in class ECPointFormat.
     * There are no magic values to indicate a failed conversion, so an IllegalArgumentException is thrown instead.   
     * @param ecPointFormat TLS ECPointFormat given as string
     * @return corresponding integer value found in class ECPointFormat
     * @throws IllegalArgumentException if conversion fails
     */
    public static short convertECPointFormatStringToShort(String ecPointFormat) throws IllegalArgumentException {
        return convertBCEnumerationStringToShort(ECPointFormat.class, ecPointFormat);
    }
    
    /**
     * Converts a TLS ECPointFormat given as integer value found in class ECPointFormat to a corresponding string.
     * @param ecPointFormat TLS ECPointFormat given as integer value found in class ECPointFormat
     * @return corresponding string or null, if conversion fails
     */
    public static String convertECPointFormatShortToString(short ecPointFormat) {
        return convertBCEnumerationShortToString(ECPointFormat.class, ecPointFormat);
    }

    private static int convertBCEnumerationStringToInt(@SuppressWarnings("rawtypes") Class clazz, String aString) throws IllegalArgumentException {
        // search for public final static int fields in class clazz, which should basically be an enumeration.
        for(Field field : clazz.getFields()) {
            Class<?> type = field.getType();
            if(type == int.class){
                if(Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
                    if(field.getName().equalsIgnoreCase(aString)) {
                        try{
                            return field.getInt(null);
                        } catch (IllegalAccessException ignore) {
                            // this should never happen, as we access only public fields
                        }
                    }
                }
            }
        }
        
        throw new IllegalArgumentException("Could not convert string '" + aString + "' to a corresponding int from class " + clazz.getCanonicalName());
    }
    
    private static String convertBCEnumerationIntToString(@SuppressWarnings("rawtypes") Class clazz, int anInt) {
        // search for public final static int fields in class clazz, which should basically be an enumeration.
        for(Field field : clazz.getFields()) {
            Class<?> type = field.getType();
            if(type == int.class){
                if(Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
                    try{
                        if(anInt == field.getInt(null)) {
                                return field.getName();
                        }
                    } catch (IllegalAccessException ignore) {
                        // this should never happen, as we access only public fields
                    }
                }
            }
        }
        
        return null;
    }
    
    private static short convertBCEnumerationStringToShort(@SuppressWarnings("rawtypes") Class clazz, String aString) throws IllegalArgumentException {
        // search for public final static short fields in class clazz, which should basically be an enumeration.
        for(Field field : clazz.getFields()) {
            Class<?> type = field.getType();
            if(type == short.class){
                if(Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
                    if(field.getName().equalsIgnoreCase(aString)) {
                        try{
                            return field.getShort(null);
                        } catch (IllegalAccessException ignore) {
                            // this should never happen, as we access only public fields
                        }
                    }
                }
            }
        }
        
        throw new IllegalArgumentException("Could not convert string '" + aString + "' to a corresponding short from class " + clazz.getCanonicalName());
    }

    private static String convertBCEnumerationShortToString(@SuppressWarnings("rawtypes") Class clazz, short anInt) {
        // search for public final static short fields in class clazz, which should basically be an enumeration.
        for(Field field : clazz.getFields()) {
            Class<?> type = field.getType();
            if(type == short.class){
                if(Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
                    try{
                        if(anInt == field.getShort(null)) {
                                return field.getName();
                        }
                    } catch (IllegalAccessException ignore) {
                        // this should never happen, as we access only public fields
                    }
                }
            }
        }
        
        return null;
    }
    
    private static LinkedList<String> getAllBCEnumerationStrings(@SuppressWarnings("rawtypes") Class clazz) {
        // search for public final static int fields in class clazz, which should basically be an enumeration.
        LinkedList<String> strings = new LinkedList<String>();
        for(Field field : clazz.getFields()) {
            Class<?> type = field.getType();
            if(type == int.class){
                if (Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
                    strings.add(field.getName());
                }
            }
        }
        
        return strings;
    }

    /**
     * Extracts the TLS key exchange algorithm from given TLS cipher suite.
     * @param cipherSuite TLS Cipher suite given as string.
     * @return TLS key exchange algorithm from given TLS cipher suite.
     */
    public static int getKeyExchangeAlgorithmFromCipherSuite(String cipherSuite) {
    	if (cipherSuite != null && !cipherSuite.isEmpty()) {
    		Pattern pattern = Pattern.compile("TLS_(.*)_WITH_.*");
    		Matcher matcher = pattern.matcher(cipherSuite);
    		if (matcher.find()) {
    			String extractedKeyExchangeAlgorithm = matcher.group(1);
    			return convertKeyExchangeAlgorithmStringToInt(extractedKeyExchangeAlgorithm);
    		}
    	}
    	
    	return KeyExchangeAlgorithm.NULL;
    }
    
    /**
     * Extracts the TLS key exchange algorithm from given TLS cipher suite.
     * @param cipherSuite TLS Cipher suite given as integer value found in class CipherSuite.
     * @return TLS key exchange algorithm from given TLS cipher suite.
     */
    public static int getKeyExchangeAlgorithmFromCipherSuite(int cipherSuite) {
    	return getKeyExchangeAlgorithmFromCipherSuite(convertCipherSuiteIntToString(cipherSuite));
    }

    
    /**
     * Converts a TLS SignatureAndHashAlgorithm pair given as string to a corresponding object of class SignatureAndHashAlgorithm.
     * Expected form of string: (HASH)with(SIGNATURE), e.g. SHA256withRSA
     * @param signatureAndHashAlgorithm TLS SignatureAndHashAlgorithm pair given as string
     * @return corresponding object of class SignatureAndHashAlgorithm; or null, if conversion fails
     */
    public static SignatureAndHashAlgorithm convertSignatureAndHashAlgorithmStringToClass(String signatureAndHashAlgorithm) {
        if (signatureAndHashAlgorithm != null && !signatureAndHashAlgorithm.isEmpty()) {
            Pattern pattern = Pattern.compile("(.*)with(.*)");
            Matcher matcher = pattern.matcher(signatureAndHashAlgorithm.toLowerCase(Locale.ROOT));
            if (matcher.find()) {
                String extractedHash = matcher.group(1);
                short hash =  convertBCEnumerationStringToShort(HashAlgorithm.class, extractedHash);
                String extractedSignature = matcher.group(2);
                short signature = convertBCEnumerationStringToShort(SignatureAlgorithm.class, extractedSignature);
                return new SignatureAndHashAlgorithm(hash, signature);
            }
        }
        
        return null;
    }
    
    /**
     * Converts a TLS SignatureAndHashAlgorithm pair given as object of class SignatureAndHashAlgorithm to a corresponding string.
     * Expected form of string: (HASH)with(SIGNATURE), e.g. SHA256withRSA
     * @param signatureAndHashAlgorithm TLS SignatureAndHashAlgorithm pair given as object of class SignatureAndHashAlgorithm
     * @return corresponding string; or null, if conversion fails
     */
    public static String convertSignatureAndHashAlgorithmObjectToString(SignatureAndHashAlgorithm signatureAndHashAlgorithm) {
        if (signatureAndHashAlgorithm != null) {
                String hash =  convertBCEnumerationShortToString(HashAlgorithm.class, signatureAndHashAlgorithm.getHash());
                String signature = convertBCEnumerationShortToString(SignatureAlgorithm.class, signatureAndHashAlgorithm.getSignature());
                if(hash != null && signature != null) {
                    return hash.toUpperCase(Locale.ROOT) + "with" + signature.toUpperCase(Locale.ROOT);
                }
        }
        
        return null;
    }

    /**
     * Returns all DHParameters found in class DHStandardGroups as a list of strings.
     * @return list of strings
     */
    public static LinkedList<String> getAllDHParametersFromDHStandardGroupsStrings() {
        // search for public final static DHParameters fields in class DHStandardGroups
        LinkedList<String> strings = new LinkedList<String>();
        for(Field field : DHStandardGroups.class.getFields()) {
            Class<?> type = field.getType();
            if(type == DHParameters.class){
                if(Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
                    strings.add(field.getName());
                }
            }
        }
        
        return strings;
    }
    
    /**
     * Searches for an equal DHParameters object in class DHStandardGroups and returns its name as a string.
     * @param params DHParameters to search for in class DHStandardGroups
     * @return corresponding string or null, if conversion fails
     */
    public static String convertDHParametersObjectToDHStandardGroupsString(DHParameters params) {
        // search for public final static DHParameters fields in class DHStandardGroups
        for(Field field : DHStandardGroups.class.getFields()) {
            Class<?> type = field.getType();
            if(type == DHParameters.class){
                if(Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
                    try{
                        if(field.get(null).equals(params)) {
                                return field.getName();
                        }
                    } catch (IllegalAccessException ignore) {
                        // this should never happen, as we access only public fields
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Searches for a DHParameters object in class DHStandardGroups by its name.
     * @param params name of DHParameters to search for in class DHStandardGroups
     * @return corresponding object or null, if conversion fails
     */
    public static DHParameters convertDHStandardGroupsStringToDHParametersObject(String params) {
        // search for public final static DHParameters fields in class DHStandardGroups
        for(Field field : DHStandardGroups.class.getFields()) {
            Class<?> type = field.getType();
            if(type == DHParameters.class){
                if(Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
                    try{
                        if(field.getName().equals(params)) {
                                return (DHParameters) field.get(null);
                        }
                    } catch (IllegalAccessException ignore) {
                        // this should never happen, as we access only public fields
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Converts a configuration string for TLS versions to the corresponding BouncyCastle ProtocolVersion object.
     * If conversion fails, throws a RuntimeException.
     * @param protocol String as defined by enumeration {@link PROTOCOLS}
     * @return the corresponding BouncyCastle ProtocolVersion object
     */
    public static ProtocolVersion convertProtocolVersionFromEnumToObject(String protocol) {
        // SSLv2 not supported by BouncyCastle
        if (PROTOCOLS.sslv3.name().equalsIgnoreCase(protocol)) {
            return ProtocolVersion.SSLv3;
        } else if (PROTOCOLS.tls10.name().equalsIgnoreCase(protocol)) {
            return ProtocolVersion.TLSv10;
        } else if (PROTOCOLS.tls11.name().equalsIgnoreCase(protocol)) {
            return ProtocolVersion.TLSv11;
        } else if (PROTOCOLS.tls12.name().equalsIgnoreCase(protocol)) {
            return ProtocolVersion.TLSv12;
        }
        
        throw new RuntimeException("Could not convert '" + protocol + "' to a valid TLS ProtocolVerison for BouncyCastle.");
    }
    
    // some old utility functions
    public static Certificate convertCertificateChainFromJavaToBC(X509Certificate[] certificateChain) throws CertificateEncodingException {
        org.bouncycastle.asn1.x509.Certificate[] tempList = new org.bouncycastle.asn1.x509.Certificate[certificateChain.length];
        for(int i=0 ; i < certificateChain.length ; i++) {
            X509CertificateHolder sigCert = new JcaX509CertificateHolder(certificateChain[i]);
            tempList[i] = sigCert.toASN1Structure();
        }
        return new Certificate(tempList);
    }
    
}
