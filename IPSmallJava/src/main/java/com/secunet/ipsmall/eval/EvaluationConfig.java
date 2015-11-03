package com.secunet.ipsmall.eval;

import java.util.Map;

public class EvaluationConfig {
    
    public enum DefaultRegEx {
        
        REGEX_NUMBER(
                "^-?[0123456789]+$"),
        REGEX_NUMBER_NONEGATIVE(
                "^[0123456789]+$"),
        REGEX_HEX(
                "^[0-9A-Fa-f]+$"),
        REGEX_URI(
                "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
        
        private final String RegEx;
        
        private DefaultRegEx(final String theRegEx) {
            this.RegEx = theRegEx;
        }
        
        public String getRegEx() {
            return RegEx;
        }
        
    }
    
    /** Order of children is important */
    private final String[] children;
    private final String nodeName;
    private final String xPathQuery;
    private final int min;
    private final int max;
    private final boolean warning;
    private final boolean forceFailOnError;
    private final String evalContentRegex;
    private final Map<Integer, String> specificEvalContentRegex;
    private final String evalTypeRegex;
    private final Map<String, String> types;
    
    public EvaluationConfig(String nodeName, String xPathQuery, String[] children, int min, int max,
            String evalContentRegex, Map<Integer, String> specificEvalContentRegex, String evalTypeRegex, Map<String, String> type) {
        this(nodeName, xPathQuery, children, min, max, false, false, evalContentRegex, specificEvalContentRegex, evalTypeRegex, type);
    }
    
    public EvaluationConfig(String nodeName, String[] children, int min, int max, String evalContentRegex, Map<Integer, String> specificEvalContentRegex,
            String evalTypeRegex, Map<String, String> type) {
        this(nodeName, null, children, min, max, false, false, evalContentRegex, specificEvalContentRegex, evalTypeRegex, type);
    }
    
    /**
     * Configuration of a certain node to evaluate its occurrence and validation
     * of its child nodes.
     * 
     * @param nodeName
     *            the XML tag of the node
     * @param xPathQuery
     *            the query
     * @param children
     *            names of the expected children
     * @param min
     *            minimum occurrence of this node
     * @param max
     *            maximum occurrence of this node
     * @param warning
     *            is this a warning and will not trigger a testcase fail?
     * @param evalContentRegex
     * @param evalTypeRegex
     * @param types
     */
    public EvaluationConfig(String nodeName, String xPathQuery, String[] children, int min, int max, boolean warning, boolean forceFailOnError,
            String evalContentRegex, Map<Integer, String> specificEvalContentRegex, String evalTypeRegex, Map<String, String> types) {
        this.nodeName = nodeName;
        this.xPathQuery = xPathQuery;
        this.children = children;
        this.min = min;
        this.max = max;
        this.warning = warning;
        this.forceFailOnError = forceFailOnError;
        this.evalContentRegex = evalContentRegex;
        this.specificEvalContentRegex = specificEvalContentRegex;
        this.evalTypeRegex = evalTypeRegex;
        this.types = types;
    }
    
    public String[] getChildren() {
        return children;
    }
    
    public String getNodeName() {
        return nodeName;
    }
    
    public String getXPathQuery() {
        if (xPathQuery == null)
            return getNodeName();
        return xPathQuery;
    }
    
    public int getMinOccurence() {
        return min;
    }
    
    public int getMaxOccurence() {
        return max;
    }
    
    public boolean isWarning() {
        return warning;
    }
    
    public boolean isForceFailOnError() {
        return forceFailOnError;
    }
    
    public String getEvalContentRegex() {
        return evalContentRegex;
    }
    
    public String getSpecificEvelContentRegex(final int index) {
        if (specificEvalContentRegex == null)
            return null;
        else
            return specificEvalContentRegex.get(index);
    }
    
    public String getEvalTypeRegex() {
        return evalTypeRegex;
    }
    
    public Map<String, String> getTypesReference() {
        return types;
    }
    
    public boolean hasChildren() {
        return children != null && children.length > 0;
    }
    
    public String getChildName(final int index) {
        if (children == null)
            return null;
        else
            return getChildren()[index];
    }
    
    public static void addResultXMLContent(Map<String, EvaluationConfig> mapping) {
        mapping.put("Result", new EvaluationConfig("Result", new String[] { "ResultMajor", "ResultMinor",
                "ResultMessage" }, 1, 1, null, null, null, null));
        mapping.put("ResultMajor", new EvaluationConfig("ResultMajor", null, 1, 1, DefaultRegEx.REGEX_URI.getRegEx(), null,
                null, null));
        mapping.put("ResultMinor", new EvaluationConfig("ResultMinor", null, 0, 1, DefaultRegEx.REGEX_URI.getRegEx(), null,
                null, null));
        mapping.put("ResultMessage", new EvaluationConfig("ResultMessage", null, 0, 1, null, null, null, null));
    }
    
}
