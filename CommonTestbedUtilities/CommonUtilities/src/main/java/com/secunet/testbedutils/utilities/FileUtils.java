package com.secunet.testbedutils.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;

// TODO: @Bernd Neunkirchen: kommentieren
public class FileUtils {
	public static void deleteDir(final File source) throws IOException, FileNotFoundException {
		if (source.exists()) {
			File[] files = source.listFiles();
			for (File file : files) {
				if (file.isDirectory())
					deleteDir(file);
				else if (!file.delete())
					throw new IOException("Unable to delete file: " + file.getAbsolutePath());
			}

			source.delete();
		} else
			throw new FileNotFoundException("Unable to delete directory: Source path not found: " + source.getAbsolutePath());
	}

	public static void copyDir(final File source, final File dest, final boolean overwrite) throws IOException, FileNotFoundException {
		copyDir(source, dest, overwrite, false);
	}

	public static void copyDir(final File source, final File dest, final boolean overwrite, final boolean mergeConfigProperties) throws IOException, FileNotFoundException {
		if (source.exists()) {
			File[] files = source.listFiles();

			if (!dest.exists())
				dest.mkdirs();

			for (File file : files) {
				if (file.isDirectory())
					copyDir(file, new File(dest.getAbsolutePath() + System.getProperty("file.separator") + file.getName()), overwrite, mergeConfigProperties);
				else {
					if (file.getName().equals("config.properties") && mergeConfigProperties) {
						mergeConfigProperties(file, new File(dest.getAbsolutePath() + System.getProperty("file.separator") + file.getName()));
					} else
						copyFile(file, new File(dest.getAbsolutePath() + System.getProperty("file.separator") + file.getName()), overwrite);
				}
			}
		} else
			throw new FileNotFoundException("Unable to copy directory: Source path not found: " + source.getAbsolutePath());
	}

	public static void copyFile(final File source, final File dest, final boolean overwrite) throws IOException, FileNotFoundException {
		if (dest.exists()) {
			if (overwrite)
				dest.delete();
			else
				return;
		}

		if (!dest.getParentFile().exists())
			dest.getParentFile().mkdirs();

		BufferedInputStream in = new BufferedInputStream(new FileInputStream(source));
		;
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest, true));
		;

		int bytes = 0;
		while ((bytes = in.read()) != -1) {
			out.write(bytes);
		}
		in.close();
		out.close();
	}

	public static String getFileContentAsString(String path) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
		String data = new String();
		try {
			String line;
			while ((line = br.readLine()) != null) {
				data = data.concat(line);
			}
		} finally {
			br.close();
		}
		return data;
	}

	public static void updatePropertiesFile(final File propertiesFile, final String attribute, final String value) throws IOException {
		// create file if does not exist
		if (!propertiesFile.exists()) {
			propertiesFile.getParentFile().mkdirs();
			propertiesFile.createNewFile();
		}

		// TODO: fix linebreaks
		String escapedValue = value.replace("\n", "\\n");

		// update if attribute exists
		boolean updated = false;
		StringBuffer buffer = new StringBuffer();
		BufferedReader in = new BufferedReader(new FileReader(propertiesFile));
		String line = null;
		while ((line = in.readLine()) != null) {
			if (line.startsWith(attribute)) {
				buffer.append(attribute + "=" + escapedValue); // append line
																// with new
																// attribute
																// value
				updated = true;
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
	 * Reads an {@link InputStream} into a byte array
	 * @param is The {@link InputStream} to be read
	 * @return The read byte array
	 * @throws IOException 
	 */
	public static byte[] inputStreamToBytes(InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[16384];
		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		buffer.flush();
		return buffer.toByteArray();
	}
}
