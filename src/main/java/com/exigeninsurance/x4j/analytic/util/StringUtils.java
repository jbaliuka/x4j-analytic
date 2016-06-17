/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.util;

import java.util.Collection;
import java.util.Iterator;


public final class StringUtils {
	private StringUtils() {}
	
	public static String join(Object[] arr, String separator) {
		StringBuilder sb = new StringBuilder();
		if (arr.length > 0) {
			sb.append(arr[0].toString());
		}
		for (int i = 1; i < arr.length; i++) {
			sb.append(separator);
			sb.append(arr[i]);
		}
		return sb.toString();
	}
	
	public static String join(Collection<?> col, String separator) {
		StringBuilder sb = new StringBuilder();
		Iterator<?> it = col.iterator();
		if (!col.isEmpty()) {
			Object obj = it.next();
			sb.append(obj.toString());
		}
		while (it.hasNext()) {
			Object obj = it.next();
			sb.append(separator);
			sb.append(obj.toString());
		}
		return sb.toString();
	}
	
	public static String join(Object[] arr) {
		return join(arr, ",");
	}
	
	public static String join(Collection<?> arr) {
		return join(arr, ",");
	}
	
	public static boolean equals(String str1,String str2){
		
		if(str1 == null && str2 == null){
			return true;
		}else if (str1 == null){			
			return false;			
		}				
		return str1.equals(str2);
	}

	public static boolean isEmpty(String s) {
		
		return s == null || s.isEmpty();
	}
}
