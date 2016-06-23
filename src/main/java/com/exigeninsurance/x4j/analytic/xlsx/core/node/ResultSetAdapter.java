/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.node;

import java.util.Map;

import com.exigeninsurance.x4j.analytic.xlsx.core.groups.GroupState;


public interface ResultSetAdapter {
	
	boolean next(String group);
	
	void getRow(Map<String, Object> destination);
	
	void close(String column);
	
	void addGroup(String group, GroupState initialState);
	
	boolean last();
	
	boolean first();
	
	void push(String group);
	
	void pop();
}
