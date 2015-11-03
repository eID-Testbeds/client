package com.secunet.ipsmall.eval;

import com.secunet.ipsmall.IPSmallManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPathConstants;

import com.secunet.testbedutils.cvc.cvcertificate.DataBuffer;

import org.w3c.dom.Node;

import com.secunet.ipsmall.ecard.MessageHandler;
import com.secunet.ipsmall.eval.EvaluateResult.ResultType;
import com.secunet.ipsmall.eval.EvaluationConfig.DefaultRegEx;
import com.secunet.ipsmall.log.IModuleLogger.ConformityResult;
import com.secunet.ipsmall.log.IModuleLogger.LogLevel;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.test.ITestData;
import com.secunet.ipsmall.tobuilder.ics.TR031242ICS;

/**
 * Uses the configuration to create a evaluation setup to evaluate messages.
 * 
 * @author olischlaeger.dennis
 */
public class Evaluator {
    
    public enum ContentPlaceholder {
        FILE_CARDACCESS,
        FILE_CARDSECURITY,
        ICS_CLIENT_NAME,
        ICS_CLIENT_VERSIONMAJOR,
        ICS_CLIENT_VERSIONMINOR,
        ICS_CLIENT_VERSIONSUBMINOR
    }
    
    private static final String PREFIX = "ecard.xmleval.";
    private static final String POSTFIX_XMLTAG = ".XMLTag";
    private static final String POSTFIX_QUERY = ".Query";
    private static final String POSTFIX_Children = ".Children";
    private static final String POSTFIX_MinOccurrence = ".MinOccurrence";
    private static final String POSTFIX_MaxOccurrence = ".MaxOccurrence";
    private static final String POSTFIX_Warning = ".Warning";
    private static final String POSTFIX_ForceFailOnError = ".ForceFailOnError";
    private static final String POSTFIX_ContentRegEx = ".ContentRegEx";
    private static final String POSTFIX_TypeRegEx = ".TypeRegEx";
    private static final String MIDFIX_Type = ".Type.";
    private static final String POSTFIX_KEY = ".key";
    private static final String POSTFIX_VALUE = ".value";
    private static final int prefixLength = PREFIX.length();
    private static final int postfixLength = POSTFIX_XMLTAG.length();
    
