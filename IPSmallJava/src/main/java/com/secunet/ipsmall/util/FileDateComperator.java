package com.secunet.ipsmall.util;

import java.io.File;
import java.util.Comparator;

/**
 * Compares files by last modified date
 */
public class FileDateComperator implements Comparator<File> {

    @Override
    public int compare(File f1, File f2) {
        return (int)(f1.lastModified() - f2.lastModified());
    }
    
}
