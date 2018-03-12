package com.secunet.ipsmall.eval;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.secunet.ipsmall.ecard.MessageHandler;
import com.secunet.ipsmall.eval.EvaluateResult;
import com.secunet.ipsmall.eval.EvaluateResult.ResultType;
import com.secunet.ipsmall.eval.EvaluationConfig;
import com.secunet.ipsmall.eval.Evaluator;

public class EvalMinMaxOccurenceTest
{
	@Test
	public void test_1_1_1()
	{
		EvaluateResult res = evaluate(generateMessage(1), generateEvalProps(1,1));
		System.out.println(res.toString());
		assertEquals(ResultType.OK, res.getType());
	}
	
	@Test
	public void test_2_2_2()
	{
		EvaluateResult res = evaluate(generateMessage(2), generateEvalProps(2,2));
		System.out.println(res.toString());
		assertEquals(ResultType.OK, res.getType());
	}
	
	@Test
	public void test_1_1_2()
	{
		EvaluateResult res = evaluate(generateMessage(1), generateEvalProps(1,2));
		System.out.println(res.toString());
		assertEquals(ResultType.OK, res.getType());
	}
	
	@Test
	public void test_2_1_2()
	{
		EvaluateResult res = evaluate(generateMessage(2), generateEvalProps(1,2));
		System.out.println(res.toString());
		assertEquals(ResultType.OK, res.getType());
	}
	
	@Test
	public void test_0_1_2()
	{
		EvaluateResult res = evaluate(generateMessage(0), generateEvalProps(1,2));
		System.out.println(res.toString());
		assertEquals(ResultType.OccurenceError, res.getType());
	}
	
	@Test
	public void test_3_1_2()
	{
		EvaluateResult res = evaluate(generateMessage(3), generateEvalProps(1,2));
		System.out.println(res.toString());
		assertEquals(ResultType.OccurenceError, res.getType());
	}
	
	@Test
	public void test_0_1_1()
	{
		EvaluateResult res = evaluate(generateMessage(0), generateEvalProps(1,1));
		System.out.println(res.toString());
		assertEquals(ResultType.OccurenceError, res.getType());
	}

	@Test
	public void test_2_1_1()
	{
		EvaluateResult res = evaluate(generateMessage(2), generateEvalProps(1,1));
		System.out.println(res.toString());
		assertEquals(ResultType.OccurenceError, res.getType());
	}

	@Test
	public void test_1_2_2()
	{
		EvaluateResult res = evaluate(generateMessage(1), generateEvalProps(2,2));
		System.out.println(res.toString());
		assertEquals(ResultType.OccurenceError, res.getType());
	}
	
	@Test
	public void test_3_2_2()
	{
		EvaluateResult res = evaluate(generateMessage(3), generateEvalProps(2,2));
		System.out.println(res.toString());
		assertEquals(ResultType.OccurenceError, res.getType());
	}
	
