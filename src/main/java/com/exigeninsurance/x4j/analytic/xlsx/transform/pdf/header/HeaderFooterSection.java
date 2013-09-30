/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header;

import java.util.ArrayList;
import java.util.List;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.DrawablePdfElement;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContainer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.instruction.Instruction;
import com.exigeninsurance.x4j.analytic.xlsx.utils.InstructionParser;


public abstract class HeaderFooterSection implements DrawablePdfElement {

    protected String sectionPosition;
    protected String headerFooter;
	
	private PdfContainer parent;
	private float heigth;
	
	private List<Instruction> drawingInstructions = new ArrayList<Instruction>();
	private String contents;
	
	public HeaderFooterSection(String contents, String headerFooter) {
		this.contents = contents;
        this.headerFooter = headerFooter;
	}
	
	public void prepareForRendering() {
		drawingInstructions = InstructionParser.parseInstructions(contents, sectionPosition + headerFooter);
		groupInstructionsToLines();
	}
	
	public void groupInstructionsToLines() {
		List<Instruction> groupedInstructions = new ArrayList<Instruction>();
		int lastNewLineInstruction = 0;
		for (int i = 0; i < drawingInstructions.size(); i++) {
			Instruction instruction = drawingInstructions.get(i);
			if (instructionSignalsNewLine(instruction)) {
				groupedInstructions.add(groupInstructionsToLine(lastNewLineInstruction, i, drawingInstructions));
				lastNewLineInstruction = i + 1;
			}
		}
		if (!allInstructionsGrouped(lastNewLineInstruction)) {
			groupedInstructions.add(groupInstructionsToLine(lastNewLineInstruction, drawingInstructions.size(), drawingInstructions));
		}
		drawingInstructions = groupedInstructions;
	}
	
	private Line groupInstructionsToLine(int startingInstruction, int endingInstruction, List<Instruction> instructions) {
		Line line = new Line("");
		List<Instruction> lineChildren = new ArrayList<Instruction>();
		for (int i = startingInstruction; i < endingInstruction; i++) {
			lineChildren.add(instructions.get(i));
		}
		
		line.setChildren(lineChildren);
		return line;
	}
	
	private boolean instructionSignalsNewLine(Instruction instruction) {
		return instruction.getContents().equals("\n");
	}
	
	private boolean allInstructionsGrouped(int lastNewLineInstruction) {
		return lastNewLineInstruction == drawingInstructions.size();
	}

    @Override
    public void draw(RenderingContext context) throws Exception {
        doDraw(context);
    }

    public void process(PdfContext context) {
        for (Instruction line : drawingInstructions) {
            line.process(context);
        }
    }

    public abstract void doDraw(RenderingContext context) throws Exception;

    public float estimateHeight(RenderingContext context) {
		float totalHeight = 0f;
		for (Instruction line : drawingInstructions) {
			totalHeight += line.estimateHeight(context);
		}
		heigth = totalHeight;
		return totalHeight;
	}
	
	public float estimateWidth(RenderingContext context) {
		float maxWidth = 0f;
		for (Instruction line : drawingInstructions) {
			float lineWidth = line.estimateWidth(context);
			if (lineWidth > maxWidth) {
				maxWidth = lineWidth;
			}
		}
		return maxWidth;
	}

	public List<Instruction> getDrawingInstructions() {
		return drawingInstructions;
	}

	public void setDrawingInstructions(List<Instruction> drawingInstructions) {
		this.drawingInstructions = drawingInstructions;
	}
	
	public void notify(PdfContext context) {
		
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	@Override
	public void setParent(PdfContainer element) {
		parent = element;
	}

	@Override
	public PdfContainer getParent() {
		return parent;
	}

	@Override
	public float getHeigth() {
		return heigth;
	}
	
	
	
	
}
