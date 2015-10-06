package com.secunet.ipsmall.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Contains several methods for file operations.
 */
public class FileUtils {
    /**
     * Deletes a directory recursively.
     * 
     * @param source Directory to delete.
     * 
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void deleteDir(final File source) throws IOException, FileNotFoundException {
        if (source.exists()) {
            File[] files = source.listFiles();
            for (File file : files) {
                if (file.isDirectory())
                    deleteDir(file);
                else
                    if(!file.delete())
                        throw new IOException("Unable to delete: " + file.getAbsolutePath());
            }
            
            source.delete();
        } else
            throw new FileNotFoundException("Unable to delete directory: Source path not found: " + source.getAbsolutePath());
    }
    
    /**
     * Copies a directory.
     * 
     * @param source Source directory.
     * @param dest Destination directory.
     * @param overwrite If set to 'true', all existing files in destination directory will be overwritten by files from source directory.
     * 
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void copyDir(final File source, final File dest, final boolean overwrite) throws IOException, FileNotFoundException {
        copyDir(source, dest, overwrite, false);
    }
    
    /**
     * Copies a directory.
     * 
     * @param source Source directory.
     * @param dest Destination directory.
     * @param overwrite If set to 'true', all existing files in destination directory will be overwritten by files from source directory.
     * @param mergeConfigProperties If set to 'true', settings from 'config.properties' files in source are added or overwritten in destination.
     * 
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void copyDir(final File source, final File dest, final boolean overwrite, final boolean mergeConfigProperties) throws IOException, FileNotFoundException {
        if (source.exists()) {
            File[] files = source.listFiles();
            
            if (!dest.exists())
                dest.mkdirs();
            
            for (File file : files) {
                if (file.isDirectory()) // copy sub directory
                    copyDir(file, new File(dest.getAbsolutePath() + System.getProperty("file.separator") + file.getName()), overwrite, mergeConfigProperties);
                else { // copy file
                    if (file.getName().equals("config.properties") && mergeConfigProperties) { // merge
                        mergeConfigProperties(file, new File(dest.getAbsolutePath() + System.getProperty("file.separator") + file.getName()));
                    } else // copy
                        copyFile(file, new File(dest.getAbsolutePath() + System.getProperty("file.separator") + file.getName()), overwrite);
                }
            }
        } else
            throw new FileNotFoundException("Unable to copy directory: Source path not found: " + source.getAbsolutePath());
    }
    
    /**
     * Copies a file.
     * 
     * @param source Source file.
     * @param dest Destination file.
     * @param overwrite If set to 'true', file will be overwritten if already exists.
     * 
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void copyFile(final File source, final File dest, final boolean overwrite) throws IOException, FileNotFoundException {
        // cancel if file exists and shall be not overwritten.
        if (dest.exists()) {
            if (overwrite)
                dest.delete();
            else
                return;
        }
        
        // create parent directory if not exists yet.
        if (!dest.getParentFile().exists())
            dest.getParentFile().mkdirs();
        
        // copy the file
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(source));;
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest, true));; 
        
        int bytes = 0;
        while ((bytes = in.read()) != -1) {
            out.write(bytes);
        }
        in.close();
        out.close();
    }
    
    /**
     * Updates or adds (if not exists yet) an attribute value pair in a properties file.
     * 
     * @param propertiesFile The properties file.
     * @param attribute Name of attribute.
     * @param value The value.
     * 
     * @throws IOException
     */
    public static void updatePropertiesFile(final File propertiesFile, final String attribute, final String value) throws IOException {
        // create file if does not exist
        if (!propertiesFile.exists()) {
            propertiesFile.getParentFile().mkdirs();
            propertiesFile.createNewFile();
        }
        
        // fix escaping
        String escapedValue = value;
        escapedValue = escapedValue.replace("\\", "\\\\");
        escapedValue = escapedValue.replace("\n", "\\n");
        escapedValue = escapedValue.replace("\t", "\\t");
        
        // update if attribute exists
        boolean updated = false;
        StringBuffer buffer = new StringBuffer();
        BufferedReader in = new BufferedReader(new FileReader(propertiesFile));
        String line = null;
        while ((line = in.readLine()) != null) {
            if (line.startsWith(attribute + "=")) {
                buffer.append(attribute + "=" + escapedValue); // append line with new attribute value
                updated= true;
            } else
                buffer.append(line); // append old line
            
            buffer.append(System.getProperty("line.separator"));
        }
        in.close();

        // append attribute and value if not found in file
        if (!updated)
            buffer.append(attribute + "=" + escapedValue);
        
        // write output
        PrintWriter out = new PrintWriter(new FileWriter(propertiesFile));
        out.print(buffer);
        out.close();
    }
    
    /**
     * Adds or updates properties from source file to destination file.
     * 
     * @param source Source file.
     * @param dest Destination file.
     * 
     * @throws IOException
     * @throws FileNotFoundException
     */
    private static void mergeConfigProperties(final File source, final File dest) throws IOException, FileNotFoundException {
        // read all properties from source
        Properties configProperties = new Properties();
        configProperties.load(new FileReader(source));
        
        // update attributes and values in destination file
        for (Enumeration<?> attributes = configProperties.propertyNames(); attributes.hasMoreElements();) {
            String attribute = (String) attributes.nextElement();
            String value = configProperties.getProperty(attribute);
            
            updatePropertiesFile(dest, attribute, value);
        }
    }
    
    /**
     * Writes a given message to file.
     * 
     * @param file File to write.
     * @param message The message to write in file.
     * 
     * @throws IOException
     */
    public static void writeMessage2File(File file, String message) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write(message);
        writer.close();
    }
    
    /**
     * Reads specific attribute value from properties file.
     * 
     * @param propertiesFile The properties file.
     * @param attribute Attribute to read.
     * @return The value.
     * 
     * @throws IOException
     */
    public static String readAttributeValue(File propertiesFile, String attribute) throws IOException {
        String ret = "";
        
        if (propertiesFile.exists()) {
            BufferedReader in = new BufferedReader(new FileReader(propertiesFile));
                
            String line = null;
            while ((line = in.readLine()) != null) {
                if (line.startsWith(attribute + "=")) {
                    ret = line.substring(attribute.length());
                    ret = ret.trim();
                    if (ret.startsWith("=")) {
                        ret = ret.replaceFirst("=", "");
                        ret = ret.trim();
                    }
                    break;
                }
            }
            in.close();
        }
        
        return ret;
    }
}
