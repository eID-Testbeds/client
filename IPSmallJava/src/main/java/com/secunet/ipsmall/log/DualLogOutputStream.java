package com.secunet.ipsmall.log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Writes to in constructor given stream and also in inherited behavior of {@link LogOutputStream} 
 */
public class DualLogOutputStream extends LogOutputStream {
    
    private  OutputStream traditionalOutputStream = null;
    
    /**
     * @param stream should be <code>System.out</code> or <code>System.err</code>. Must not be null
     */
    public DualLogOutputStream(final PrintStream stream) {
        traditionalOutputStream = stream;
    }

    @Override
    public void write(int b) throws IOException
    {
        traditionalOutputStream.write(b);
        
        super.write(b); // TODO check why log has 2 line endings \r\n ?!? encoding?
    }
    
}
