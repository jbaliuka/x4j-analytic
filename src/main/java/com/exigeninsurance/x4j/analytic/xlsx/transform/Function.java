/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform;


/**
 * Supported sub total function types in XLSX tables
 * @author jbaliuka
 *
 */

public enum Function{
	SUM,
	COUNT,
	AVG,
	AVERAGE,
	/**
	 * Custom is not implemented it is converted to SUM
	 */
	CUSTOM

}