	@Test
	public void test_AA()
	{
		String msg = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:iso=\"urn:iso:std:iso-iec:24727:tech:schema\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:paos=\"urn:liberty:paos:2006-08\" xmlns:wsa=\"http://www.w3.org/2005/03/addressing\" xmlns:dss=\"urn:oasis:names:tc:dss:1.0:core:schema\" xmlns:ecard=\"http://www.bsi.bund.de/ecard/api/1.1\">\r\n" + 
				"\r\n" + 
				" <soap:Header>\r\n" + 
				"\r\n" + 
				"  <paos:PAOS soap:mustUnderstand=\"1\" soap:actor=\"http://schemas.xmlsoap.org/soap/actor/next\">\r\n" + 
				"\r\n" + 
				"   <paos:Version>urn:liberty:paos:2006-08</paos:Version>\r\n" + 
				"\r\n" + 
				"   <paos:EndpointReference>\r\n" + 
				"\r\n" + 
				"    <paos:Address>http://www.projectliberty.org/2006/01/role/paos</paos:Address>\r\n" + 
				"\r\n" + 
				"    <paos:MetaData>\r\n" + 
				"\r\n" + 
				"     <paos:ServiceType>http://www.bsi.bund.de/ecard/api/1.1/PAOS/GetNextCommand</paos:ServiceType>\r\n" + 
				"\r\n" + 
				"    </paos:MetaData>\r\n" + 
				"\r\n" + 
				"   </paos:EndpointReference>\r\n" + 
				"\r\n" + 
				"  </paos:PAOS>\r\n" + 
				"\r\n" + 
				"  <wsa:ReplyTo>\r\n" + 
				"\r\n" + 
				"   <wsa:Address>http://www.projectliberty.org/2006/02/role/paos</wsa:Address>\r\n" + 
				"\r\n" + 
				"  </wsa:ReplyTo>\r\n" + 
				"\r\n" + 
				"  <wsa:RelatesTo>urn:uuid:A7BB9DEE337949C685E5255F70AC4786</wsa:RelatesTo>\r\n" + 
				"\r\n" + 
				"  <wsa:MessageID>urn:uuid:54b9fffb-d1ce-4b86-af6a-4aa39bd7c369</wsa:MessageID>\r\n" + 
				"\r\n" + 
				" </soap:Header>\r\n" + 
				"\r\n" + 
				" <soap:Body>\r\n" + 
				"\r\n" + 
				"  <TransmitResponse xmlns=\"urn:iso:std:iso-iec:24727:tech:schema\" Profile=\"http://www.bsi.bund.de/ecard/api/1.1\">\r\n" + 
				"\r\n" + 
				"   <Result xmlns=\"urn:oasis:names:tc:dss:1.0:core:schema\">\r\n" + 
				"\r\n" + 
				"    <ResultMajor>http://www.bsi.bund.de/ecard/api/1.1/resultmajor#error</ResultMajor>\r\n" + 
				"\r\n" + 
				"    <ResultMinor>http://www.bsi.bund.de/ecard/api/1.1/resultminor/al/common#unknownError</ResultMinor>\r\n" + 
				"\r\n" + 
				"   </Result>\r\n" + 
				"\r\n" + 
				"   <OutputAPDU>871101dc88e882e14633eb88472f56fb3e6340990290008e084cce26a3893237cd9000</OutputAPDU>\r\n" + 
				"\r\n" + 
				"  </TransmitResponse>\r\n" + 
				"\r\n" + 
				" </soap:Body>\r\n" + 
				"\r\n" + 
				"</soap:Envelope>";
		EvaluateResult res = evaluate(msg, generateEvalProps(2,2));
		System.out.println(res.toString());
		assertEquals(ResultType.OccurenceError, res.getType());
	}
	
