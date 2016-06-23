
/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.xlsx.core.groups;


public class NoGroupState extends GroupState {

	protected GroupState transitionOnSkip() {
		return new IllegalState();
	}

	public GroupState transitionOnNext() {
		return this;
	}

	public boolean processesGroups() {
		return false;
	}

	protected boolean skipNext() {
		return false;
	}

	protected boolean nextAfterSkip() {
		return false;
	}

}
