/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.Date;

import com.exigeninsurance.x4j.analytic.api.ReportException;


abstract public class MockResultSet implements ResultSet {

	public static ResultSet create(final String[] columns,final Object[][] rows){

		return create(columns,rows,0);

	}

    private MockResultSet() {

    }

	public static ResultSet create(final String[] columns,final Object[][] rows, final int generate){
        return (ResultSet) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{ResultSet.class}, new InvocationHandler() {

            private int currentRow = -1;
            private int generated = 0;
            private ResultSetMetaData metadata;

            public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {

                if(method.getName().equals("next")){
                    if(currentRow + 1 == rows.length ){
                        if( generated >= generate){
                            return false;
                        }else {
                            generated ++;
                            return true;
                        }
                    }

                    currentRow++;
                    return true;
                }else if (method.getName().equals("getMetaData")){
                    if(metadata == null){
                        metadata = createMatadata(columns,rows.length == 0 ? null : rows[0]);
                    }
                    return 	metadata;
                }else if( method.getName().startsWith("get") ){

                    if(args[0] instanceof Integer ){
                        if(generate > 0 && rows[currentRow][ (Integer)args[0] - 1 ] instanceof String){
                            rows[currentRow][ (Integer)args[0] - 1 ] = System.currentTimeMillis() + "0";
                        }
                        return narrow(rows[currentRow][ (Integer)args[0] - 1], method.getReturnType() );
                    }else {
                        int index = 0;
                        for (String colName : columns){
                            if(colName.equalsIgnoreCase(args[0].toString())){
                                if(generate > 0 && rows[currentRow][ index ] instanceof String){
                                    rows[currentRow][ index ] = System.currentTimeMillis() + "0";
                                }
                                return narrow(rows[currentRow][ index ],  method.getReturnType());
                            }else {
                                index++;
                            }
                        }
                        throw new IllegalArgumentException(args[0] + " not found");

                    }


                }else if (method.getName().equals("close")){
                    currentRow = -1;
                    generated = 0;
                }
                else if (method.getName().equals("isLast")) {
                    return (generated + 1 == generate);
                }

                return null;
            }


        });
	}

	private static Object narrow(Object object,  Class<?> cls){
		
		if(cls == Double.class || cls == Double.TYPE){
			return new Double( Double.valueOf((Double)object) );
		}else if (cls == java.sql.Date.class){
			
			return new java.sql.Date( ((Date)object).getTime() );
		}
		
		
		return object;
	}
	
	
	private static ResultSetMetaData createMatadata(final String[] columns, final Object[] rows) {
        return (ResultSetMetaData) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{ResultSetMetaData.class}, new InvocationHandler(){

            public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {

                if(method.getName().equals("getColumnCount")){
                    return columns.length;
                }else if (method.getName().equals("getColumnName")){
                    return columns[ (Integer)args[0] - 1];
                }else if (method.getName().equals("getColumnType")){
                	if(rows == null || rows.length == 0){
                		return Types.VARCHAR;
                	}
                    Object col = rows[(Integer)args[0] - 1];
                    if(col instanceof BigDecimal ){
                    	return Types.NUMERIC;
                    }else if (col instanceof Number){
                       return Types.INTEGER;	
                    }else if (col instanceof String){
                    	return Types.VARCHAR;
                    }else if (col instanceof Date){
                    	return Types.DATE;
                    }
                    
                    throw new ReportException("unimplemented type" + col);                    
                }

                return null;
            }


        });
	}

	public static String [] cols(String  ... args ){
		return args;
	}
	public static Object[] row(Object ... row ){
		return row;
	}

	public static Object[][] data(Object[] ... row ){
		return row;
	}
}