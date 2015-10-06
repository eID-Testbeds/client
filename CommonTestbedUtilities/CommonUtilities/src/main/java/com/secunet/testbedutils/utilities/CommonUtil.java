package com.secunet.testbedutils.utilities;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CommonUtil {
	
	/**
	 * Reads data from a file into a byte array
	 * @param filename The full file name including the path
	 * @return
	 * @throws IOException
	 */
    public static byte[] readFromFile(String filename) throws IOException {
        FileInputStream stream = null;
        ByteArrayOutputStream out = null;
        try {
            byte buf[] = new byte[1024 * 32];
            stream = new FileInputStream(filename);
            BufferedInputStream in = new BufferedInputStream(stream);
            out = new ByteArrayOutputStream();
            
            int ret = 0;
            while ((ret = in.read(buf)) != -1) {
                out.write(buf, 0, ret);
            }
            
            return out.toByteArray();
        } finally {
            if (stream != null)
                stream.close();
            
            if (out != null)
                out.close();
        }
    }
    
    /**
     * Reads data from a file into a {@link String}
	 * @param filename The full file name including the path
     * @return
     * @throws IOException
     */
    public static String readFromFileAsString(String filename) throws IOException {
        return new String(readFromFile(filename));
    }
    
    public static byte[] convertUUID(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
    
    /*
     * public static byte[] concatArrays(byte[] A , byte[] B) { int aLen =
     * A.length; int bLen = B.length; byte[] C= new byte[aLen+bLen];
     * System.arraycopy(A, 0, C, 0, aLen); System.arraycopy(B, 0, C, aLen,
     * bLen);
     * 
     * return C; }
     */
    /**
     * Concatenates the given arrays in order of their occurence
     * @param arrays
     * @return
     */
    public static byte[] concatArrays(byte[]... arrays) {
        int size = 0;
        for (byte[] cur : arrays) {
            if (cur == null)
                continue;
            size = size + cur.length;
        }
        
        byte[] out = new byte[size];
        
        int curPos = 0;
        for (byte[] cur : arrays) {
            if (cur == null)
                continue;
            System.arraycopy(cur, 0, out, curPos, cur.length);
            curPos = curPos + cur.length;
        }
        
        return out;
    }
    
    /**
     * Removes leading zero bytes from a byte array
     * @param in
     * @return
     */
    public static byte[] removeLeadingZeros(byte[] in) {
        if (in.length > 1) {
            
            int i = 0;
            while (in[i] == 0x00) {
                i++;
            }
            
            byte[] out = new byte[in.length - i];
            System.arraycopy(in, i, out, 0, in.length - i);
            
            return out;
        }
        return in;
    }
    
    public static boolean isXML(final File f) {
        return f.isFile() && f.canRead() && f.exists() && f.getName().endsWith(".xml");
    }
    
    public static boolean isFileAccessible(final String absFilePath) {
        return absFilePath != null && isFileAccessible(new File(absFilePath));
    }
    
    public static boolean isFileAccessible(final File file) {
        return file != null && file.exists() && file.isFile() && file.canRead();
    }
    
    public static boolean isDirectoryAccessible(final String absFilePath) {
        return absFilePath != null && isDirectoryAccessible(new File(absFilePath));
    }
    
    public static boolean isDirectoryAccessible(final File file) {
        return file != null && file.exists() && file.isDirectory() && file.canRead();
    }
    
    public static boolean containsFilebasedTestcaseFolder(final File file) {
        for (File f : file.listFiles()) {
            if (isFilebasedTestcaseFolder(f))
                return true;
        }
        return false;
    }
    
    public static boolean isFilebasedTestcaseFolder(final File file) {
        if (file.isDirectory()) {
            
            // skip common folder to prevent being process as usual TC-folder
            if ("common".equals(file.getName().toLowerCase())) {
                return false;
            }
            
            // boolean hasTokenXML = false;
            boolean hasConfigProperties = false;
            // boolean hasStepp00XML = false;
            // boolean hasPrivateKeyDer = false;
            // boolean hasServerCertificateDer = false;
            // TODO check for more?
            for (File child : file.listFiles()) {
                if (child.isFile()) {
                    hasConfigProperties |= "config.properties".equals(child.getName());
                    // hasStepp00XML |= "Step00.xml".equals(child.getName());
                    // hasPrivateKeyDer |=
                    // "PrivateKey.der".equals(child.getName());
                    // hasServerCertificateDer |=
                    // "ServerCertificate.der".equals(child.getName());
                }
            }
            
            // since we read default-values from common/config.properties, most
            // of these
            // artefacts (being formerly checked as mandatory) are not required
            // anymore
            // but optional only. Remaining mandatory element is
            // config.properties (to define
            // the TC name/description at least
            return hasConfigProperties;
            // return hasTokenXML && hasConfigProperties && hasStepp00XML &&
            // hasPrivateKeyDer && hasServerCertificateDer;
        }
        
        return false;
    }
    
    public static void copyFileTo(final File source, final File destination) throws IOException {
        Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
    }
    
    /**
     * Returns a substring before a given substring separator. Substring can be
     * an empty string if input string starts with substring separator. If
     * separator was not found, input string will be returned.
     * 
     * @param strToParse
     *            String to parse
     * @param substrSeparator
     *            Substring separator
     * @param searchFromEnd
     *            If true, string to parse will be searched backwards
     * 
     * @return Substring if separator was found; else input string
     */
    public static String getSubstringBefore(String strToParse, String substrSeparator, boolean searchFromEnd) {
        if ((strToParse == null) || (substrSeparator == null)) {
            return strToParse;
        }
        String strParsed = strToParse;
        int nPos = 0;
        if (searchFromEnd == false) {
            nPos = strParsed.indexOf(substrSeparator);
        } else {
            nPos = strParsed.lastIndexOf(substrSeparator);
        }
        if (nPos == -1) {
            nPos = strToParse.length();
        }
        return strParsed.substring(0, nPos);
    }
    
    /**
     * Returns a substring after a given substring separator. Substring can be
     * an empty string if input string ends with substring separator. If
     * separator was not found, input string will be returned.
     * 
     * @param strToParse
     *            String to parse
     * @param substrSeparator
     *            Substring separator
     * @param searchFromEnd
     *            If true, string to parse will be searched backwards
     * 
     * @return Substring if separator was found; else input string
     */
    public static String getSubstringAfter(String strToParse, String substrSeparator, boolean searchFromEnd) {
        if ((strToParse == null) || (substrSeparator == null)) {
            return strToParse;
        }
        String strParsed = strToParse;
        int nPos = 0;
        if (searchFromEnd == false) {
            nPos = strParsed.indexOf(substrSeparator);
        } else {
            nPos = strParsed.lastIndexOf(substrSeparator);
        }
        if (nPos == -1) {
            return strToParse;
        } else if (nPos >= (strParsed.length() - 1)) {
            return "";
        }
        return strParsed.substring(nPos + substrSeparator.length(), strParsed.length());
    }
    
    public static int[] commaSeparatedStringToIntArray(String commaSeparatedString) {
        String[] sResult = commaSeparatedStringToStringArray(commaSeparatedString);
        if (sResult == null) {
            return null;
        } else {
            int[] result = new int[sResult.length];
            for (int i = 0; i < result.length; i++) {
                result[i] = Integer.valueOf(sResult[i]);
            }
            return result;
        }
    }
    
    public static String[] commaSeparatedStringToStringArray(String commaSeparatedString) {
        if (commaSeparatedString == null || commaSeparatedString.trim().isEmpty()) {
            return null;
        } else {
            return commaSeparatedString.split(",");
        }
    }
    
    public static String arrayToCommaSeparatedString(int[] array) {
        if (array == null) {
            return null;
        }
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            result.append(array[i] + (i == array.length - 1 ? "" : ", "));
        }
        return result.toString();
    }
    public static String arrayToCommaSeparatedString(String[] array) {
        if (array == null) {
            return null;
        }
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            result.append(array[i] + (i == array.length - 1 ? "" : ", "));
        }
        return result.toString();
    }
    
    public static String listToCommaSeparatedString(List<String> list) {
        if (list == null) {
            return null;
        }
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            result.append(list.get(i) + (i == list.size() - 1 ? "" : ", "));
        }
        return result.toString();
    }
    
    /**
     * Returns true if 's' contains 'searchFor', ignoring case. 
     * @param s
     * @param searchFor
     * @return
     */
    public static boolean containsIgnoreCase(String s, String searchFor) {
        return s.toLowerCase().contains(searchFor.toLowerCase());
    }
    
    /**
     * Returns true if the passed list contains the passed String s, ignoring
     * case.
     * 
     * @param list
     * @param s
     * @return
     */
    public static boolean containsIgnoreCase(List<String> list, String s){
        for (String string : list) {
            if( string.toLowerCase().equals(s.toLowerCase())){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns true if the passed <i>map</a> contains the passed {@link String} key <i>key</i>, ignoring
     * case.
     * 
     * @param map
     * @param key
     * @return
     */
    public static boolean containsKeyIgnoreCase(Map<String, ?> map, String key) {
        Set<String> keys = map.keySet();
        for (String string : keys) {
            if (key.equalsIgnoreCase(string)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get a string from <i>map</i> irgnoring the case of the key
     * @param map
     * @param key
     * @return
     */
    public static String getIgnoreCase(Map<String, String> map, String key) {
        Set<String> keys = map.keySet();
        for (String string : keys) {
            if (key.equalsIgnoreCase(string)) {
                return map.get(string);
            }
        }
        return null;
    }
    
    /**
     * Creates a new {@link List<?>} of {@link String} and adds <i>s</i> to it
     * @param s
     * @return
     */
    public static List<String> createList(String s){
        List<String> result = new ArrayList<String>();
        result.add(s);
        return result;
    }
    
}
