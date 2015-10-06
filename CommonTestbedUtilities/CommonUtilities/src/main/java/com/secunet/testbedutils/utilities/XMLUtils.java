package com.secunet.testbedutils.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Some static utility methods for JAXB handling and XML<->DOM converting
 */
public class XMLUtils {
	
    
    /**
     * Marshal a JAXB element to a XML file
     *
     * @param jaxbElement
     *            The root of content tree to be marshalled
     * @param strXMLFilePath
     *            XML output file path
     * @throws Exception
     *             in error case
     */
    public static void doMarshalling(Object jaxbElement, String strXMLFilePath) throws Exception {
        if (jaxbElement == null) {
            throw new RuntimeException("No JAXB element to marshal (null)!");
        }
        if (strXMLFilePath == null) {
            throw new RuntimeException("No XML file path (null)!");
        }
        FileOutputStream fos = null;
        try {
            JAXBContext jaxbCtx = JAXBContext.newInstance(jaxbElement.getClass().getPackage().getName());
            Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8"); // NOI18N
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            fos = new FileOutputStream(strXMLFilePath);
            marshaller.marshal(jaxbElement, fos);// System.out);
        } catch (Exception e) {
//            Logger.XMLEval.logState("Marshalling failed: " + e.getMessage(), LogLevel.Error);
            throw e;
        } finally {
            if (fos != null) {
                fos.close();
                fos = null;
            }
        }
    }
    
    /**
     * Marshal a JAXB element to a XML DOM document
     *
     * @param jaxbElement
     *            The root of content tree to be marshalled
     * @return XML DOM document
     * @throws Exception
     *             in error case
     */
    public static Document doMarshallingJAXBObject(Object jaxbElement) throws Exception {
        if (jaxbElement == null) {
            throw new RuntimeException("No JAXB element to marshal (null)!");
        }
        Document doc = null;
        try {
            JAXBContext jaxbCtx = JAXBContext.newInstance(jaxbElement.getClass().getPackage().getName());
            Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8"); // NOI18N
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.newDocument();
            marshaller.marshal(jaxbElement, doc);
            doc.getDocumentElement().normalize();
        } catch (Exception e) {
//            Logger.XMLEval.logState("Marshalling failed: " + e.getMessage(), LogLevel.Error);
            throw e;
        }
        
        return doc;
    }
    
    /**
     * Unmarshal XML data from XML file path using XSD string and return the resulting JAXB content tree
     * 
     * @param dummyCtxObject
     *            Dummy contect object for creating related JAXB context
     * @param strXMLFilePath
     *            XML file path
     * @param strXSD
     *            XSD
     * @return resulting JAXB content tree
     * @throws Exception
     *             in error case
     */
    public static Object doUnmarshallingFromXMLFile(Object dummyCtxObject, String strXMLFilePath, String strXSD) throws Exception {
        if (dummyCtxObject == null) {
            throw new RuntimeException("No dummy context object (null)!");
        }
        if (strXMLFilePath == null) {
            throw new RuntimeException("No XML file path (null)!");
        }
        if (strXSD == null) {
            throw new RuntimeException("No XSD (null)!");
        }
        
        Object unmarshalledObject = null;
        
        try {
            JAXBContext jaxbCtx = JAXBContext.newInstance(dummyCtxObject.getClass().getPackage().getName());
            Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            // unmarshaller.setValidating(true);
            /*
            javax.xml.validation.Schema schema =
            javax.xml.validation.SchemaFactory.newInstance(
            javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(
            new java.io.File(m_strXSDFilePath));
             */
            StringReader reader = null;
            FileInputStream fis = null;
            try {
                reader = new StringReader(strXSD);
                javax.xml.validation.Schema schema = javax.xml.validation.SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(
                        new StreamSource(reader));
                unmarshaller.setSchema(schema);
                
                fis = new FileInputStream(strXMLFilePath);
                unmarshalledObject = unmarshaller.unmarshal(fis);
            } finally {
                if (fis != null) {
                    fis.close();
                    fis = null;
                }
                if (reader != null) {
                    reader.close();
                    reader = null;
                }
            }
            // } catch (JAXBException e) {
            // //m_logger.error(e);
            // throw new OrderException(e);
        } catch (Exception e) {
//            Logger.XMLEval.logState("Unmarshalling failed: " + e.getMessage(), LogLevel.Error);
            throw e;
        }
        
        return unmarshalledObject;
    }
    
