/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.util;

import java.io.*;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.exigeninsurance.x4j.analytic.api.Cursor;
import com.exigeninsurance.x4j.analytic.api.CursorMetadataFabric;


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

	/*
	 //Alternative implementation
	 public static CursorMetadata createFromResultSet(final ResultSet rs) throws SQLException {
	 	return createFromResultSet(rs, new CursorMetadataFabric(){

			@Override
			public CursorMetadata createFromResultSet(ResultSet rs) throws SQLException {
				ResultSetMetaData metaData = rs.getMetaData();
				String[] columns = new  String[metaData.getColumnCount()];
				int count =  metaData.getColumnCount();
				for (int i = 0; i < count; i++) {
					columns[i] = metaData.getColumnName(i + 1);
				}
				return new CursorMetadata(columns);
			}
		});
	 }
	*/

	/**
	 * Provide ability for custom fabric and extension of CursorMetadata
	 * @param rs
	 * @param cursorMetadataFabric
	 * @return
	 * @throws SQLException
	 */
	public static CursorMetadata createFromResultSet(ResultSet rs, CursorMetadataFabric cursorMetadataFabric) throws SQLException {
		return cursorMetadataFabric.createFromResultSet(rs);
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

	public void writeRow(DataOutput objectOut, Cursor rs) throws IOException, SQLException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			ObjectOutputStream oout = new ObjectOutputStream(out);

			try {
				for(int bytes = 0; bytes < this.columns.length; ++bytes) {
					Object obj=rs.getObject(bytes + 1);
					if(obj instanceof Clob){
						oout.writeObject(clobToString((Clob)obj));
					}
					else{
						oout.writeObject(obj);
					}
				}

				oout.flush();
				byte[] bytes = out.toByteArray();
				objectOut.writeInt(bytes.length);
				objectOut.write(bytes);
			} finally {
				oout.close();
			}
		}finally{
			out.close();
		}
	}

	private Serializable clobToString(Clob data) throws SQLException, IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader( data.getCharacterStream());
		try {
			String line;
			while(null != (line = br.readLine())) {
				sb.append(line);
			}
		} finally {
			br.close();
		}
		return sb;
	}

}
