/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.util.Internal;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.CenterHeaderFooterSection;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.HeaderFooterSection;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.LeftHeaderFooterSection;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.RightHeaderFooterSection;

@Internal
public class SectionParser {

    private final ParseState STRING_STATE = new StringState();
    private final ParseState AMPERSAND_STATE = new AmpersandState();

    private final char LEFT_SECTION = 'L';
    private final char CENTER_SECTION = 'C';
    private final char RIGHT_SECTION = 'R';
    private final char NO_SECTION = 'N';


    private String headerFooter;
    private char sectionExpression = NO_SECTION;
    private ParseState currentState;
    private List<HeaderFooterSection> sections = new ArrayList<HeaderFooterSection>();
    private StringBuilder builder = new StringBuilder();

    public static List<HeaderFooterSection> parseSections(String contents, String headerFooter) {
        SectionParser sectionParser = new SectionParser();
        sectionParser.setHeaderFooter(headerFooter);
        return sectionParser.parse(contents);
    }

    private List<HeaderFooterSection> parse(String contents) {
        currentState = STRING_STATE;
        for (char token : contents.toCharArray()) {
            if (token == '&') {
                currentState.processAmpersand(this, token);
            }
            else {
                currentState.processCharacter(this, token);
            }
        }
        currentState.processEnd(this);
        return sections;
    }

    private interface ParseState {
        void processCharacter(SectionParser context, char token);

        void processAmpersand(SectionParser context, char token);

        void processEnd(SectionParser context);
    }

    private void addSection(String contents, char sectionExpression) {
        switch(sectionExpression) {
            case LEFT_SECTION:
                sections.add(new LeftHeaderFooterSection(contents, getHeaderFooter()));
                break;
            case CENTER_SECTION:
                sections.add(new CenterHeaderFooterSection(contents, getHeaderFooter()));
                break;
            case RIGHT_SECTION:
                sections.add(new RightHeaderFooterSection(contents, getHeaderFooter()));
                break;
        }
        builder = new StringBuilder();
    }

    private class StringState implements ParseState {

        public void processCharacter(SectionParser context, char token) {
            context.getBuilder().append(token);
        }

        public void processAmpersand(SectionParser context, char token) {
            context.setCurrentState(AMPERSAND_STATE);
        }

        public void processEnd(SectionParser context) {
            String contents = context.getBuilder().toString();
            if (noSectionsFound(context) && !(contents.equals(""))) {
                sections.add(new CenterHeaderFooterSection(contents, getHeaderFooter()));
            }
            else {
                context.addContentsToSection();
            }
        }

    }

    private boolean noSectionsFound(SectionParser context) {
        return context.getSectionExpression() == context.NO_SECTION
                && context.getSections().isEmpty();
    }

    private void addContentsToSection() {
        String contents = builder.toString();
        if (!contents.equals("")) {
            addSection(contents , sectionExpression);
        }
    }

    private class AmpersandState implements ParseState {

        public void processCharacter(SectionParser context, char token) {
            if (token == LEFT_SECTION || token == CENTER_SECTION || token == RIGHT_SECTION) {
                addContentsToSection();
                context.setSectionExpression(token);
            }
            else {
                context.getBuilder().append("&").append(token);
            }
            context.setCurrentState(STRING_STATE);
        }

        public void processAmpersand(SectionParser context, char token) {
            context.getBuilder().append(token);

        }

        public void processEnd(SectionParser context) {
            context.getBuilder().append("&");
        }

    }



    private StringBuilder getBuilder() {
        return builder;
    }

    private char getSectionExpression() {
        return sectionExpression;
    }

    private void setSectionExpression(char sectionExpression) {
        this.sectionExpression = sectionExpression;
    }

    private void setCurrentState(ParseState currentState) {
        this.currentState = currentState;
    }

    private List<HeaderFooterSection> getSections() {
        return sections;
    }

    public String getHeaderFooter() {
        return headerFooter;
    }

    public void setHeaderFooter(String headerFooter) {
        this.headerFooter = headerFooter;
    }
}
