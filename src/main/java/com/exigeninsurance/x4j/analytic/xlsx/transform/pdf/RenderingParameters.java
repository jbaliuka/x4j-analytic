/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class RenderingParameters {

	private final Map<String, Object> params;

	public RenderingParameters(RenderingParameter ... params) {
		this.params = new HashMap<String, Object>();
		for (RenderingParameter param : params) {
			this.params.put(param.getName(), param.getValue());
		}
	}

	public RenderingParameters(String name, Object value) {
		this(new RenderingParameter(name, value));
	}

	public RenderingParameters(Set<RenderingParameter> params) {
		this(params.toArray(new RenderingParameter[0]));
	}

	public Object get(String name) {
		return params.get(name);
	}

	public static RenderingParameters empty() {
		return new RenderingParameters();
	}

	public boolean has(String name) {
		return params.containsKey(name);
	}
}
