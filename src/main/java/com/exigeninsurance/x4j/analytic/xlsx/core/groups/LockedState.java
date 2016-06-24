/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.groups;


public class LockedState extends GroupState {
	
	private final GroupState previousState;
	
	public LockedState(GroupState previousState) {
		this.previousState = previousState;
	}

	public GroupState transitionOnNext() {
		return previousState;
	}

	public boolean processesGroups() {
		return false;
	}

	protected boolean skipNext() {
		return true;
	}

	protected boolean nextAfterSkip() {
		return false;
	}
	
	protected GroupState transitionOnSkip() {
		return previousState;
	}

}
