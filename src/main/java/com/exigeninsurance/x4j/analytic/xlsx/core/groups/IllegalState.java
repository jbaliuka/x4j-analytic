/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.groups;


public class IllegalState extends GroupState {

	protected GroupState transitionOnSkip() {
		throw new IllegalStateException();
	}

	public GroupState transitionOnNext() {
		throw new IllegalStateException();
	}

	public boolean processesGroups() {
		throw new IllegalStateException();
	}

	protected boolean skipNext() {
		throw new IllegalStateException();
	}

	protected boolean nextAfterSkip() {
		throw new IllegalStateException();
	}

}
