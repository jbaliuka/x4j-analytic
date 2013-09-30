/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core;

import static com.exigeninsurance.x4j.analytic.util.MockResultSet.cols;
import static com.exigeninsurance.x4j.analytic.util.MockResultSet.data;
import static com.exigeninsurance.x4j.analytic.util.MockResultSet.row;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.exigeninsurance.x4j.analytic.util.MockResultSet;
import com.exigeninsurance.x4j.analytic.xlsx.core.groups.LockedState;
import com.exigeninsurance.x4j.analytic.xlsx.core.groups.NoGroupState;
import com.exigeninsurance.x4j.analytic.xlsx.core.groups.OpenState;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.GroupAdapter;

public class GroupManagerTest {

	@Test
	public void testHasNext() {
		ResultSet resultSet = MockResultSet.create(cols("A", "B", "C", "D"), data(row(
				1, 2, 3, 4),
				row(1, 2, 3, 4)));
		GroupAdapter adapter = new GroupAdapter(resultSet);
		adapter.addGroup(GroupAdapter.NO_GROUP, new OpenState());
		boolean hasNext = adapter.next(GroupAdapter.NO_GROUP);
		assertTrue(hasNext);
	}
	
	@Test
	public void testHasNextFalse() {
		ResultSet resultSet = MockResultSet.create(cols("A", "B", "C", "D"), data());
		GroupAdapter adapter = new GroupAdapter(resultSet);
		adapter.addGroup(GroupAdapter.NO_GROUP, new OpenState());
		boolean hasNext = adapter.next(GroupAdapter.NO_GROUP);
		assertFalse(hasNext);
	}
	
	@Test
	public void testBasicIteration()  {
		ResultSet resultSet = MockResultSet.create(cols("A", "B", "C", "D"), data(row(
				1, 2, 3, 4),
				row(5, 6, 7, 8)));
		GroupAdapter adapter = new GroupAdapter(resultSet);
		adapter.addGroup(GroupAdapter.NO_GROUP, new OpenState());
		adapter.next(GroupAdapter.NO_GROUP);
		Map<String, Object> next = new HashMap<String, Object>();
		adapter.getRow(next);
		assertEquals(1, next.get("a"));
		adapter.next(GroupAdapter.NO_GROUP);
		adapter.getRow(next);
		assertEquals(5, next.get("a"));
		
		assertFalse(adapter.next(GroupAdapter.NO_GROUP));
	}
	
	@Test
	public void testHasNextWithGroup() {
		ResultSet resultSet = MockResultSet.create(cols("A", "B", "C", "D"), data(row(
				1, 2, 3, 4),
				row(1, 2, 3, 4)));
		GroupAdapter adapter = new GroupAdapter(resultSet);
		adapter.addGroup("A", new OpenState());
		boolean hasNext = adapter.next("A");
		assertTrue(hasNext);
	}
	
	@Test
	public void testHasNextFalseWithGroup() {
		ResultSet resultSet = MockResultSet.create(cols("A", "B", "C", "D"), data());
		GroupAdapter adapter = new GroupAdapter(resultSet);
		adapter.addGroup("A", new OpenState());
		boolean hasNext = adapter.next("A");
		assertFalse(hasNext);
	}
	
	@Test
	public void testBasicIterationWithGroup() {
		ResultSet resultSet = MockResultSet.create(cols("A", "B", "C", "D"), data(row(
				1, 2, 3, 4),
				row(1, 2, 3, 4)));
		GroupAdapter adapter = new GroupAdapter(resultSet);
		adapter.addGroup("a", new OpenState());
		adapter.next("a");
		Map<String, Object> next = new HashMap<String, Object>();
		adapter.getRow(next);
		assertEquals(1, next.get("a"));
		adapter.next("a");
		adapter.getRow(next);
		assertEquals(1, next.get("a"));
		assertFalse(adapter.next("a"));
	}
	
	@Test
	public void testLockedState() {
		ResultSet resultSet = MockResultSet.create(cols("A", "B", "C", "D"), data(row(
				1, 2, 3, 4),
				row(1, 6, 7, 8)));
		GroupAdapter adapter = new GroupAdapter(resultSet);
		adapter.addGroup(GroupAdapter.NO_GROUP, new NoGroupState());
		adapter.addGroup("A", new LockedState(new OpenState()));
		adapter.next(GroupAdapter.NO_GROUP);
		
		assertTrue(!adapter.next("A"));
		Map<String, Object> row = new HashMap<String, Object>();
		adapter.getRow(row);
		assertEquals(1, row.get("a"));
		
		assertTrue(adapter.next("A"));
	}
	
	@Test
	public void testGroupOverOnNext() {
		ResultSet resultSet = MockResultSet.create(cols("A", "B", "C", "D"), data(row(
				1, 2, 3, 4),
				row(5, 6, 7, 8)));
		GroupAdapter adapter = new GroupAdapter(resultSet);
		adapter.addGroup("A", new OpenState());
		assertTrue(adapter.next("A"));
	}
	
	@Test
	public void testHandleGroupOverOnNext() {
		ResultSet resultSet = MockResultSet.create(cols("A", "B", "C", "D"), data(row(
				1, 2, 3, 4),
				row(5, 6, 7, 8),
				row(5, 9, 10, 11)));
		GroupAdapter adapter = new GroupAdapter(resultSet);
		adapter.addGroup(GroupAdapter.NO_GROUP, new NoGroupState());
		adapter.addGroup("A", new OpenState());
		assertTrue(adapter.next(GroupAdapter.NO_GROUP));
		assertTrue(!adapter.next("A"));
		Map<String, Object> row = new HashMap<String, Object>();
		adapter.getRow(row );
		assertEquals(2, row.get("b"));
	}
	
	@Test
	public void testHandleGroupOverOnNextTwoElements() {
		ResultSet resultSet = MockResultSet.create(cols("A", "B", "C", "D"), data(row(
				1, 2, 3, 4),
				row(1, 444, 444, 444),
				row(5, 6, 7, 8),
				row(5, 9, 10, 11)));
		GroupAdapter adapter = new GroupAdapter(resultSet);
		adapter.addGroup(GroupAdapter.NO_GROUP, new NoGroupState());
		adapter.addGroup("A", new OpenState());
		assertTrue(adapter.next(GroupAdapter.NO_GROUP));
		assertTrue(adapter.next("A"));
		assertTrue(!adapter.next("A"));
		Map<String, Object> row = new HashMap<String, Object>();
		adapter.getRow(row );
		assertEquals(444, row.get("b"));
	}
}
