/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;



public class CursorMetadata implements Serializable {

	private static final long serialVersionUID = 1L;

	private String[] columns;

	public CursorMetadata(){}
	public  CursorMetadata(String[] columns){

		this.columns = columns;
	}

	public int getColumnCount() {
		return columns.length;
	}

	/**
	 * index is 1 based
	 */

	public String getColumnName(int i) {

		return columns[i - 1];
	}

	public static CursorMetadata createFromResultSet(ResultSet rs) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		String[] columns = new  String[metaData.getColumnCount()];
		int count =  metaData.getColumnCount();
		for (int i = 0; i < count; i++) {
			columns[i] = metaData.getColumnName(i + 1);            
		}
		return new CursorMetadata(columns);
	}

	public Object[] readRow(DataInput data) throws IOException, ClassNotFoundException {
		int len = data.readInt();
		byte[] bytes = new byte[len];
		data.readFully(bytes);
		ObjectInputStream in  = new ObjectInputStream(new ByteArrayInputStream(bytes));
		try{
			Object [] objects = new Object [columns.length];
			for(int i = 0; i < columns.length; i++){
				objects[i] = in.readObject();

			}
			return objects;
		}finally{
			in.close();
		}



	}

	public void writeRow(DataOutput objectOut, ResultSet rs) throws IOException, SQLException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try{
			ObjectOutputStream oout = new ObjectOutputStream(out);	
			try{
				ResultSetMetaData metadata = rs.getMetaData();
				for(int i = 0 ; i < columns.length; i++ ){
					String name = metadata.getColumnTypeName(i + 1);
					oout.writeObject( convert(rs.getObject(i + 1),name) );
				}
				oout.flush();
				byte[] bytes = out.toByteArray();
				objectOut.writeInt(bytes.length);
				objectOut.write(bytes);
			}finally{
				oout.close();
			}
		}finally{
			out.close();
		}

	}
	public static Object convert(Object object, String name) {
		if("DATE".equals(name) && object instanceof Timestamp){
			return new Date(((Date)object).getTime());
		}
		return object;
	}
}
