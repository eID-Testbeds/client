package com.secunet.log4j.plugin;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.Arrays;

import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.FileManager;
import org.apache.logging.log4j.core.appender.ManagerFactory;

public class CustomXMLFileManager extends FileManager {
    
    private static final CustomXMLFileManagerFactory FACTORY = new CustomXMLFileManagerFactory();

    protected CustomXMLFileManager(String fileName, OutputStream os, boolean append, boolean locking, String advertiseURI,
            Layout<? extends Serializable> layout, int bufferSize) {
        super(fileName, os, append, locking, advertiseURI, layout, bufferSize);
    }
    
    @Override
    protected synchronized void write(final byte[] bytes, final int offset, final int length)  {
        removeFooter();
        super.write(bytes, offset, length);
        writeFooter();
    }
    
    @Override
    protected void writeFooter() {
        if (!removeFooter(true)) {
            byte[] footer = this.layout.getFooter();
            super.write(footer, 0, footer.length);
        }
    }
    
    protected boolean removeFooter() {
        return removeFooter(false);
    }
    
    protected boolean removeFooter(boolean onlycheck) {
        byte[] footer = this.layout.getFooter();
        return removeLastContent(onlycheck, footer);
    }
    
    protected boolean removeLastContent(boolean onlycheck, byte[] content) {
        boolean result = false;
        
        File file = new File(getFileName());
        if (file.exists()) {
            RandomAccessFile ramFile = null;
            byte[] lastBytes = new byte[content.length];
            try {
                // get last bytes
                ramFile = new RandomAccessFile(file,"rwd");
                ramFile.seek(file.length() - content.length);
                ramFile.read(lastBytes, 0, lastBytes.length);
                
                result = Arrays.equals(lastBytes, content);
                if (!onlycheck && result)
                    ramFile.setLength(file.length() - content.length);
                
            } catch (Exception e) {
                result = false;
            }
            
            if (ramFile != null)
                try {
                    ramFile.close();
                } catch (IOException e) {
                }
        }
        
        return result;
    }
    
    public static CustomXMLFileManager getCustomXMLFileManager(final String fileName, final boolean append, boolean locking,
            final boolean bufferedIo, final String advertiseUri, final Layout<? extends Serializable> layout,
            final int bufferSize) {

        if (locking && bufferedIo) {
            locking = false;
        }
        return (CustomXMLFileManager) getManager(fileName, new FactoryData(append, locking, bufferedIo, bufferSize,
                advertiseUri, layout), FACTORY);
    }
    
    /**
     * Factory Data.
     */
    private static class FactoryData {
        private final boolean append;
        private final boolean locking;
        private final boolean bufferedIO;
        private final int bufferSize;
        private final String advertiseURI;
        private final Layout<? extends Serializable> layout;

        /**
         * Constructor.
         * @param append Append status.
         * @param locking Locking status.
         * @param bufferedIO Buffering flag.
         * @param bufferSize Buffer size.
         * @param advertiseURI the URI to use when advertising the file
         */
        public FactoryData(final boolean append, final boolean locking, final boolean bufferedIO, final int bufferSize,
                final String advertiseURI, final Layout<? extends Serializable> layout) {
            this.append = append;
            this.locking = locking;
            this.bufferedIO = bufferedIO;
            this.bufferSize = bufferSize;
            this.advertiseURI = advertiseURI;
            this.layout = layout;
        }
    }

    /**
     * Factory to create a FileManager.
     */
    private static class CustomXMLFileManagerFactory implements ManagerFactory<FileManager, FactoryData> {

        /**
         * Create a FileManager.
         * @param name The name of the File.
         * @param data The FactoryData
         * @return The FileManager for the File.
         */
        @Override
        public CustomXMLFileManager createManager(final String name, final FactoryData data) {
            final File file = new File(name);
            final File parent = file.getParentFile();
            if (null != parent && !parent.exists()) {
                parent.mkdirs();
            }

            OutputStream os;
            try {
                String altName = name;
                
                if (name.isEmpty()) {
                    altName = "tmp_log.xml"; // not used for file output stream, but as identifier for file manager object.
                    os = new NullOutputStream();
                }
                else
                    os = new FileOutputStream(altName, data.append);
                
                int bufferSize = data.bufferSize;
                if (data.bufferedIO) {
                    os = new BufferedOutputStream(os, bufferSize);
                } else {
                    bufferSize = -1; // signals to RollingFileManager not to use BufferedOutputStream
                }
                return new CustomXMLFileManager(altName, os, data.append, data.locking, data.advertiseURI, data.layout, bufferSize);
            } catch (final FileNotFoundException ex) {
                LOGGER.error("FileManager (" + name + ") " + ex);
            }
            return null;
        }
    }
    
    /**
     * An OutputStream, which writes to nowhere.
     */
    public static class NullOutputStream extends OutputStream {
        
        @Override
        public void write(byte[] b) throws IOException {
            // write nothing
        }
        
        @Override
        public void write(byte[] b, int offset, int length) throws IOException {
            // write nothing
        }

        @Override
        public void write(int b) throws IOException {
            // write nothing
        }
      }
    
}
