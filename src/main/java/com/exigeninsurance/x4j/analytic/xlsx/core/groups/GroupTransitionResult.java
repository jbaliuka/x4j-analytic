/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.groups;


public class GroupTransitionResult {
	
	private boolean skipping;
	private boolean nextAfterSkip;
	private GroupState newState;
	
	public GroupTransitionResult(boolean skipping, boolean nextAfterSkip, GroupState newState) {
		this.skipping = skipping;
		this.newState = newState;
		this.nextAfterSkip = nextAfterSkip;
	}

	public boolean isSkipping() {
		return skipping;
	}

	public void setSkipping(boolean skipping) {
		this.skipping = skipping;
	}

	public GroupState getNewState() {
		return newState;
	}

	public void setNewState(GroupState newState) {
		this.newState = newState;
	}

	public boolean isNextAfterSkip() {
		return nextAfterSkip;
	}

	public void setNextAfterSkip(boolean nextAfterSkip) {
		this.nextAfterSkip = nextAfterSkip;
	}

}