    /**
     * Unmarshal XML data from XML DOM document using XSD string and return the resulting JAXB content tree
     *
     * @param dummyJAXBObject
     *            Dummy contect object for creating related JAXB context
     * @param doc
     *            XML DOM document
     * @param strXSD
     *            XSD
     * @return resulting JAXB content tree
     * @throws Exception
     *             in error case
     */
    public static Object doUnmarshallingFromDOMDocument(Object dummyJAXBObject, Document doc, String strXSD) throws Exception {
        if (dummyJAXBObject == null) {
            throw new RuntimeException("No dummy context objekt (null)!");
        }
        if (doc == null) {
            throw new RuntimeException("No XML DOM document (null)!");
        }
        if (strXSD == null) {
            throw new RuntimeException("No XSD document (null)!");
        }
        
        Object unmarshalledObject = null;
        StringReader reader = null;
        
        try {
            JAXBContext jaxbCtx = JAXBContext.newInstance(dummyJAXBObject.getClass().getPackage().getName());
            Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            reader = new StringReader(strXSD);
            
            javax.xml.validation.Schema schema = javax.xml.validation.SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(
                    new StreamSource(reader));
            unmarshaller.setSchema(schema);
            
            unmarshalledObject = unmarshaller.unmarshal(doc);
        } catch (Exception e) {
//            Logger.XMLEval.logState("Unmarshalling failed: " + e.getMessage(), LogLevel.Error);
            throw e;
        } finally {
            if (reader != null) {
                reader.close();
                reader = null;
            }
        }
        
        return unmarshalledObject;
    }
    
    /**
     * Unmarshal XML data from XML file path using XSD from file path and return the resulting JAXB content tree
     *
     * @param dummyCtxObject
     *            Dummy contect object for creating related JAXB context
     * @param strXMLFilePath
     *            XML file path
     * @param strXSDFilePath
     *            XSD file path
     * @return resulting JAXB content tree
     * @throws Exception
     *             in error case
     */
    public static Object doUnmarshallingFromFiles(Object dummyCtxObject, String strXMLFilePath, String strXSDFilePath) throws Exception {
        if (dummyCtxObject == null) {
            throw new RuntimeException("No dummy context object (null)!");
        }
        if (strXMLFilePath == null) {
            throw new RuntimeException("No XML file path (null)!");
        }
        if (strXSDFilePath == null) {
            throw new RuntimeException("No XSD file path (null)!");
        }
        
        Object unmarshalledObject = null;
        
        FileInputStream fis = null;
        try {
            JAXBContext jaxbCtx = JAXBContext.newInstance(dummyCtxObject.getClass().getPackage().getName());
            Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            // unmarshaller.setValidating(true);
            javax.xml.validation.Schema schema = javax.xml.validation.SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(
                    new java.io.File(strXSDFilePath));
            unmarshaller.setSchema(schema); // register schema for validation
            
            fis = new FileInputStream(strXMLFilePath);
            unmarshalledObject = unmarshaller.unmarshal(fis);
        } catch (Exception e) {
//            Logger.XMLEval.logState("Unmarshalling failed: " + e.getMessage(), LogLevel.Error);
            throw e;
        } finally {
            if (fis != null) {
                fis.close();
                fis = null;
            }
        }
        
        return unmarshalledObject;
    }
    
    /**
     * Unmarshal XML data from XML string using XSD string and return the resulting JAXB content tree
     *
     * @param dummyCtxObject
     *            Dummy contect object for creating related JAXB context
     * @param strXML
     *            XML
     * @param strXSD
     *            XSD
     * @return resulting JAXB content tree
     * @throws Exception
     *             in error case
     */
    public static Object doUnmarshalling(Object dummyCtxObject, String strXML, String strXSD) throws Exception {
        if (dummyCtxObject == null) {
            throw new RuntimeException("No dummy context objekt (null)!");
        }
        if (strXML == null) {
            throw new RuntimeException("No XML document (null)!");
        }
        if (strXSD == null) {
            throw new RuntimeException("No XSD document (null)!");
        }
        
        Object unmarshalledObject = null;
        StringReader readerXSD = null;
        StringReader readerXML = null;
        
        try {
            JAXBContext jaxbCtx = JAXBContext.newInstance(dummyCtxObject.getClass().getPackage().getName());
            Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            readerXSD = new StringReader(strXSD);
            readerXML = new StringReader(strXML);
            
            javax.xml.validation.Schema schema = javax.xml.validation.SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(
                    new StreamSource(readerXSD));
            unmarshaller.setSchema(schema);
            
            unmarshalledObject = unmarshaller.unmarshal(new StreamSource(readerXML));
        } catch (Exception e) {
//            Logger.XMLEval.logState("Unmarshalling failed: " + e.getMessage(), LogLevel.Error);
            throw e;
        } finally {
            if (readerXSD != null) {
                readerXSD.close();
                readerXSD = null;
            }
            if (readerXML != null) {
                readerXML.close();
                readerXML = null;
            }
        }
        
        return unmarshalledObject;
    }
    
