
/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.xlsx.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.poi.util.Internal;

import com.exigeninsurance.x4j.analytic.api.ReportException;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;

@Internal
public class MacroParser {

    private static final Set<String> ALL_MACROS;
    private static final Set<String> BRANCH_MACROS;
    private static final Set<String> LEAF_MACROS;

    private static final String END_MACRO = "#end";
    private static final String FOR_MACRO = "#for";
    private static final String IF_MACRO = "#if";
    private static final String TABLE_MACRO = "#table";
    private static final String SET_MACRO = "#set";
    private static final String EVAL_MACRO = "#eval";

    private final MacroNodeFactory nodeFactory;

    static {
        BRANCH_MACROS = new HashSet<String>(Arrays.asList(new String[]{
                FOR_MACRO,
                IF_MACRO
        }));
        LEAF_MACROS = new HashSet<String>(Arrays.asList(new String[]{
                SET_MACRO,
                EVAL_MACRO
        }));
        ALL_MACROS = new HashSet<String>(BRANCH_MACROS);
        ALL_MACROS.addAll(LEAF_MACROS);
        ALL_MACROS.add(END_MACRO);
        ALL_MACROS.add(TABLE_MACRO);
    }

    public MacroParser(MacroNodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    public static boolean isMacro(String macro) {
        if (!containsMacroIdentifier(macro)) {
            return false;
        }
        return ALL_MACROS.contains(parseMacroIdentifier(macro));
    }

    public static boolean isEndMacro(String macro) {
        return END_MACRO.equals(macro.trim());
    }

    public static boolean isBranchMacro(String macro) {
        if (!containsMacroIdentifier(macro)) {
            return false;
        }
        return BRANCH_MACROS.contains(parseMacroIdentifier(macro));
    }

    public static boolean isLeafMacro(String macro) {
        if (!containsMacroIdentifier(macro)) {
            return false;
        }
        return LEAF_MACROS.contains(parseMacroIdentifier(macro));
    }

    public Node createMacroNode(String text) {
        validate(text);
        return parseMacro(parseMacroIdentifier(text), text);
    }

    private static boolean containsMacroIdentifier(String macro) {
        StringTokenizer tokenizer = new StringTokenizer(macro, " \t\n\r");
        if (tokenizer.hasMoreTokens()) {
            String firstToken = tokenizer.nextToken();
            if (firstToken.startsWith("#")) {
                return true;
            }
        }
        return false;
    }

    private static String parseMacroIdentifier(String text) {
        StringTokenizer tokenizer = new StringTokenizer(text, " (\t\n\r");
        return tokenizer.nextToken();
    }

    private void validate(String macro) {
        if (!isMacro(macro)) {
            throw new IllegalArgumentException("Macro not supported: " + macro);
        }
        if (!(isBranchMacro(macro) || isLeafMacro(macro))) {
            throw new IllegalArgumentException("Cannot create node for macro: " + macro);
        }
    }

    private Node parseMacro(String identifier, String macro) {
        if (identifier.equals(IF_MACRO)) {
            return parseIfMacro(macro);
        }
        if (identifier.equals(EVAL_MACRO)) {
            return parseEvalMacro(macro);
        }
        if (identifier.equals(FOR_MACRO)) {
            return parseForMacro(macro);
        }
        if (identifier.equals(SET_MACRO)) {
            return parseSetMacro(macro);
        }
        throw new ReportException("Failed to parse macro: " + macro);
    }

    private Node parseSetMacro(String macro) {
        return nodeFactory.parseSetMacro(macro);
    }

    private Node parseForMacro(String macro) {
        return nodeFactory.parseForMacro(macro);
    }

    private Node parseEvalMacro(String macro) {
        return nodeFactory.parseEvalMacro(macro);
    }

    private Node parseIfMacro(String macro) {
        return nodeFactory.parseIfMacro(macro);
    }


}
