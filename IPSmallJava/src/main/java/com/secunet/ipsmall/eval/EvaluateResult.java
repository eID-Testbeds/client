package com.secunet.ipsmall.eval;

import java.util.ArrayList;
import java.util.List;

/** Checks the XML structure of the messages. Expectation will be given via EvaluationConfig */
public class EvaluateResult {
    
    public enum ResultType {
        
        /** no error occurred, but check for warnings! */
        OK,
        /** */
        SyntaxError,
        /** validation of the content type (attribute of the node) fails */
        TypeError,
        /** validation of the content fails */
        ContentError,
        /** node missing, node too often or unexpected node given */
        OccurenceError,
        /** error of the evaluation logic or unforeseen error */
        GeneralError;
    }
    
    private ResultType type = ResultType.GeneralError;
    private boolean critical = false;
    private String description;
    private String messageValue;
    private String expactedValue;
    private List<EvaluateResult> warnings;
    
    public EvaluateResult(ResultType type, boolean critical, String description, String messageValue, String expactedValue) {
        this.type = type;
        this.critical = critical;
        this.description = description;
        this.messageValue = messageValue;
        this.expactedValue = expactedValue;
        warnings = new ArrayList<EvaluateResult>();
    }
    
    public ResultType getType() {
        return type;
    }
    
    public void setType(ResultType newType) {
        type = newType;
    }
    
    public boolean isError() {
        return type != ResultType.OK;
    }
    
    public boolean isCriticalError() {
        return isError() && critical;
    }
    
    public boolean isValid() {
        return type == ResultType.OK;
    }
    
    public String getMessageValue() {
        return messageValue;
    }
    
    public void setMessageValue(String messageValue) {
        this.messageValue = messageValue;
    }
    
    public String getExpectedValue() {
        return expactedValue;
    }
    
    public void setExpactedValue(String expactedValue) {
        this.expactedValue = expactedValue;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void addWarnings(final List<EvaluateResult> newWarnings) {
        warnings.addAll(newWarnings);
    }
    
    public void addWarning(final EvaluateResult warning) {
        warnings.add(warning);
    }
    
    /** Returns the reference to the list of all results */
    public List<EvaluateResult> getWarningsReference() {
        return warnings;
    }
    
    public String toString() {
        StringBuilder warningStr = new StringBuilder();
        for (EvaluateResult er : getWarningsReference()) {
            warningStr.append(er).append(", ");
        }
        return "EvaluteResult[ResultType=" + type + (isError() ? "(error)" : "(valid)") + ", Description="
                + getDescription() + ", Expected=" + getExpectedValue() + ", Value=" + getMessageValue()
                + ((getWarningsReference().size() != 0) ? ", Warnings=" + warningStr : "") + "]";
    }
    
}
