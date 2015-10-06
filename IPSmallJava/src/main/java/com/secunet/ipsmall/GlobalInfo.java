package com.secunet.ipsmall;

import java.io.IOException;
import java.util.Properties;

import com.secunet.ipsmall.log.Logger;

/**
 * Contains some global information.
 */
public enum GlobalInfo {
    Title("globalinfo.properties", "title"),
    Copyright("globalinfo.properties", "copyright"),
    SoftwareVersion("globalinfo.properties", "version"),
    PublishDate("globalinfo.properties", "date"),
    LogVersion("globalinfo.properties", "logversion");
    
    private final String value;

    /**
     * Create a GlobalInfo with given value.
     * @param value The value.
     */
    private GlobalInfo(String value) {
        this.value = value;
    }

    /**
     * Creates a GlobalInfo with value from properties file (must be present in resources).
     * @param propertiesFileName Name of properties file.
     * @param key Key for value in properties file.
     */
    private GlobalInfo(String propertiesFileName, String key) {
        String value = "";

        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesFileName));
            value = properties.getProperty(key);
        } catch (IOException e) {
            Logger.Global.logState("Unable to load global infos: " + e.getMessage());
        }

        this.value = value;
    }

    /**
     * Gets value of GlobalInfo.
     * @return The value.
     */
    public String getValue() {
        return this.value;
    }
}
