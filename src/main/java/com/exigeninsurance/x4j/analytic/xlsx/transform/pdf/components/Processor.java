/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components;

import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


public interface Processor {
	
	void process(XLXContext context) throws Exception;

}
