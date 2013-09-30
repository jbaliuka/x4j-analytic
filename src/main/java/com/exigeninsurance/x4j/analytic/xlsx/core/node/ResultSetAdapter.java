/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.node;

import java.util.Map;

import com.exigeninsurance.x4j.analytic.xlsx.core.groups.GroupState;


public interface ResultSetAdapter {
	
	public boolean next(String group);
	
	public void getRow(Map<String, Object> destination);
	
	public void close(String column);
	
	public void addGroup(String group, GroupState initialState);
	
	public boolean last();
	
	public boolean first();
	
	public void push(String group);
	
	public void pop();
}
