/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;


public final class Constant implements Estimator{
	private final float constant;
	
	public Constant(float constant) {
		this.constant = constant;
	}

	public float estimate(RenderingContext context) {
		return constant;
	}

}
