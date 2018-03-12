package com.secunet.ipsmall.log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/** Helperclass to create an output stream which uses the logger to print data and behaves more like a writer/printer. */
public class LogOutputStream extends OutputStream {

	private static final int MAX_BYTES = 4 * 1024;

	private ByteArrayOutputStream data = new ByteArrayOutputStream(MAX_BYTES);
	protected int m_logLevel;
	private int last = 0;
	
	@Override
	public void write(int b) throws IOException
	{
	    if ( (last == '\r' && b == '\n') || data.size() > MAX_BYTES) {
		    flush();
		}
		else {
		    if (b != '\r' && b!='\n')
		        data.write(b);
		}
		last = b;
	}

	public void flush() throws IOException
	{
	    Logger.SystemOut.logState(new String(data.toByteArray()));
		close();
		data = new ByteArrayOutputStream(MAX_BYTES);
	}

	public void close() throws IOException
	{
		data.close();
	}

}
