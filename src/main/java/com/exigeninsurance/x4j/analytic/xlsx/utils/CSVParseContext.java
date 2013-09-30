/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.poi.util.Internal;

@Internal
public class CSVParseContext {

	private static final CSVParseState ESCAPESTRINGSTATE = new EscapeStringState();
	private static final CSVParseState QUOTEDSTRINGSTATE = new QuotedStringState();
	private static final CSVParseState INITSTATE = new InitState() ;

    @Internal
	abstract static class CSVParseState {
		abstract void next(CSVParseContext context,String token);
		abstract void nextSeparator(CSVParseContext context, char separator);
		abstract void nextQuote(CSVParseContext context, char c);
		abstract void endLine(CSVParseContext context);
	}

	private static final class InitState extends CSVParseState {
		@Override
		void next(CSVParseContext context, String token) {
			context.getBuilder().append(token);
		}

		@Override
		void nextSeparator(CSVParseContext context, char separator) {
			context.nextField();
		}

		@Override
		void nextQuote(CSVParseContext context, char c) {			
			context.setCurrentState(QUOTEDSTRINGSTATE);
		}

		@Override
		void endLine(CSVParseContext context) {
			context.nextField();
		}
	}

	private static final class QuotedStringState extends CSVParseState {
		@Override
		void next(CSVParseContext context, String token) {
			context.getBuilder().append(token);			
		}

		@Override
		void nextSeparator(CSVParseContext context, char separator) {
			context.getBuilder().append(separator);		
		}

		@Override
		void nextQuote(CSVParseContext context, char c) {
			context.setCurrentState(ESCAPESTRINGSTATE);
		}

		@Override
		void endLine(CSVParseContext context) {
			context.nextField();
		}
	}

	private static final class EscapeStringState extends CSVParseState {
		@Override
		void next(CSVParseContext context, String token) {
			context.getBuilder().append(token);
		}

		@Override
		void nextSeparator(CSVParseContext context, char separator) {
			context.nextField();
			context.setCurrentState(INITSTATE);			
		}

		@Override
		void nextQuote(CSVParseContext context, char c) {
			context.getBuilder().append(c);
			context.setCurrentState(QUOTEDSTRINGSTATE);
		}

		@Override
		void endLine(CSVParseContext context) {
			context.nextField();
		}
	}

	private  CSVParseState currentState = INITSTATE;
	private final char separator;
	private StringBuilder builder = new StringBuilder();
	private final List<String> fields = new ArrayList<String>();

	public CSVParseState getCurrentState() {
		return currentState;
	}

	public void setCurrentState(CSVParseState currentState) {
		this.currentState = currentState;
	}
	
	public CSVParseContext(char separator){
		this.separator = separator;
	}
	
	public List<String> parse(String line){
		StringTokenizer tokens = new StringTokenizer(line,separator + "\"",true);
		while(tokens.hasMoreTokens()){
			String token = tokens.nextToken();
			if(token.length() == 1){
				if(separator == token.charAt(0)){
					currentState.nextSeparator(this, separator);
					continue;
				}else if ('\"' == token.charAt(0) ){
					currentState.nextQuote(this, '\"');
					continue;
				}
			}
			currentState.next(this, token);
		}
		currentState.endLine(this);
		return fields;
	}

	public StringBuilder getBuilder() {
		return builder;
	}

	public List<String> getFields() {
		return fields;
	}

	void nextField(){
		fields.add(builder.toString().trim());
		builder = new StringBuilder();
	}
}
