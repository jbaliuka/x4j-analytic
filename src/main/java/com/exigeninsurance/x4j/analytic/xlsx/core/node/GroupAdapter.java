/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.node;

import java.sql.ResultSet;
import java.util.*;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.lang.ObjectUtils;

import com.exigeninsurance.x4j.analytic.api.Cursor;
import com.exigeninsurance.x4j.analytic.util.CursorMetadata;
import com.exigeninsurance.x4j.analytic.util.ResultSetWrapper;
import com.exigeninsurance.x4j.analytic.xlsx.core.groups.Group;
import com.exigeninsurance.x4j.analytic.xlsx.core.groups.GroupState;
import com.exigeninsurance.x4j.analytic.xlsx.core.groups.GroupTransitionResult;


public class GroupAdapter implements ResultSetAdapter {

	public static final String NO_GROUP = "notAGroup";
	private static final Map<String, Object> RESULTSET_END = new HashMap<String, Object>();
	private final Cursor cursor;
	
	@SuppressWarnings("unchecked")
	private final Map<String, Object> currentRow = new CaseInsensitiveMap();
	@SuppressWarnings("unchecked")
	private Map<String, Object> nextRow = new CaseInsensitiveMap();
 	private final LinkedHashMap<String, Group> groups = new LinkedHashMap<String, Group>();
 	private final Deque<String> stack = new ArrayDeque<String>();

	public GroupAdapter(ResultSet rs) {
        this(ResultSetWrapper.wrap(rs));
    }
	
	public GroupAdapter(Cursor cursor) {
		this.cursor = cursor;
		assert(cursor != null);
		fetchNextRow();
	}
	
	public boolean next(String group) {
		Group currentGroup = getCurrentGroup(group);
		
		GroupTransitionResult groupNext = currentGroup.next();
		if (groupNext.isSkipping()) {
			return groupNext.isNextAfterSkip();
		}
		
		boolean next = !dataEnd();
		
		if (!next) {
			return false;
		}
		
		if (!handleGroups(next, group, currentGroup)) {
			currentGroup.transitionOnNext(next);
			return false;
		}
		
		moveNextForward(currentGroup);
		
		handleLast(group);
		
		return next;
	}
	
	private void moveNextForward(Group currentGroup) {
		getCurrentRow(currentRow);
		fetchNextRowAndUpdateState(currentGroup);
	}
	
	private boolean handleGroups(boolean next, String group, Group currentGroup) {
		String overOnGroup = isRelevantGroupOverOnNext(group);
		boolean isGroupOver = overOnGroup != null;
		return processGroupOver(isGroupOver, next, overOnGroup, currentGroup);
	}
	
	private void handleLast(String group) {
		String overOnGroup = isRelevantGroupOverOnNext(group);
		boolean isGroupOver = overOnGroup != null;
		if (isGroupOver) {
			handleLastGroupItem(group);
		}
	}
	
	public boolean processGroupOver(boolean groupOver, boolean next, String overOnGroup, Group currentGroup) {
		if (currentGroup.getState().processesGroups() && groupOver) {
			handleGroupOver(overOnGroup);
			return false;
		}
		return next;
	}
	
	private void handleGroupOver(String overOnGroup) {
		boolean groupFound = false;
		for (String group : groups.keySet()) {
			if (overOnGroup.equals(group) || groupFound) {
				Group currentGroup = groups.get(group);
				currentGroup.transitionOnGroupOver();
				groupFound = true;
			}
		}
	}
	
	private void handleLastGroupItem(String overOnGroup) {
		boolean groupFound = false;
		for (String group : groups.keySet()) {
			if (ObjectUtils.equals(overOnGroup, group) || groupFound) {
				Group currentGroup = groups.get(group);
				currentGroup.setLast(true);
				groupFound = true;
			}
		}
	}
	
	private String isRelevantGroupOverOnNext(String currentGroup) {
		for (String group : groups.keySet()) {
			if (isGroupOverOnNext(group)) {
				return group;
			}
			if (ObjectUtils.equals(currentGroup, group)) {
				return null;
			}
		}
		return null;
	}
	
	private Group getCurrentGroup(String group) {
		if (group == null) {
			return groups.get(NO_GROUP);
		}
		else {
			return groups.get(group);
		}
	}
	
	private void fetchNextRowAndUpdateState(Group group) {
		boolean next = next();
		group.transitionOnNext(next);
		if (next) {
			getCurrentRow(nextRow);
		}
		else {
			nextRow = RESULTSET_END;
		}
	}
	
	private void fetchNextRow() {
		boolean next = next();
		if (next) {
			getCurrentRow(nextRow);
		}
		else {
			nextRow = RESULTSET_END;
		}
	}
	
	private boolean isGroupOverOnNext(String group) {
		Object currentValue = currentRow.get(group);
		Object nextValue = nextRow.get(group);
		return !ObjectUtils.equals(nextValue, currentValue) && !currentRow.isEmpty();
	}
	
	public boolean next() {
		return cursor.next();
	}
	
	private boolean dataEnd() {
		return nextRow.isEmpty();
	}

	private void getCurrentRow(Map<String, Object> row) {
		CursorMetadata meta = cursor.getMetadata();
		int columnCount = meta.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			row.put(meta.getColumnName(i + 1), cursor.getObject(i + 1));
		}
	}
	
	public void getRow(Map<String, Object> row) {
		row.putAll(currentRow);
	}
	
	public void addGroup(String group, GroupState state) {
		groups.put(group, new Group(state));
	}


	public void close(String column) {
		if (column.equals(NO_GROUP)) {
			cursor.close();
		}
	}

	public boolean last() {
		return last(stack.peek());
	}

	public boolean first() {
		return first(stack.peek());
	}
	
	public boolean last(String group) {
		return getCurrentGroup(group).isLast();
	}
	
	public boolean first(String group) {
		return getCurrentGroup(group).isFirst();
	}


	public void push(String group) {
		stack.push(group);
	}

	public void pop() {
		stack.pop();
	}
}
