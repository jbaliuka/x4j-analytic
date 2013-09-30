
/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/
package com.exigeninsurance.x4j.analytic.model;


import java.math.BigDecimal;

/**
 * Special data type to format report cell as money
 * @author jbaliuka
 *
 */
public class Money {
	
	private final String currencyCd;
	private final BigDecimal value;
	
	public Money(String currencyCd, BigDecimal value) {		
		this.currencyCd = currencyCd;
		this.value = (value == null) ? BigDecimal.ZERO : value;		
	}
	
	public String getCurrencyCd() {
		return currencyCd;
	}

	public BigDecimal getValue() {
		return value;
	}
	
	public static Money valueOf(String currencyCd, BigDecimal value) {
		return new Money(currencyCd, value);
	}

	@Override
	public String toString() {
		
		return value + " " + currencyCd;
	}
	
	
}
