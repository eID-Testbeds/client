package com.secunet.ipsmall.log;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

public class SwingHandler extends Handler {

	private JTextComponent component;

	public SwingHandler() {
		super();
	}

	public SwingHandler(JTextComponent txtComp, Formatter formatter) {
		component = txtComp;
		setFormatter(formatter);
	}

	public void setComponent(final JTextComponent textComp)
	{
		component = textComp;
	}

	@Override
	public void publish(LogRecord record)
	{
		if (component == null || record == null || !isLoggable(record))
			return;

	
		final String theLog = getFormatter().format(record);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				component.setText(component.getText() + theLog);
			}
		});
	}

	@Override
	public void flush()
	{
		// nothing to do
	}

	@Override
	public void close() throws SecurityException
	{
		// nothing to do
	}

}
