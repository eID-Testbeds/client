package com.secunet.ipsmall.util;

import java.io.File;
import java.util.Comparator;

/**
 * Compares files by last modified date
 */
public class FileDateComperator implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        File f1 = (File)o1;
        File f2 = (File)o2;
        
        return (int)(f1.lastModified() - f2.lastModified());
    }
    
}
