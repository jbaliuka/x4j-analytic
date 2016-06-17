/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.poi.util.Internal;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfCellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;

@Internal
public final class WrappingUtil {

	public static final String EXCEL_BREAK = " \r\n";
	private static final char WRAPPING_BREAK = '\n';
	private static final String HTML_BREAK = "</br>";
    private static final String EXCEL_INTERNAL_WRAP_BREAK = "_x000a_";
    private static final String NBSP = "&nbsp;";

    private final ParseState whitespaceState = new WhitespaceState();
    private final ParseState nonWhitespaceState = new NonWhitespaceState();

    private String text;
    private StringBuilder builder;
    private int wordCount;
    private ParseState currentState;
    private WrappingStrategy strategy;

    private PdfCellNode cellNode;
    private float cellWidth;
    private List<String> pdfLines;


    private WrappingUtil(String text) {
        this.text = text;
        builder = new StringBuilder();
        currentState = new InitState();
    }

    private WrappingUtil(String text, PdfCellNode cellNode, float width) {
        this.text = text;
        this.cellNode = cellNode;
        cellWidth = width;
        pdfLines = new ArrayList<String>();
    }

    public static String wrap(String text) {
        return new WrappingUtil(text).wrap();
    }

    public static List<String> pdfWrap(String text, PdfCellNode cellNode, float width) {
        return new WrappingUtil(text, cellNode, width).pdfWrap();
    }

    public static String wrapFormula(String formula, XLXContext context) throws Exception {
        StringBuilder builder = new StringBuilder();
        int start = formula.indexOf("'");
        int end = 0;
        while (start != -1) {
            builder.append(formula.substring(end, start + 1));
            end = formula.indexOf("'", start + 1);
            String field = formula.substring(start + 1, end);
            Object value = CellExpressionParser.parseExpression(field).evaluate(context);
            String evaluatedField = value == null ? field : value.toString();
            builder.append(wrapTableColumnName(evaluatedField));
            start = formula.indexOf("'", end + 1);
        }
        builder.append(formula.substring(end));
        return builder.toString();
    }

    public static String wrapTableColumnName(String text) {
        return WrappingUtil.excelWrap(WrappingUtil.wrap(text), EXCEL_INTERNAL_WRAP_BREAK);
    }

    public static String htmlWrap(String text) {
        String s = text.replaceAll(String.valueOf(WRAPPING_BREAK), HTML_BREAK);
        return s.replaceAll(" ", NBSP);
    }

    public static String excelWrap(String text) {
        return text.replaceAll(String.valueOf(WRAPPING_BREAK), EXCEL_BREAK);
    }

    public static String excelWrap(String text, String replacement) {
        return text.replaceAll(String.valueOf(WRAPPING_BREAK), " " + replacement);
    }

    private List<String> pdfWrap() {
        StringTokenizer tokenizer = new StringTokenizer(text, "\n");

        while (tokenizer.hasMoreTokens()) {
            processLine(tokenizer.nextToken());
        }
        return pdfLines;
    }

    private void processLine(String line) {
        StringTokenizer tokens = new StringTokenizer(line, " ");
        if (tokens.hasMoreTokens()) {
            processWords(tokens);
        }
    }

    private void processWords(StringTokenizer tokens) {
        String firstWord = tokens.nextToken();
        while(tokens.hasMoreTokens()) {
            String nextWord = tokens.nextToken();
            String combined = firstWord + " " + nextWord;
            if (cellNode.findTextLength(combined) >= cellWidth) {
                pdfLines.add(firstWord);
                firstWord = nextWord;
            } else {
                firstWord = combined;
            }
        }
        if (!firstWord.equals("")) {
            pdfLines.add(firstWord);
        }
    }

    private String wrap() {
        int totalWords = text.split("\\s").length;
        strategy = new WrappingStrategy(totalWords);

        for (char c : text.toCharArray()) {
            if (Character.isWhitespace(c)) {
                currentState.onWhitespace(c);
            }
            else {
                currentState.onNonWhitespace(c);
            }
        }
        return builder.toString();
    }

    private abstract class ParseState {
        public abstract void onWhitespace(char c);
        public abstract void onNonWhitespace(char c);
    }

    private class WhitespaceState extends ParseState {

        @Override
        public void onWhitespace(char c) {
            builder.append(c);
        }

        @Override
        public void onNonWhitespace(char c) {
            currentState = nonWhitespaceState;
            currentState.onNonWhitespace(c);
        }
    }

    private class NonWhitespaceState extends ParseState {
        @Override
        public void onWhitespace(char c) {
            wordCount++;
            if (shouldBreak(c, wordCount)) {
                builder.append(WRAPPING_BREAK);
            }
            else {
                currentState = whitespaceState;
                currentState.onWhitespace(c);
            }
        }

        private boolean shouldBreak(char c, int afterWord) {
            return strategy.shouldBreak(afterWord) || c == WRAPPING_BREAK;
        }

        @Override
        public void onNonWhitespace(char c) {
            builder.append(c);
        }
    }


    private class InitState extends ParseState {
        @Override
        public void onWhitespace(char c) {
            currentState = whitespaceState;
            currentState.onWhitespace(c);
        }

        @Override
        public void onNonWhitespace(char c) {
            currentState = nonWhitespaceState;
            currentState.onNonWhitespace(c);
        }
    }

    private static class WrappingStrategy {
        private int totalWords;

        private WrappingStrategy(int totalWords) {
            this.totalWords = totalWords;
        }

        public boolean shouldBreak(int afterWord) {
            if (totalWords < 4) {
                return  afterWord == 1;
            }
            else if (totalWords < 5) {
                return afterWord == 2;
            }
            else {
                return totalWords % 2 == 0 ? afterWord % 2 == 0 : afterWord % 2 == 1;
            }
        }
    }
}
