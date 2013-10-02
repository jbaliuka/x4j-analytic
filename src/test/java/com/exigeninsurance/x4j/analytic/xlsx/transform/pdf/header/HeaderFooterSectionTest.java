/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContainer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.instruction.Instruction;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.instruction.SimpleTextInstruction;

public class HeaderFooterSectionTest {
	
	@Test
	public void testInstructionGroupingWithoutNewLine() {
		HeaderFooterSection section = new MockHeaderFooterSection("ignored", "ignored");
		List<Instruction> instructions = new ArrayList<Instruction>();
		instructions.add(new SimpleTextInstruction("1 "));
		instructions.add(new SimpleTextInstruction("2 "));
		instructions.add(new SimpleTextInstruction("3 "));
		section.setDrawingInstructions(instructions);
		section.groupInstructionsToLines();
		assertEquals(1, section.getDrawingInstructions().size());
	}
	
    @Test
    public void testInstructionGroupingWithNewLine() {
		HeaderFooterSection section = new MockHeaderFooterSection("ignored", "ignored");
		List<Instruction> instructions = new ArrayList<Instruction>();
		instructions.add(new SimpleTextInstruction("&P of &N "));
		instructions.add(new SimpleTextInstruction("\n"));
		instructions.add(new SimpleTextInstruction("next line"));
		section.setDrawingInstructions(instructions);
		section.groupInstructionsToLines();
		assertEquals(2, section.getDrawingInstructions().size());
    }
    
	private class MockHeaderFooterSection extends HeaderFooterSection {
		public MockHeaderFooterSection(String contents, String headerFooter) {
			super(contents, headerFooter);
            sectionPosition = "mock";
		}

		public void draw(RenderingContext context) throws Exception {
		}

        @Override
        public void doDraw(RenderingContext context) throws Exception {
        }

       

		public void notify(PdfContext context) {
		}

		public void setParent(PdfContainer element) {
		}

		public PdfContainer getParent() {
			return null;
		}

		public float getHeigth() {
			return 0;
		}
	}
}
