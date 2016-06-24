/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.groups;


public abstract class GroupState {
	
	public abstract GroupState transitionOnNext();
	
	protected abstract GroupState transitionOnSkip();

	public abstract boolean processesGroups();
	
	protected abstract boolean skipNext();
	
	protected abstract boolean nextAfterSkip();
	
	
	public GroupTransitionResult next() {
		if (skipNext()) {
			return new GroupTransitionResult(true,  nextAfterSkip(), transitionOnSkip());
		}
		else {
			return new GroupTransitionResult(false,  nextAfterSkip(), this);
		}
	}
	
	protected GroupState transitionOnGroupOver(GroupState oldState) {
		return new LockedState(new FrozenState(oldState));
	}
}