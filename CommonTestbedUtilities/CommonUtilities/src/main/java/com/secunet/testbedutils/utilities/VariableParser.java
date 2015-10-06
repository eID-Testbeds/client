package com.secunet.testbedutils.utilities;

public class VariableParser {
    
    public static interface VariableProvider {
        
        public String getValue(String varname) throws Exception;
        
        public boolean checkVarName(String substring);
        
    }
    
    public static class Data {
        int m_startPos = -1;
        int m_endPos = -1;
        String m_foundVar = null;
        
        public Data(int startPos, int endPos, String var) {
            m_startPos = startPos;
            m_endPos = endPos;
            m_foundVar = var;
        }
        
        public int getStartPos() {
            return m_startPos;
        }
        
        public int getEndPos() {
            return m_endPos;
        }
        
        public String getCompleteVar() {
            return m_foundVar;
        }
        
        public String getPlainVarName() {
            int start = m_foundVar.indexOf(c_VariableBracketOpen);
            int end = m_foundVar.indexOf(c_VariableBracketClose, start);
            return m_foundVar.substring(start + 1, end);
        }
    }
    
    private final static char c_VariableBracketOpen = '{';
    private final static char c_VariableBracketClose = '}';
    private final static char c_QuoteSign = '\\';
    
    VariableProvider m_varProvider = null;
    
    public VariableParser(VariableProvider provider) {
        m_varProvider = provider;
    }
    
    public VariableProvider getVariableProvider() {
        return m_varProvider;
    }
    
    /**
     * 
     * @param baseString
     * @return
     * @throws UnknownVariableNameException
     * @throws DataClassNotAvailableException
     * @throws ParsingException
     */
    public String format(String baseString) throws Exception {
        if (baseString == null) {
            return null;
        }
        String newString = replaceVars(baseString);
        newString = removeQuotation(newString);
        
        return newString;
    }
    
    protected String replaceVars(String baseString) throws Exception {
        StringBuilder buffer = new StringBuilder(baseString);
        int pos = 0;
        Data data = null;
        
        while ((data = findNextVariable(buffer, pos)) != null) {
            String replacement = getValue(data.getPlainVarName());
            
            String encoded = doEncoding(replacement);
            if (encoded == null) {
                break;
            }
            buffer = buffer.replace(data.getStartPos(), data.getEndPos(), encoded);
            
            pos = data.getStartPos() + encoded.length();
        }
        
        return buffer.toString();
    }
    
    protected String doEncoding(String in) {
        return in;
    }
    
    protected String getValue(String varname) throws Exception {
        return m_varProvider.getValue(varname);
        
    }
    
    protected Data findNextVariable(StringBuilder baseString, int fromPos) throws Exception {
        int newStartPos = baseString.indexOf("" + c_VariableBracketOpen, fromPos);
        while (newStartPos != -1) {
            if (newStartPos == 0)
                break;
            
            if (baseString.charAt(newStartPos - 1) != c_QuoteSign)
                break;
            
            newStartPos = baseString.indexOf("" + c_VariableBracketOpen, newStartPos + 1);
        }
        
        if (newStartPos == -1)
            return null;
        
        int newEndPos = baseString.indexOf("" + c_VariableBracketClose, newStartPos) + 1;
        
        String varName = baseString.substring(newStartPos, newEndPos);
        
        checkVarName(varName, newStartPos);
        
        return new Data(newStartPos, newEndPos, varName);
    }
    
    protected void checkVarName(String varName, int pos) throws Exception {
        int start = varName.indexOf(c_VariableBracketOpen);
        int end = varName.indexOf(c_VariableBracketClose, start);
        
        if (!m_varProvider.checkVarName(varName.substring(start + 1, end))) {
            throw new Exception("Unknown variable '" + varName + "' at row " + pos);
        }
        
    }
    
    protected String removeQuotation(String orgString) {
        String buffer = replaceAll(orgString, "" + c_QuoteSign + c_VariableBracketOpen, "" + c_VariableBracketOpen);
        buffer = replaceAll(buffer, "" + c_QuoteSign + c_VariableBracketClose, "" + c_VariableBracketClose);
        
        return buffer;
    }
    
    public static String replaceAll(String haystack, String needle, String replacement) {
        String buffer = haystack;
        int pos = 0;
        
        while ((pos = buffer.indexOf(needle)) != -1) {
            buffer = buffer.substring(0, pos) + replacement + buffer.substring(pos + needle.length(), buffer.length());
        }
        
        return buffer;
    }
}
