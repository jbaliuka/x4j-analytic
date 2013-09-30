
/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.node;

import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.xlsx.transform.MergedRegion;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


public class MergedCellsNode extends Node {

	public MergedCellsNode(XSSFSheet sheet) {
		super(sheet);
	}

	@Override
	public void process(XLXContext context) throws Exception {
		List<MergedRegion> regions = context.getNewMergedCells();
		if (!regions.isEmpty()) {
			context.write("<mergeCells count=\"" + regions.size() + "\">");
			for (MergedRegion region : regions) {
				context.write("<mergeCell ref=\"" + region.toString() + "\"/>");
			}
			context.write("</mergeCells>");
		}
	}
}
