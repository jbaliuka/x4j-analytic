/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.instruction.Instruction;


public class Line extends Instruction {
	

	public Line(String contents) {
		super(contents);
	}

	public void draw(RenderingContext renderingContext) throws Exception {
		for (Instruction child : getChildren()) {
			child.draw(renderingContext);
			renderingContext.getPdfContext().movePointerBy(child.estimateWidth(renderingContext), 0f);
		}
		
	}

	public float estimateWidth(RenderingContext context) {
		float totalWidth = 0f;
		for (Instruction child : getChildren()) {
			totalWidth += child.estimateWidth(context);
		}
		return totalWidth;
	}

	public float estimateHeight(RenderingContext context)  {
		float maxHeight = 0f;
		for (Instruction child : getChildren()) {
			float currentHeigth = child.estimateHeight(context);
			if (currentHeigth > maxHeight) {
				maxHeight = currentHeigth;
			}
		}
		return maxHeight;
	}

	public void notify(PdfContext context) {

	}

}
