package com.exigeninsurance.x4j.analytic.xlsx.core.node;

import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.xlsx.transform.PivotTable;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;

public class PivotRefNode extends Node {

	private PivotTable pivot;

	public PivotRefNode(XSSFSheet sheet,PivotTable pivot) {
		super(sheet);
		this.pivot = pivot;
		
	}
	
	@Override
	public void process(XLXContext context) throws Exception {
	
		String style = context.getReportContext().getPivotStyleName();
		if(style != null){
			pivot.getCtPivotTable().setPivotTableStyle(style);
			pivot.getCtPivotTable().getPivotTableStyleInfo().setName(style);
		}
		
		int row = context.getCurrentRow();
		String ref[] = pivot.getCtPivotTable().getLocation().getRef().split(":");
		CellReference top = new CellReference(ref[0]);
		CellReference newTop = new CellReference(row,top.getCol());	
		
		CellReference bottom = new CellReference(ref[1]);
		CellReference newBottom = new CellReference(bottom.getRow() - top.getRow() + row ,bottom.getCol());	
		
		
		pivot.getCtPivotTable().getLocation().setRef(newTop.formatAsString() + ":" + newBottom.formatAsString());
		
	}

}
