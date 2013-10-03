/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.node;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.api.Cursor;
import com.exigeninsurance.x4j.analytic.api.ReportException;
import com.exigeninsurance.x4j.analytic.model.Query;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.groups.FrozenState;
import com.exigeninsurance.x4j.analytic.xlsx.core.groups.GroupState;
import com.exigeninsurance.x4j.analytic.xlsx.core.groups.NoGroupState;
import com.exigeninsurance.x4j.analytic.xlsx.core.groups.OpenState;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


public class ForEachNode extends Node {

	private static final String INDEX = "index";
	private XLSXExpression rows;
	private String var;
	private String groupingColumn;
	private String groupDataObject;


	public ForEachNode(XSSFSheet sheet) {
		super(sheet);	
	}



	public void setVar(String var) {
		this.var = var;
	}

	public String getVar() {
		return var;
	}

	public void setRows(XLSXExpression rows) {
		this.rows = rows;
	}

	public XLSXExpression getRows() {
		return rows;
	}


	@Override
	public void process(final XLXContext context) throws Exception {
		Object rowsObject = rows.evaluate(context);
		
		if(rowsObject == null){
			throw new ReportException("can not iterate null object");
		}
		if(rowsObject instanceof Query){
            Query query = (Query) rowsObject;

            Cursor cursor = context.getReportContext().getCursorManager().createCursor(query);
            process(context, cursor);
        }else {
			process(context,rowsObject);
		}
	}
	public void process(XLXContext context,Object rowsObject) throws Exception {

		String column = fetchColumnName(context);
		
		ResultSetAdapter adapter = getAdapter(rowsObject);
		if (groupingColumn != null) {
			addGroupWrapperToContext(context, adapter);
		}
		
		try {

			int index = 0;
			adapter.push(column);
			while(adapter.next(column)){
				context.getExpresionContext().getVars().put(INDEX, index);
				adapter.getRow((context.getExpresionContext().getVars()));
				if(var != null){
					context.getExpresionContext().getVars().put(var, context.getExpresionContext().getVars());
				}
				super.process(context);
				index++;
			}
			adapter.pop();
			
		}finally{
			adapter.close(column);
		}
	}

	private String fetchColumnName(XLXContext context) {
		String column = (String) context.getExpresionContext().getVars().get(rows.toString() + "_column");
		return column == null ? GroupAdapter.NO_GROUP : column;
	}
	
	private void addGroupWrapperToContext(XLXContext context, ResultSetAdapter parent) {
		parent.addGroup(groupingColumn, getDefaultInitialState());
		String groupName = groupDataObject;
		String column = groupName + "_column";
		
		context.getExpresionContext().getVars().put(groupName, parent);
		context.getExpresionContext().getVars().put(column, groupingColumn);
		
	}
	
	private ResultSetAdapter getAdapter(Object rowsObject) {
		if (rowsObject instanceof ResultSetAdapter) {
			return (ResultSetAdapter) rowsObject;
		}
		else if (rowsObject instanceof Cursor) {
			GroupAdapter groupManager = new GroupAdapter((Cursor) rowsObject);
			groupManager.addGroup(GroupAdapter.NO_GROUP, new NoGroupState());
			return groupManager;
		}
        else {
            throw new ReportException("ForEach can only process cursors");
        }
	}
	
	private GroupState getDefaultInitialState() {
		return new FrozenState(new OpenState());
	}

	public void setGroupingColumn(String groupingColumn) {
		this.groupingColumn = groupingColumn;
	}

	public void setGroupDataObject(String dataObject) {
        groupDataObject = dataObject;
	}
}
