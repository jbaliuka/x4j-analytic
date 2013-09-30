/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.util.Internal;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.instruction.CurrentPageInstruction;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.instruction.DateInstruction;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.instruction.Instruction;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.instruction.NewLineInstruction;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.instruction.PictureInstruction;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.instruction.SimpleTextInstruction;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.instruction.TimeInstruction;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.instruction.TotalPagesInstruction;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.instruction.UnimplementedInstruction;

@Internal
public class InstructionParser { //NOSONAR

    private static final char QUOTE = '\"';
    private static final char AMPERSAND = '&';
    private static final char NEW_LINE = '\n';

    private static final char CURRENT_PAGE_EXPRESSION = 'P';
    private static final char TOTAL_PAGES_EXPRESSION = 'N';
    private static final char DATE_EXPRESSION = 'D';
    private static final char TIME_EXPRESSION = 'T';
    private static final char SHEET_EXPRESSION = 'A';
    private static final char FILE_NAME_EXPRESSION = 'F';
    private static final char FILE_PATH_EXPRESSION = 'Z';
    private static final char PICTURE_EXPRESSION = 'G';

    private final InstructionParseState STRING_STATE = new StringState();
    private final InstructionParseState AMPERSAND_STATE = new AmpersandState();
    private final InstructionParseState QUOTE_STATE = new QuoteState();
    private final InstructionParseState DIGIT_STATE = new DigitState();
	
	private final List<Instruction> drawingInstructions = new ArrayList<Instruction>();
	private final List<FormattingInstruction> formattingInstructions = new ArrayList<FormattingInstruction>();
	private InstructionParseState currentState;
	private StringBuilder builder = new StringBuilder();
	private StringBuilder instructionBuilder = new StringBuilder();
	private final String instructions;

    private String sectionCode;
	
	private InstructionParser(String instructions) {
		this.instructions = instructions;
	}

	public static List<Instruction> parseInstructions(String instructions, String sectionCode) {
		InstructionParser instructionParser = new InstructionParser(instructions);
		instructionParser.sectionCode = sectionCode;
		return instructionParser.parseInstructions();
	}

	private List<Instruction> parseInstructions() {
		currentState = STRING_STATE;
		for (char token : instructions.toCharArray()) {
			switch(token) {
			case AMPERSAND:
				currentState.processAmpersand(this, token);
				break;
			case NEW_LINE:
				currentState.processNewLine(this, token);
				break;
			case QUOTE:
				currentState.processQuote(this, token);
				break;
			default:
				currentState.processCharacter(this, token);
				break;
			}

		}
		currentState.processEnd(this);
		return drawingInstructions;
	}
	
    private void evaluateAndProcessInstruction(char token) {
		
		switch(token) {
		case CURRENT_PAGE_EXPRESSION:
			appendTextInstructionAndApplyFormatting();
			drawingInstructions.add(new CurrentPageInstruction("&" + token));
			break;
		case TOTAL_PAGES_EXPRESSION:
			appendTextInstructionAndApplyFormatting();
			drawingInstructions.add(new TotalPagesInstruction("&" + token));
			break;
		case DATE_EXPRESSION:
			appendTextInstructionAndApplyFormatting();
			drawingInstructions.add(new DateInstruction("&" + token));
			break;
		case TIME_EXPRESSION:
			appendTextInstructionAndApplyFormatting();
			drawingInstructions.add(new TimeInstruction("&" + token));
			break;
        case PICTURE_EXPRESSION:
            appendTextInstructionAndApplyFormatting();
            drawingInstructions.add(new PictureInstruction("&" + token, sectionCode));
            break;
		case SHEET_EXPRESSION:
		case FILE_NAME_EXPRESSION:
		case FILE_PATH_EXPRESSION:
			appendTextInstructionAndApplyFormatting();
			drawingInstructions.add(new UnimplementedInstruction("&" + token));
			break;

		default:
			builder.append("&").append(Character.toString(token));
			break;
		}
		
	}

	private void appendNewLineInstruction() {
		drawingInstructions.add(new NewLineInstruction("\n"));
	}
	
	private void appendTextInstructionAndApplyFormatting() {
		String instructionString = builder.toString();
		if (!instructionString.equals("")) {
			SimpleTextInstruction instruction = new SimpleTextInstruction(instructionString);
			for (FormattingInstruction formattingInstruction : formattingInstructions) {
				formattingInstruction.applyTo(instruction);
			}
			drawingInstructions.add(instruction);
			builder = new StringBuilder();
			formattingInstructions.clear();
		}
		
	}

	private abstract class InstructionParseState {
		
		public abstract void processCharacter(InstructionParser context, char token);
		
		public abstract void processQuote(InstructionParser context, char token);

		public abstract void processAmpersand(InstructionParser context, char token);
		
		public abstract void processNewLine(InstructionParser context, char token);
		
		public abstract void processEnd(InstructionParser context);
		
	}

	private final class StringState extends InstructionParseState {

		@Override
		public void processCharacter(InstructionParser context, char token) {
			context.getBuilder().append(token);
			
		}

		@Override
		public void processAmpersand(InstructionParser context, char token) {
			context.setCurrentState(AMPERSAND_STATE);
		}

