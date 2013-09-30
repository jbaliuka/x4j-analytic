/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.util;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.instruction.Instruction;
import com.exigeninsurance.x4j.analytic.xlsx.utils.InstructionParser;

public class InstructionParserTest {

	@Test
	public void testStringParse() {
		String contents = "test test2";
		List<Instruction> instructions = InstructionParser.parseInstructions(contents, "ignored");
		assertEquals(1, instructions.size());
		assertEquals(contents, instructions.get(0).getContents());
	}
	
	@Test
	public void testInstructionParse() {
		String contents = "Page of &P";
		List<Instruction> instructions = InstructionParser.parseInstructions(contents, "ignored");
		assertEquals(2, instructions.size());
		assertEquals("Page of ", instructions.get(0).getContents());
		assertEquals("&P", instructions.get(1).getContents());
	}
	
	@Test
	public void testComplexParse() {
		String contents = "Page of &P in &N";
		List<Instruction> instructions = InstructionParser.parseInstructions(contents, "ignored");
		assertEquals(4, instructions.size());
		assertEquals("Page of ", instructions.get(0).getContents());
		assertEquals("&P", instructions.get(1).getContents());
		assertEquals(" in ", instructions.get(2).getContents());
		assertEquals("&N", instructions.get(3).getContents());
	}
	
	@Test
	public void testAmpersandsParse() {
		String contents = "&&P";
		List<Instruction> instructions = InstructionParser.parseInstructions(contents, "ignored");
		assertEquals(2, instructions.size());
		assertEquals("&", instructions.get(0).getContents());
		assertEquals("&P", instructions.get(1).getContents());
	}
	
	@Test
	public void testManyAmpersandsParse() {
		String contents = "&&&&P& ";
		List<Instruction> instructions = InstructionParser.parseInstructions(contents, "ignored");
		assertEquals(3, instructions.size());
		assertEquals("&&&", instructions.get(0).getContents());
		assertEquals("&P", instructions.get(1).getContents());
		assertEquals("& ", instructions.get(2).getContents());
	}
	
	@Test
	public void testFontSize() {
		String contents = "&14test";
		List<Instruction> instructions = InstructionParser.parseInstructions(contents, "ignored");
		Instruction instruction = instructions.get(0);
		assertEquals(1, instructions.size());
		assertEquals(14, instruction.getFontSize());
		assertEquals("test", instruction.getContents());
	}
	
	@Test
	public void testFontStyle() {
		String contents = "&\"-,Bold\"test";
		List<Instruction> instructions = InstructionParser.parseInstructions(contents, "ignored");
		assertEquals(1, instructions.size());
		Instruction instruction = instructions.get(0);
		assertEquals("-,Bold", instruction.getFontStyle());
		assertEquals("test", instruction.getContents());
	}
	
	@Test
	public void testSizeAndStyle() {
		String contents = "&\"-,Bold\"&12test";
		List<Instruction> instructions = InstructionParser.parseInstructions(contents, "ignored");
		assertEquals(1, instructions.size());
		Instruction instruction = instructions.get(0);
		assertEquals("-,Bold", instruction.getFontStyle());
		assertEquals(12, instruction.getFontSize());
		assertEquals("test", instruction.getContents());
	}
	
	@Test
	public void testMixedFormattings() {
		String contents = "&\"-,Bold\"&12test &\"-,Regular\"&11test2";
		List<Instruction> instructions = InstructionParser.parseInstructions(contents, "ignored");
		assertEquals(2, instructions.size());
		Instruction instruction = instructions.get(0);
		assertEquals("-,Bold", instruction.getFontStyle());
		assertEquals(12, instruction.getFontSize());
		assertEquals("test ", instruction.getContents());
		Instruction instruction2 = instructions.get(1);
		assertEquals("-,Regular", instruction2.getFontStyle());
		assertEquals(11, instruction2.getFontSize());
		assertEquals("test2", instruction2.getContents());
	}
}