    /**
     * Convert XML DOM document to a XML string representation
     *
     * @param doc
     *            XML DOM document
     * @return XML string
     * @throws Exception
     *             in error case
     */
    public static String xmlDOMDocumentToString(Document doc) throws Exception {
        if (doc == null) {
            throw new RuntimeException("No XML DOM document (null)!");
        }
        StringWriter stringWriter = new StringWriter();
        String strDoc = null;
        
        try {
            StreamResult streamResult = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            // transformerFactory.setAttribute("nIndent-number", new Integer(4));
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.transform(new DOMSource(doc.getDocumentElement()), streamResult);
            stringWriter.flush();
            strDoc = stringWriter.toString();
        } catch (Exception e) {
//            Logger.XMLEval.logState("Parsing of XML DOM document failed: " + e.getMessage(), LogLevel.Error);
            throw e;
        } finally {
            if (stringWriter != null) {
                stringWriter.close();
                stringWriter = null;
            }
        }
        
        return strDoc;
    }
    
    /**
     * Convert XML string to a XML DOM document
     *
     * @param strXML
     *            XML
     * @return XML DOM document
     * @throws Exception
     *             in error case
     */
    public static Document xmlStringToDOMDocument(String strXML) throws Exception {
        if (strXML == null) {
            throw new RuntimeException("No XML input given(null)!");
        }
        
        StringReader reader = null;
        Document doc = null;
        try {
            reader = new StringReader(strXML);
            InputSource inputSource = new InputSource(reader);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(inputSource);
            doc.getDocumentElement().normalize();
        } catch (Exception e) {
//            Logger.XMLEval.logState("Parsing of XML input failed: " + e.getMessage(), LogLevel.Error);
            throw e;
        } finally {
            if (reader != null) {
                reader.close();
                reader = null;
            }
        }
        
        return doc;
    }
    
    /**
     * Convert XML from file path to a XML DOM document
     *
     * @param strXMLFilePath
     *            XML file path
     * @return XML DOM document
     * @throws Exception
     *             in error case
     */
    public static Document xmlFileToDOMDocument(String strXMLFilePath) throws Exception {
        if (strXMLFilePath == null) {
            throw new RuntimeException("No XML path given (null)!");
        }
        
        Document doc = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            File xmlFile = new File(strXMLFilePath);
            doc = db.parse(xmlFile);
            doc.getDocumentElement().normalize();
        } catch (Exception e) {
//            Logger.XMLEval.logState("Parsing of XML input failed: " + e.getMessage(), LogLevel.Error);
            throw e;
        }
        
        return doc;
    }
    
    /**
     * Pretty format a given XML document
     *
     * @param strInput
     *            Valid XML document (No validity check yet!)
     * @param nIndent
     *            Indent
     * @return Formatted XML document
     * @throws Exception
     *             in error case
     */
    public static String prettyFormat(String strInput, int nIndent) throws Exception {
        try {
            Source xmlInput = new StreamSource(new StringReader(strInput));
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", nIndent);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(nIndent));
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        } catch (Exception e) {
//            Logger.XMLEval.logState("Pretty formatting: " + e.getMessage(), LogLevel.Error);
            throw e;
        }
    }
    
    /**
     * Pretty format a given XML document with indent of 2
     *
     * @param strInput
     *            Valid XML document (No validity check yet!)
     * @return Formatted XML document
     * @throws Exception
     *             in error case
     */
    public static String prettyFormat(String strInput) throws Exception {
        return prettyFormat(strInput, 2);
    }
    
} // class XMLUtils

/******************************************************************************/