    /**
     *
     * @param properties
     *            e.g. the StartPAOS properties object
     * @param initalNode
     *            e.g. "StartPAOS" the root node of the xml to look at
     * @return
     * @throws NumberFormatException
     *             if config could not be
     */
    public static Map<String, EvaluationConfig> createEvaluationSetup(final ITestData config, final Properties properties, final String initalNode)
            throws NumberFormatException {
        
        // create into this map all needed config entries
        Map<String, EvaluationConfig> configs = new HashMap<>();
        
        Set<String> nodes = new HashSet<>();
        for (Object key : properties.keySet()) {
            if (key instanceof String && ((String) key).endsWith(".XMLTag")) {
                String complKey = (String) key;
                nodes.add(complKey.substring(prefixLength, (complKey.length() - postfixLength)));
            }
        }

        // get expected name and version from ics
        TR031242ICS ics = IPSmallManager.getInstance().getIcs();
        String icsClientName = null;
        String icsClientVersionMajor = null;
        String icsClientVersionMinor = null;
        String icsClientVersionSubminor = null;
        if (ics != null) {
            TR031242ICS.SoftwareVersion swVersion = ics.getSoftwareVersion();
            if (swVersion != null) {
                icsClientName = swVersion.getName();
                if (swVersion.getVersionMajor() != null && swVersion.getVersionMinor() != null && swVersion.getVersionSubminor() != null) {
                    if (!swVersion.getVersionMajor().isEmpty()) {
                        icsClientVersionMajor = swVersion.getVersionMajor();
                        if (!swVersion.getVersionMinor().isEmpty()) {
                            icsClientVersionMinor = swVersion.getVersionMinor();
                            if (!swVersion.getVersionSubminor().isEmpty()) {
                                icsClientVersionSubminor = swVersion.getVersionSubminor();
                            }
                        }
                    }
                }
            }
        }
        
        for (String node : nodes) {
            String xmltag = properties.getProperty(PREFIX + node + POSTFIX_XMLTAG, node);
            String query = properties.getProperty(PREFIX + node + POSTFIX_QUERY, null);
            String children = properties.getProperty(PREFIX + node + POSTFIX_Children, "");
            int minOccu = Integer.parseInt(properties.getProperty(PREFIX + node + POSTFIX_MinOccurrence, null));
            int maxOccu = Integer.parseInt(properties.getProperty(PREFIX + node + POSTFIX_MaxOccurrence, null));
            boolean warning = Boolean.parseBoolean(properties.getProperty(PREFIX + node + POSTFIX_Warning, "false"));
            boolean forceFailOnError = Boolean.parseBoolean(properties.getProperty(PREFIX + node + POSTFIX_ForceFailOnError, "false"));
            String contentRegEx = properties.getProperty(PREFIX + node + POSTFIX_ContentRegEx, null);
            String typeRegEx = properties.getProperty(PREFIX + node + POSTFIX_TypeRegEx, null);
            
            Map<Integer, String> specificEvalContentRegex = new HashMap<Integer, String>();
            
            Set<String> bla = properties.stringPropertyNames();
            String[] blubb = bla.toArray(new String[bla.size()]);
            Pattern patternContentRegEx = Pattern.compile("^" + PREFIX + node + ".(\\d+)" + POSTFIX_ContentRegEx + "$");
            for (int i = 0; i < blubb.length; i++) {
                Matcher matcher = patternContentRegEx.matcher(blubb[i]);
                if (matcher.matches()) {
                    MatchResult result = matcher.toMatchResult();
                    specificEvalContentRegex.put(Integer.parseInt(result.group(1)), properties.getProperty(blubb[i], null));
                }
            }
            
            // handle type mapping
            Map<String, String> types = new HashMap<String, String>();
            int i = 0;
            while (properties.containsKey(PREFIX + node + MIDFIX_Type + i + POSTFIX_KEY)
                    && properties.containsKey(PREFIX + node + MIDFIX_Type + i + POSTFIX_VALUE)) {
                types.put(properties.getProperty(PREFIX + node + MIDFIX_Type + i + POSTFIX_KEY),
                        properties.getProperty(PREFIX + node + MIDFIX_Type + i + POSTFIX_VALUE));
                i++;
            }
            
            // handle content reg ex
            if (ContentPlaceholder.FILE_CARDACCESS.name().equals(contentRegEx)) {
                try {
                    byte[] rawFile = IPSmallManager.getInstance().getCardSimulator().getCurrentEFCardAccess();
                    if (rawFile == null) {
                        Logger.XMLEval.logState("Could not load FILE_CARDACCESS from simulator, loading from testcase configuration ...", LogLevel.Debug);
                        rawFile = config.getEFCardAccessFile();
                    }
                    DataBuffer file = new DataBuffer(rawFile);
                    contentRegEx = "^" + toLowerOrUpperCase(file.asHexBinary());
                } catch (Exception ignore) {
                    Logger.XMLEval.logConformity(ConformityResult.failed, "Could not load FILE_CARDACCESS, so the evaluation will not check the content of it", LogLevel.Warn);
                    contentRegEx = null;
                }
            } else if (ContentPlaceholder.FILE_CARDSECURITY.name().equals(contentRegEx)) {
                try {
                    byte[] rawFile = IPSmallManager.getInstance().getCardSimulator().getCurrentEFCardSecurity();
                    if (rawFile == null) {
                        Logger.XMLEval.logState("Could not load FILE_CARDSECURITY from simulator, loading from testcase configuration ...", LogLevel.Debug);
                        rawFile = config.getEFCardSecurityFile();
                    }
                    DataBuffer file = new DataBuffer(rawFile);
                    contentRegEx = "^" + toLowerOrUpperCase(file.asHexBinary());
                } catch (Exception ignore) {
                    Logger.XMLEval.logConformity(ConformityResult.failed, "Could not load FILE_CARDSECURITY, so the evaluation will not check the content of it", LogLevel.Warn);
                    contentRegEx = null;
                }
            } else if (ContentPlaceholder.ICS_CLIENT_NAME.name().equals(contentRegEx)) {
                if (icsClientName == null) {
                    Logger.XMLEval.logConformity(ConformityResult.failed, "Could not load ICS_CLIENT_NAME, so the evaluation will not check the content of it", LogLevel.Warn);
                    contentRegEx = null;
                }
                else {
                    contentRegEx = "^" + icsClientName + "$";
                }
            } else if (ContentPlaceholder.ICS_CLIENT_VERSIONMAJOR.name().equals(contentRegEx)) {
                if (icsClientVersionMajor == null) {
                    Logger.XMLEval.logConformity(ConformityResult.failed, "Could not load ICS_CLIENT_VERSIONMAJOR, so the evaluation will not check the content of it", LogLevel.Error);
                    contentRegEx = null;
                }
                else {
                    contentRegEx = "^" + icsClientVersionMajor + "$";
                }
            } else if (ContentPlaceholder.ICS_CLIENT_VERSIONMINOR.name().equals(contentRegEx)) {
                if (icsClientVersionMinor == null) {
                    Logger.XMLEval.logConformity(ConformityResult.failed, "Could not load ICS_CLIENT_VERSIONMINOR, so the evaluation will not check the content of it", LogLevel.Error);
                    contentRegEx = null;
                }
                else {
                    contentRegEx = "^" + icsClientVersionMinor + "$";
                }
            } else if (ContentPlaceholder.ICS_CLIENT_VERSIONSUBMINOR.name().equals(contentRegEx)) {
                if (icsClientVersionSubminor == null) {
                    Logger.XMLEval.logConformity(ConformityResult.failed, "Could not load ICS_CLIENT_VERSIONSUBMINOR, so the evaluation will not check the content of it", LogLevel.Info);
                    contentRegEx = null;
                }
                else {
                    contentRegEx = "^" + icsClientVersionSubminor + "$";
                }
            } else {
                if (contentRegEx != null)
                    try {
                        contentRegEx = DefaultRegEx.valueOf(contentRegEx).getRegEx();
                    } catch (Exception ignore) {
                        // could not identify the given configuration value as a default REGEX. so we will handle it as an actual regular expression to use the
                        // match it in the evaluation process
                    }
            }
            
            // handle children
            List<String> childrenOrder = new ArrayList<>();
            StringTokenizer tokenizer = new StringTokenizer(children, ";");
            while (tokenizer.hasMoreTokens()) {
                childrenOrder.add(tokenizer.nextToken());
            }
            configs.put(xmltag, new EvaluationConfig(xmltag, query, childrenOrder.toArray(new String[childrenOrder.size()]), minOccu, maxOccu, warning,
                    forceFailOnError, contentRegEx, specificEvalContentRegex, typeRegEx, types));
        }
        
        return configs;
    }
    
