/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.groups;



public class OpenState extends GroupState {

	protected GroupState transitionOnSkip() {
		return new IllegalState();
	}

	public GroupState transitionOnNext() {
		return this;
	}
	
	protected boolean skipNext() {
		return false;			
	}

	public boolean processesGroups() {
		return true;
	}

	protected boolean nextAfterSkip() {
		return false;
	}
}