	private static EvaluateResult evaluate(String message, String evalProps)
	{
		try
		{
			MessageHandler handler = new MessageHandler(message);
			Properties properties = new Properties();
			properties.load(new StringReader(evalProps));
			String initalNode = "TransmitResponse";
			Map<String, EvaluationConfig> evalSetup = Evaluator.createEvaluationSetup(null, properties, initalNode);
			return Evaluator.createOrderedOccurenceResult(initalNode, evalSetup, handler);
		}
		catch (SAXException | IOException | ParserConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private static String generateEvalProps(int min, int max) {
		String props = "## All possible configuration entries start\r\n" + 
				"# ecard.xmleval.NAME.XMLTag=StartPAOS\r\n" + 
				"# ecard.xmleval.NAME.Query=/Envelope/Body/StartPAOS\r\n" + 
				"# ecard.xmleval.NAME.Children=Child1;Child2;Child3\r\n" + 
				"# ecard.xmleval.NAME.MinOccurrence=0..2147483647\r\n" + 
				"# ecard.xmleval.NAME.MaxOccurrence=0..2147483647\r\n" + 
				"# ecard.xmleval.NAME.Warning=false oder true\r\n" + 
				"# ecard.xmleval.NAME.ForceFailOnError=false oder true\r\n" + 
				"# ecard.xmleval.NAME.ContentRegEx=REGEX_HEX, REGEX_NUMBER, REGEX_URI, REGEX_NUMBER_NONEGATIVE oder eigener RegEx\r\n" + 
				"# ecard.xmleval.NAME.TypeRegEx=\r\n" + 
				"## Map for validating all given types\r\n" + 
				"# ecard.xmleval.NAME.Type.0.key=\r\n" + 
				"# ecard.xmleval.NAME.Type.0.value=\r\n" + 
				"# ecard.xmleval.NAME.Type.1.key=\r\n" + 
				"# ecard.xmleval.NAME.Type.1.value=\r\n" + 
				"## All possible configuration entries END\r\n" + 
				"\r\n" + 
				"## TransmitResponse\r\n" + 
				"ecard.xmleval.TransmitResponse.XMLTag=TransmitResponse\r\n" + 
				"ecard.xmleval.TransmitResponse.Query=/Envelope/Body/TransmitResponse\r\n" + 
				"ecard.xmleval.TransmitResponse.Children=Result;OutputAPDU\r\n" + 
				"ecard.xmleval.TransmitResponse.MinOccurrence=1\r\n" + 
				"ecard.xmleval.TransmitResponse.MaxOccurrence=1\r\n" + 
				"\r\n" + 
				"## Result\r\n" + 
				"ecard.xmleval.Result.XMLTag=Result\r\n" + 
				"ecard.xmleval.Result.MinOccurrence=1\r\n" + 
				"ecard.xmleval.Result.MaxOccurrence=1\r\n" + 
				"ecard.xmleval.Result.Children=ResultMajor;ResultMinor;ResultMessage\r\n" + 
				"## Result.ResultMajor\r\n" + 
				"ecard.xmleval.ResultMajor.XMLTag=ResultMajor\r\n" + 
				"ecard.xmleval.ResultMajor.MinOccurrence=1\r\n" + 
				"ecard.xmleval.ResultMajor.MaxOccurrence=1\r\n" + 
				"ecard.xmleval.ResultMajor.ContentRegEx=http://www.bsi.bund.de/ecard/api/1.1/resultmajor#error\r\n" + 
				"## Result.ResultMinor\r\n" + 
				"ecard.xmleval.ResultMinor.XMLTag=ResultMinor\r\n" + 
				"ecard.xmleval.ResultMinor.MinOccurrence=0\r\n" + 
				"ecard.xmleval.ResultMinor.MaxOccurrence=1\r\n" + 
				"ecard.xmleval.ResultMinor.ContentRegEx=REGEX_URI\r\n" + 
				"## Result.ResultMessage\r\n" + 
				"ecard.xmleval.ResultMessage.XMLTag=ResultMessage\r\n" + 
				"ecard.xmleval.ResultMessage.MinOccurrence=0\r\n" + 
				"ecard.xmleval.ResultMessage.MaxOccurrence=1\r\n" + 
				"\r\n" + 
				"## OutputAPDU\r\n" + 
				"ecard.xmleval.OutputAPDU.XMLTag=OutputAPDU\r\n" + 
				"ecard.xmleval.OutputAPDU.ForceFailOnError=true\r\n" + 
				"#<text>There is a single element \"OutputAPDU\" returned. It contains a valid secure messaging response APDU and the first byte of EF.CardAccess and the status word '90 00'.</text>\r\n" + 
				"ecard.xmleval.OutputAPDU.MinOccurrence=" + min + "\r\n" + 
				"ecard.xmleval.OutputAPDU.MaxOccurrence=" + max + "\r\n";

		for(int i=0 ; i < max ; i++) {
			props += "ecard.xmleval.OutputAPDU." + i + ".ContentRegEx=^([0-9A-Fa-f]{2})+990290008[Ee]08[0-9A-Fa-f]{16}9000$\r\n";
		}		
		
		return props;
	}
	
	private static String generateMessage(int apdu) {
		String msg = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Header><paos:PAOS xmlns:paos=\"urn:liberty:paos:2006-08\" actor=\"http://schemas.xmlsoap.org/soap/actor/next\" mustUnderstand=\"1\"><paos:Version>urn:liberty:2006-08</paos:Version><paos:Version>urn:liberty:2003-08</paos:Version><paos:EndpointReference><paos:Address>http://www.projectliberty.org/2006/01/role/paos</paos:Address><paos:MetaData xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"paos:MetaDataType\"><paos:ServiceType>http://www.bsi.bund.de/ecard/api/1.0/PAOS/GetNextCommand</paos:ServiceType><paos:Options xsi:type=\"paos:OptionsType\"/></paos:MetaData></paos:EndpointReference></paos:PAOS><wsa:MessageID xmlns:wsa=\"http://www.w3.org/2005/03/addressing\">urn:uuid84A5FB8D5C0FBFB76649CCC2AFC7A224</wsa:MessageID><wsa:ReplyTo xmlns:wsa=\"http://www.w3.org/2005/03/addressing\"><wsa:Address xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"wsa:AttributedURIType\">http://www.projectliberty.org/2006/02/role/paos</wsa:Address></wsa:ReplyTo><wsa:RelatesTo xmlns:wsa=\"http://www.w3.org/2005/03/addressing\">urn:uuid:6F0C0C86887D46C0A6DD95E969364A42</wsa:RelatesTo></soap:Header><soap:Body><iso:TransmitResponse xmlns:iso=\"urn:iso:std:iso-iec:24727:tech:schema\" RequestID=\"\"><dss:Result xmlns:dss=\"urn:oasis:names:tc:dss:1.0:core:schema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"dss:\"><dss:ResultMajor>http://www.bsi.bund.de/ecard/api/1.1/resultmajor#error</dss:ResultMajor></dss:Result>";
		for(int i=0 ; i < apdu ; i++) {
			msg += "<iso:OutputAPDU>87110128DE0CCAE12EA4C1EBE302F66C732CEE990290008E084852D8654E1CC7CA9000</iso:OutputAPDU>";
		}
		msg += "</iso:TransmitResponse></soap:Body></soap:Envelope>";
		return msg;
	}
	
}