    // -----
    // Factories ---------------------------------------------------------------
    // -----
    
    /**
     * ResultType.OK
     */
    public static EvaluateResult createPositiveResult() {
        return new EvaluateResult(ResultType.OK, false, null, null, null);
    }
    
    /**
     * Checks if nodes present, nodes/childnodes are in order and min/max occurrences checks out. Also checks if types/attributes are present. TODO: Logging
     * 
     * @param startNode
     *            the name of the node
     * @param cfgs
     *            configuration of the evaluation per node
     * @param handler
     *            the access to the actual message
     * @return Returns OK if no error occurs. if errors marked as warnings occur, it will return them in the field of the EvaluateResult (but is still OK)
     */
    public static EvaluateResult createOrderedOccurenceResult(String startNode, Map<String, EvaluationConfig> cfgs, MessageHandler handler) {
        try {
            if (cfgs.get(startNode) == null)
                throw new RuntimeException("could not find start node: " + startNode);
            
            Node node = (Node) handler.read(cfgs.get(startNode).getXPathQuery(), XPathConstants.NODE);
            if (node == null)
                return new EvaluateResult(ResultType.GeneralError, cfgs.get(startNode).isForceFailOnError(), "XML Node " + startNode + " not found", "none",
                        "1");
            
            return createOrderedOccurenceResult(node, null, cfgs, handler);
        } catch (Exception ex) {
            Logger.XMLEval.logState("Error creating EvaluateResult: " + ex.getMessage(), LogLevel.Error);
            return new EvaluateResult(ResultType.GeneralError, cfgs.get(startNode).isForceFailOnError(),
                    ((ex.getMessage() == null || ex.getMessage().length() == 0) && ex.getCause() != null) ? ex.getCause().getMessage() : ex.getMessage(), ex
                            .getClass().getSimpleName(), "");
        }
    }
    
