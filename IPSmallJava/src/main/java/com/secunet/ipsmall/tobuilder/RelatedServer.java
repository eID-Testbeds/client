package com.secunet.ipsmall.tobuilder;

/**
 * Represents a related server.
 */
public enum RelatedServer {
    eService("eservice."),
    eIDServer("eidservice.");

    private final String configPrefix;

    /**
     * Creates representation related server.
     * @param configPrefix Prefix for configuration key.
     */
    RelatedServer(String configPrefix) {
        this.configPrefix = configPrefix;
    }

    /**
     * Gets prefix for configuration key.
     * @return Prefix for configuration key.
     */
    public String getConfigPrefix() {
        return configPrefix;
    }
}
