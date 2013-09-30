/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.api;


/**
 * Runtime exception for unexpected error
 *
 * @author jbaliuka
 */

public class ReportException extends RuntimeException {
	private static final long serialVersionUID = 1L;


	public ReportException(String msg, Throwable ex) {
		super(msg, ex);
		
	}

	public ReportException(Throwable ex) {
		super(ex);
		
	}


	public ReportException(String msg) {
		super(msg);		
	}
}
