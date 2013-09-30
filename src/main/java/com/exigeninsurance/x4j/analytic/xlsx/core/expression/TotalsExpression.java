/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.expression;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.CellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


public final class TotalsExpression implements XLSXExpression {
	
	private final CellNode templateCellNode;

	public TotalsExpression(CellNode templateCellNode2) {
        templateCellNode = templateCellNode2;
	}

	public Object evaluate(XLXContext context)
	throws Exception {
		return ((AggregateExpression)templateCellNode.getExpression()).getValue();
	}

	

    public CellNode getTemplateCellNode() {
        return templateCellNode;
    }
}