/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.groups;


public class FrozenState extends GroupState {
	
	private final GroupState previousState;
	
	public FrozenState(GroupState previousState) {
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
		return true;
	}
	
	protected GroupState transitionOnSkip() {
		return previousState;
	}

}
