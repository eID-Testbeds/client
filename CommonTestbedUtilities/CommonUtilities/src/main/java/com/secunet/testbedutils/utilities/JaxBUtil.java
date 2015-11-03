package com.secunet.testbedutils.utilities;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

/**
* @author Lukasz Kubik
*/
public class JaxBUtil {

	private static final Logger logger = LogManager.getRootLogger();

	/**
	 * Unmarshalls the XML string given the JaxB class. Also performs schema validation based on the given schema.
	 * @param <T>
	 * @param xml The xml string as input
	 * @param clazz The class of the object which shall be unmarshalled
	 * @param inputSchema The xsd schema
	 * @return 
	 */
	@SuppressWarnings("unchecked")	// no need to check for the instance, if it is the wrong one the JaxB exception will take care of it
	public static <T> T unmarshal(final String xml, Class<?> clazz, InputStream inputSchema) {
		try {
			JAXBContext ctxt = JAXBContext.newInstance(clazz);
			Unmarshaller u = ctxt.createUnmarshaller();
			if(inputSchema != null) {
				if (inputSchema != null) {
					SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
					Schema schema = sf.newSchema(new StreamSource(inputSchema));
					u.setSchema(schema);
				}
			}
			return (T) u.unmarshal(new StringReader(xml));
		} catch (JAXBException | SAXException e) {
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.warn("Unmarshalling error:" + System.getProperty("line.separator") + trace.toString());
		}
		return null;
	}
	
	/**
	 * Unmarshalls the XML string given the JaxB class. Does not perform schema validation.
	 * @param <T>
	 * @param xml The xml string as input
	 * @param clazz The class of the object which shall be unmarshalled
	 * @return 
	 */
	public static <T> T unmarshal(final String xml, Class<?> clazz) {
		return unmarshal(xml, clazz, null);
	}
	
	/**
	 * Unmarshalls the XML document given the JaxB class.
	 * @param <T>
	 * @param xml The xml document as input
	 * @param clazz The class of the object which shall be unmarshalled
	 * @param inputSchema The xsd schema
	 * @return 
	 */
	@SuppressWarnings("unchecked")	// no need to check for the instance, if it is the wrong one the JaxB exception will take care of it
	public static <T> T unmarshal(final File xmlFile, Class<?> clazz, InputStream inputSchema) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			if(inputSchema != null) {
				if (inputSchema != null) {
					SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
					Schema schema = sf.newSchema(new StreamSource(inputSchema));
					jaxbUnmarshaller.setSchema(schema);
				}
			}
			return (T) jaxbUnmarshaller.unmarshal(xmlFile);
		} catch (JAXBException | SAXException e) {
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.warn("Unmarshalling error:" + System.getProperty("line.separator") + trace.toString());
		}
		return null;
	}
	
	/**
	 * Unmarshalls the XML document given the JaxB class.
	 * @param <T>
	 * @param xml The xml document as input
	 * @param clazz The class of the object which shall be unmarshalled
	 * @return 
	 */
	public static <T> T unmarshal(final File xmlFile, Class<?> clazz) {
		return unmarshal(xmlFile, clazz, null);
	}

	/**
	 * Marshalls the given JaxB class into a XML document while explicitly providing the class
	 * @param o The JaxB object instance
	 * @param clazz The class of the object which shall be marshalled
	 * @return 
	 */
	public static String marshall(final Object o, Class<?> clazz) {
		Marshaller m;
		try {
			m = JAXBContext.newInstance(clazz).createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			final StringWriter w = new StringWriter();
			m.marshal(o, w);
			return w.toString();
		} catch (JAXBException e) {
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.warn("text" + System.getProperty("line.separator") + trace.toString());
		}
		return null;
	}
	
	/**
	 * Marshalls the given JaxB class into a XML document
	 * @param o The JaxB object instance
	 * @return 
	 */
	public static String marshall(final Object o) {
		return marshall(o, o.getClass());
	}
	
	/**
	 * Marshalls the given JaxB class into a XML document, without replacing the &, ", \< and \> characters
	 * @param o The JaxB object instance
	 * @return 
	 */
	public static String marshallWithoutReplacing(final Object o) {
		String jaxbString = marshall(o);
		jaxbString = jaxbString.replace("&lt;", "<");
		jaxbString = jaxbString.replace("&gt;", ">");
		jaxbString = jaxbString.replace("&quot;", "\"");
		jaxbString = jaxbString.replace("&amp;", "&");
		return jaxbString;
	}
	
	/**
	 * Gets allowed child element names for a JaxB class.
	 * 
	 * @param elementClass JaxB class representing the parent element.
	 * @return List of allowed child elements.
	 */
	public static String[] getAllowedChildElementNames(Class<?> elementClass) {
		List<String> elementNames = new ArrayList<String>();
		for (Field field : elementClass.getDeclaredFields()) {
			if (field.isAnnotationPresent(XmlElement.class)) {
				elementNames.add(field.getAnnotation(XmlElement.class).name());
			}
		}
		return elementNames.toArray(new String[elementNames.size()]);
	}
	
	/**
	 * Gets getter method <i>get<b>XYZ</b>()</i> for given child element name.
	 * @param childElementName Childs element name (<b>XYZ</b>).
	 * @param elementClass JaxB class representing the parent element.
	 * @return Getter method.
	 */
	public static Method getGetterMethod(String childElementName, Class<?> elementClass) {
		// get correct field name
		String fieldName = null;
		if (childElementName != null && !childElementName.isEmpty()) {
			for (Field field : elementClass.getDeclaredFields()) {
				if (field.isAnnotationPresent(XmlElement.class)) {
					if (childElementName.equals(field.getAnnotation(XmlElement.class).name())) {
						fieldName = field.getName();
					}
				}
			}
		}
		
		// guess getter name from field name
		if (fieldName != null && !fieldName.isEmpty()) {
			for (Method method : elementClass.getMethods()) {
				if (("get" + fieldName.toUpperCase()).equals(method.getName())) {
					return method;
				}
			}
		}	
		return null;
	}
}
