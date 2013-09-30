/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.expression;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.exigeninsurance.x4j.analytic.model.Money;
import com.exigeninsurance.x4j.analytic.xlsx.transform.Function;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLSXErrorValue;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


public final class AggregateExpression implements XLSXExpression {

	private final XLSXExpression expr;
	private int count;
	private BigDecimal sum;
	private String currencyCd;
	private final Function function;
	private boolean currencyError;

	public AggregateExpression(XLSXExpression expr,Function function){
		this.expr = expr;
		this.function = function;
        sum = BigDecimal.ZERO;

	}

	public Object evaluate(XLXContext context) throws Exception {
		Object object = expr.evaluate(context);
		BigDecimal value = toDecimal(object);		
		sum = sum.add(value);		
		count++;		
		return object;
	}



	private BigDecimal toDecimal(Object value) {

		if(value == null){
			return BigDecimal.ZERO;
		}else if(value instanceof BigDecimal){
			return (BigDecimal) value;
		}
		else if (value instanceof Money) {
			Money money = (Money) value;							
			if(currencyCd != null && !currencyCd.equals(money.getCurrencyCd())) {				
				 currencyError = true;
			}
		    currencyCd =   money.getCurrencyCd();
			return money.getValue();
		}else if (value instanceof Number){
			return  new BigDecimal(value.toString());
		}else if (value instanceof String ){
			try{
				return new BigDecimal(value.toString());
			}catch(NumberFormatException nfe){
				return BigDecimal.ZERO;
			}
		}

		return BigDecimal.ZERO;
	}


	public Object getValue(){ //NOSONAR
		switch (function) {
		case AVG:
		case AVERAGE:
			return getAvg();
		case COUNT:
			return getCount();
		case SUM:
		case CUSTOM:
			return getSum();
		default:
			throw new IllegalStateException();
		}
	}

	public Object getSum(){
		if(!currencyError){
			return new Money(currencyCd, sum);
		}else {
			return XLSXErrorValue.INSATNCE;
		}
	}

	public  Object getAvg() {	

		if(count == 0){
			return null;
		}
		BigDecimal avg =  sum.divide(new BigDecimal(count),RoundingMode.HALF_UP);
		if(!currencyError){
			return new Money(currencyCd, avg);
		}else {
			return XLSXErrorValue.INSATNCE;
		}

	}

	public BigDecimal getCount(){
		return new BigDecimal(count);
	}



	public String getCurrencyCd() {
		return currencyCd;
	}

	public void setCurrencyCd(String currencyCd) {
		this.currencyCd = currencyCd;
	}

	

}
