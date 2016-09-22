/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.groups;



public class Group {
	
	private GroupState state;
	private boolean last;
	private boolean first;
	
	public Group(GroupState state) {
		this.state = state;
	}
	
	public GroupTransitionResult next() {
		GroupTransitionResult next = state.next();
		state = next.getNewState();
		first = true;
		last = false;
		return next;
	}

	public GroupState getState() {
		return state;
	}

	public void setState(GroupState state) {
		this.state = state;
	}

	public void transitionOnNext(boolean next) {
		state = state.transitionOnNext();
		first = false;
	}
	
	public void transitionOnGroupOver() {
		state = state.transitionOnGroupOver(state);
		last = true;
		first = false;
	}

	public boolean isLast() {
		return last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}

	public boolean isFirst() {
		return first;
	}

	public void setFirst(boolean first) {
		this.first = first;
	}
}
