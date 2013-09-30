/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.xlsx.transform;

import java.util.Map;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.jexl2.JexlContext;


public class CaseInsensitiveContext implements JexlContext {
	
	private final CaseInsensitiveMap map = new CaseInsensitiveMap();

	public void setVars(Map<String, Object> vars) {
		map.putAll(vars);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getVars() {
		return map;
	}

	public Object get(String name) {
		return map.get(name);
	}

	public void set(String name, Object value) {
		map.put(name, value);
		
	}

	public boolean has(String name) {
		return map.containsKey(name);
	}
}