		@Override
		public void processEnd(InstructionParser context) {
			context.appendTextInstructionAndApplyFormatting();
		}

		@Override
		public void processNewLine(InstructionParser context, char token) {
			context.appendTextInstructionAndApplyFormatting();
			context.appendNewLineInstruction();
		}

		@Override
		public void processQuote(InstructionParser context, char token) {
			context.getBuilder().append(token);
		}
	}

	private final class AmpersandState extends InstructionParseState {

		@Override
		public void processCharacter(InstructionParser context, char token) {
			if (Character.isDigit(token)) {
				appendTextInstructionAndApplyFormatting();
				context.setCurrentState(DIGIT_STATE);
				context.getCurrentState().processCharacter(context, token);
			}
			else {
				context.evaluateAndProcessInstruction(token);
				context.setCurrentState(STRING_STATE);
			}
		}

		@Override
		public void processAmpersand(InstructionParser context, char token) {
			context.getBuilder().append("&");
		}

		@Override
		public void processEnd(InstructionParser context) {
			context.getBuilder().append("&");
			context.appendTextInstructionAndApplyFormatting();
			
		}

		@Override
		public void processNewLine(InstructionParser context, char token) {
			context.getBuilder().append("&");
			context.appendTextInstructionAndApplyFormatting();
			context.appendNewLineInstruction();
		}

		@Override
		public void processQuote(InstructionParser context, char token) {
			context.appendTextInstructionAndApplyFormatting();
			context.setCurrentState(QUOTE_STATE);
			
		}
	}

	private final class QuoteState extends InstructionParseState {

		@Override
		public void processCharacter(InstructionParser context, char token) {
			instructionBuilder.append(token);
		}

		@Override
		public void processQuote(InstructionParser context, char token) {
			String formattingInstruction = context.getInstructionBuilder().toString();
			context.getFormattingInstructions().add(new FontStyleInstruction(formattingInstruction));
			context.setCurrentState(STRING_STATE);
			context.setInstructionBuilder(new StringBuilder());
		}

		@Override
		public void processAmpersand(InstructionParser context, char token) {
			instructionBuilder.append(token);
		}

		@Override
		public void processNewLine(InstructionParser context, char token) {
			instructionBuilder.append(token);
		}

		@Override
		public void processEnd(InstructionParser context) {

		}
	}

	private final class DigitState extends InstructionParseState {
		
		private void addFontSizeInstruction(InstructionParser context) {
			String instructionString = context.getInstructionBuilder().toString();
			FormattingInstruction formattingInstruction = new FontSizeInstruction(instructionString);
			context.getFormattingInstructions().add(formattingInstruction);
			context.setInstructionBuilder(new StringBuilder());
		}

		@Override
		public void processCharacter(InstructionParser context, char token) {
			if (Character.isDigit(token)) {
				context.getInstructionBuilder().append(token);
			}
			else {
				addFontSizeInstruction(context);
				context.setCurrentState(STRING_STATE);
				context.currentState.processCharacter(context, token);
			}
		}

		@Override
		public void processQuote(InstructionParser context, char token) {
			addFontSizeInstruction(context);
			context.setCurrentState(STRING_STATE);
			context.currentState.processQuote(context, token);
		}

		@Override
		public void processAmpersand(InstructionParser context, char token) {
			addFontSizeInstruction(context);
			context.setCurrentState(AMPERSAND_STATE);
			context.currentState.processAmpersand(context, token);
		}

		@Override
		public void processNewLine(InstructionParser context, char token) {
			addFontSizeInstruction(context);
			context.setCurrentState(STRING_STATE);
			context.currentState.processNewLine(context, token);
		}

		@Override
		public void processEnd(InstructionParser context) {
			context.getBuilder().append(context.getInstructionBuilder().toString());
			context.setCurrentState(STRING_STATE);
			context.currentState.processEnd(context);
		}
		
	}

	private abstract class FormattingInstruction {
		
		private String value;
		
		public FormattingInstruction(String value) {
			this.value = value;
		}

		public abstract void applyTo(Instruction instruction);

        public String getValue() {
            return value;
        }
    }

	private class FontSizeInstruction extends FormattingInstruction {

		public FontSizeInstruction(String value) {
			super(value);
		}

		public void applyTo(Instruction instruction) {
			instruction.setFontSize(Integer.parseInt(getValue()));
		}
		
	}

	private class FontStyleInstruction extends FormattingInstruction {

		public FontStyleInstruction(String value) {
			super(value);
		}

		public void applyTo(Instruction instruction) {
			instruction.setFontStyle(getValue());
		}
		
	}
	
	private StringBuilder getInstructionBuilder() {
		return instructionBuilder;
	}

	private void setInstructionBuilder(StringBuilder instructionBuilder) {
		this.instructionBuilder = instructionBuilder;
	}

	private List<FormattingInstruction> getFormattingInstructions() {
		return formattingInstructions;
	}

	private InstructionParseState getCurrentState() {
		return currentState;
	}

	private void setCurrentState(InstructionParseState currentState) {
		this.currentState = currentState;
	}

	private StringBuilder getBuilder() {
		return builder;
	}
}


