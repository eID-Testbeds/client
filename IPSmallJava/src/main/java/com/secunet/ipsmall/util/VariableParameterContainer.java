package com.secunet.ipsmall.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.secunet.ipsmall.util.VariableParser.VariableProvider;

/**
 * Container for variable parameters.
 */
public class VariableParameterContainer implements VariableProvider {
	
	private Map<String,String> container = null;
	
	/**
	 * Creates new variable parameter container object.
	 */
	public VariableParameterContainer() {
		this.container = new HashMap<String, String>();
	}
	
	/**
	 * Adds a new key - value pair to container.
	 * @param key The key.
	 * @param value The value.
	 */
	public void addKeyValue(String key, String value) {
		this.container.put(key, value);
	}

	@Override
	public String getValue(String varname) throws Exception {
		if (this.container.containsKey(varname))
			return this.container.get(varname);
		
		throw new Exception("Variable not defined \"" + varname + "\"");
	}

	@Override
	public boolean checkVarName(String substring) {
		List<String> list = new ArrayList<String>(this.container.keySet());
		if (list.contains(substring))
            return true;
		
		return false;
	}
}