    /** Checks order, occurrence, type of nodes and childs */
    private static EvaluateResult createOrderedOccurenceResult(Node node, Map<String, Integer> nodeCounter, Map<String, EvaluationConfig> cfgs,
            MessageHandler handler) {
        
        EvaluateResult result = createPositiveResult();
        if (node.getNodeType() != 1) {
            Logger.XMLEval.logState("EvaluateResult: Wrong Type for node " + node.getNodeName(), LogLevel.Debug);
            return null;
        }
        
        String startNode = node.getNodeName();
        // cut namespace
        if (startNode.contains(":"))
            startNode = startNode.substring(startNode.indexOf(':') + 1);
        
        int currentNodeIndex = 0;
        
        // update node counter
        if (nodeCounter != null) {
            int nodeCount = 1;
            if (nodeCounter.get(startNode) != null) {
                currentNodeIndex = nodeCounter.get(startNode);
                nodeCount += currentNodeIndex;
            }
            nodeCounter.put(startNode, nodeCount);
        }
        
        // check content global
        String regex = cfgs.get(startNode).getEvalContentRegex();
        String content = node.getNodeValue();
        if (content == null && node.getTextContent() != null)
            content = node.getTextContent();
        
        if (regex != null && !Pattern.matches(regex, (content == null) ? "" : content)) {
            EvaluateResult error = new EvaluateResult(ResultType.ContentError, cfgs.get(startNode).isForceFailOnError(), "Content of " + node.getNodeName() + " does not match regex",
                    content, regex);
            if (cfgs.get(startNode).isWarning()) {
                result.addWarning(error);
            } else
                return error;
        }
        
        // check content specific
        String specificRegex = cfgs.get(startNode).getSpecificEvelContentRegex(currentNodeIndex);
        if (specificRegex != null && !Pattern.matches(specificRegex, (content == null) ? "" : content)) {
            EvaluateResult error = new EvaluateResult(ResultType.ContentError, cfgs.get(startNode).isForceFailOnError(), "Content of " + node.getNodeName() + " does not match regex",
                    content, specificRegex);
            if (cfgs.get(startNode).isWarning()) {
                result.addWarning(error);
            } else
                return error;
        }
        
        // check type
        if (cfgs.get(startNode).getTypesReference() != null && cfgs.get(startNode).getTypesReference().size() > 0) {
            for (String key : cfgs.get(startNode).getTypesReference().keySet()) {
                if (node.getAttributes().getNamedItem(key) == null) {
                    return new EvaluateResult(ResultType.TypeError, cfgs.get(startNode).isForceFailOnError(), "Did not found attribute " + key, "none", cfgs
                            .get(startNode).getTypesReference().get(key));
                } else if (!node.getAttributes().getNamedItem(key).getNodeValue().equals(cfgs.get(startNode).getTypesReference().get(key))) {
                    return new EvaluateResult(ResultType.TypeError, cfgs.get(startNode).isForceFailOnError(), "Did not found attribute " + key
                            + " with matching value", node.getAttributes().getNamedItem(key).getNodeValue(), cfgs.get(startNode).getTypesReference().get(key));
                }
            }
        }
        
        // check if node should have children, but no xml node has no children at all
        if (node.getChildNodes().getLength() == 0 && cfgs.containsKey(startNode) && cfgs.get(startNode).hasChildren()) {
            for (String childName : cfgs.get(startNode).getChildren()) {
                if (cfgs.containsKey(childName) && (cfgs.get(childName).getMinOccurence() > 0)) {
                    EvaluateResult error = new EvaluateResult(ResultType.OccurenceError, cfgs.get(startNode).isForceFailOnError(), "XML Node " + childName
                            + " not found", "0", "[" + cfgs.get(childName).getMinOccurence() + "," + cfgs.get(childName).getMaxOccurence() + "]");
                    if (cfgs.get(childName).isWarning()) {
                        result.addWarning(error);
                    } else {
                        return error;
                    }
                }
            }
        }
        
        // check for occurrence and order of child nodes, check recursive the child
        String[] expectedChildNames = cfgs.get(startNode).getChildren();
        int expactedOrderIndex = 0, index = 0, occurence = 0;
        String lastChildNode = null;
        List<String> foundChilds = new ArrayList<>();
        
        // if no children, check if really no children
        if (expectedChildNames == null || (expectedChildNames != null && expectedChildNames.length == 0)) {
            while (index < node.getChildNodes().getLength()) {
                Node child = node.getChildNodes().item(index);
                if (child.getNodeType() != 1) {
                    index++;
                    continue;
                }
                
                EvaluateResult error = new EvaluateResult(ResultType.OccurenceError, cfgs.get(startNode).isForceFailOnError(), "Found unknown node: "
                        + child.getNodeName(), "1", "0");
                error.addWarnings(result.getWarningsReference());
                return error;
            }
            
            return result;
        }
        
        Map<String, Integer> childNodeCounter = new HashMap<String, Integer>();
        
        // iterate over children
        while (index < node.getChildNodes().getLength()) {
            Node child = node.getChildNodes().item(index);
            // skip text nodes etcpp
            if (child.getNodeType() != 1) {
                index++;
                continue;
            }
            
            String childName = child.getNodeName();
            // cut namespace
            if (childName.contains(":"))
                childName = childName.substring(childName.indexOf(':') + 1);
            foundChilds.add(childName);
            
            // check if child is expected or unknown
            if (!Arrays.asList(expectedChildNames).contains(childName)) {
                EvaluateResult error = new EvaluateResult(ResultType.OccurenceError, cfgs.get(childName).isForceFailOnError(), "XML Node " + childName
                        + " unkown", "1", "0");
                if (cfgs.get(childName) != null && cfgs.get(childName).isWarning()) {
                    result.addWarning(error);
                } else {
                    return error;
                }
            }
            
            // check if new node or 'same' (some nodes can occur 0..n times)
            if (childName.equals(lastChildNode)) {
                occurence++;
                if (occurence > cfgs.get(childName).getMaxOccurence())
                    return new EvaluateResult(ResultType.OccurenceError, cfgs.get(childName).isForceFailOnError(), "XML Node " + childName
                            + " occured too often", "" + occurence, "[" + cfgs.get(childName).getMinOccurence() + "," + cfgs.get(childName).getMaxOccurence()
                            + "]");
                // don't increase expactedOrderIndex!!
                
                // if new nodename reset occurrence and check if node was excepted
            } else {
                lastChildNode = childName;
                occurence = 1;
                boolean increaseCounter = true;
                // check if more children than expected
                if (expactedOrderIndex > expectedChildNames.length) {
                    EvaluateResult error = new EvaluateResult(ResultType.OccurenceError, cfgs.get(childName).isForceFailOnError(), "Found unknown node: "
                            + childName, "1", "0");
                    error.addWarnings(result.getWarningsReference());
                    return error;
                }
                
                if (expactedOrderIndex >= expectedChildNames.length)
                    return new EvaluateResult(ResultType.OccurenceError, cfgs.get(childName).isForceFailOnError(), "XML Node " + childName + " unknown", "1",
                            "0");
                
                // check for matching node in sequence
                while (!childName.equals(expectedChildNames[expactedOrderIndex])) {
                    if (cfgs.get(childName) != null && cfgs.get(childName).isWarning() && cfgs.get(childName).getMinOccurence() > 0) {
                        increaseCounter = false;
                        result.addWarning(new EvaluateResult(ResultType.OccurenceError, cfgs.get(childName).isForceFailOnError(), "XML Node " + childName
                                + " unkown", "1", "0"));
                        break;
                    }
                    
                    if(cfgs.containsKey(expectedChildNames[expactedOrderIndex])){
                    	if (cfgs.get(expectedChildNames[expactedOrderIndex]).getMinOccurence() > 0) {
                            EvaluateResult error = new EvaluateResult(ResultType.OccurenceError, cfgs.get(expectedChildNames[expactedOrderIndex])
                                    .isForceFailOnError(), "XML Node " + expectedChildNames[expactedOrderIndex] + " not found", "0", ""
                                    + cfgs.get(expectedChildNames[expactedOrderIndex]).getMinOccurence());
                            if (cfgs.get(expectedChildNames[expactedOrderIndex]).isWarning()) {
                                result.addWarning(error);
                            } else
                                return error;
                        }
                    }
                    
                    expactedOrderIndex++;
                    // check range
                    if (expactedOrderIndex >= expectedChildNames.length) {
                        EvaluateResult error = new EvaluateResult(ResultType.OccurenceError, cfgs.get(childName).isForceFailOnError(), "XML Node " + childName
                                + " unkown (order out of range)", "1", "0");
                        if (cfgs.get(childName).isWarning()) {
                            result.addWarning(error);
                        } else {
                            return error;
                        }
                    }
                    
                }
                
                if (increaseCounter)
                    expactedOrderIndex++;
            }
            
            // check children
            if (cfgs.containsKey(childName)) {
                EvaluateResult childResult = null;
                try {
                    childResult = createOrderedOccurenceResult(child, childNodeCounter, cfgs, handler);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    childResult = new EvaluateResult(ResultType.GeneralError, cfgs.get(childName).isForceFailOnError(), childName + " encountered an error: "
                            + ex.getClass().getSimpleName() + "::" + ex.getMessage(), "1", "0");
                }
                if (childResult.isError()) {
                    return childResult;
                } else if (!childResult.getWarningsReference().isEmpty()) {
                    result.addWarnings(childResult.getWarningsReference());
                }
            }
            
            // next node
            index++;
        }
        
        StringBuilder nonOptionalNodes = new StringBuilder();
        StringBuilder missChildsStrBldr = new StringBuilder();
        boolean missingChild = false;
        for (int i = 0; i < expectedChildNames.length; i++) {
            if (cfgs.get(expectedChildNames[i]) == null) {
                EvaluateResult error = new EvaluateResult(ResultType.OccurenceError, cfgs.get(startNode).isForceFailOnError(), "Found unkown child node: "
                        + expectedChildNames[i], "1", "0");
                error.addWarnings(result.getWarningsReference());
                return error;
            }
            
            if (cfgs.get(expectedChildNames[i]).getMinOccurence() > 0) {
                nonOptionalNodes.append(expectedChildNames[i]).append(" ");
                if (!foundChilds.contains(expectedChildNames[i])) {
                    if (cfgs.containsKey(expectedChildNames[i]) && cfgs.get(expectedChildNames[i]).isWarning()) {
                        result.addWarning(new EvaluateResult(ResultType.OccurenceError, cfgs.get(startNode).isForceFailOnError(), "XML Node not found: "
                                + expectedChildNames[i], "0", "" + cfgs.get(expectedChildNames[i]).getMinOccurence()));
                    } else {
                        missChildsStrBldr.append(" ").append(expectedChildNames[i]);
                        missingChild = true;
                    }
                }
            }
        }
        
        if (missingChild) {
            EvaluateResult error = new EvaluateResult(ResultType.OccurenceError, cfgs.get(startNode).isForceFailOnError(),
                    "Not all non optional child nodes of " + startNode + "; missing '" + missChildsStrBldr.toString() + "' expected:('"
                            + nonOptionalNodes.toString() + "')", "0", "1");
            error.addWarnings(result.getWarningsReference());
            return error;
        }
        
        return result;
    }
    
    
    /**
     * Replaces characters (a-z or A-Z) with a regex, witch allows lower or upper case encoding.
     * @param str Source string.
     * @return New string with regex for lower and upper case encoding.
     */
    private static String toLowerOrUpperCase(String str)
    {
        String result = "";
        String[] chars = str.split("");
        for (String c : chars) {
            if (c.matches("[a-zA-Z]")) {
                result += "[" + c.toLowerCase() + c.toUpperCase() + "]";
            } else
                result += c;
        }
        
        return result;
    }
}
