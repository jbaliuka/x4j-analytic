/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.utils;

import java.util.StringTokenizer;

import org.apache.poi.util.Internal;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.xlsx.core.expression.SimpleExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.EvalNode;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.ForEachNode;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.IfNode;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.SetNode;

@Internal
public class MacroNodeFactoryImpl implements MacroNodeFactory {

    protected XSSFSheet sheet;

    public MacroNodeFactoryImpl(XSSFSheet sheet) {
        this.sheet = sheet;
    }

    public Node parseSetMacro(String macro) {
        StringTokenizer tokens = new StringTokenizer(macro, " \t\n\r=");
        tokens.nextToken();
        if (tokens.hasMoreTokens()) {
            String contextVariableName = tokens.nextToken();
            String expressionString = getRemainingTokens(tokens);
            XLSXExpression expression = new SimpleExpression(expressionString);
            return new SetNode(sheet, contextVariableName, expression);
        }
        throw new IllegalArgumentException("Failed to parse for macro: " + macro);
    }

    public Node parseForMacro(String macro) {
        StringTokenizer tokens = new StringTokenizer(macro," \t\n\r");
        tokens.nextToken();
        if(tokens.hasMoreTokens()){
            return createForEachNode(tokens);
        }
        throw new IllegalArgumentException("Failed to parse for macro: " + macro);
    }

    private Node createForEachNode(StringTokenizer tokens) {
        String var = tokens.nextToken();
        Node forEachNode = null;
        if(tokens.hasMoreTokens()){
            forEachNode = parseIn(tokens, var);
        }
        return forEachNode;
    }

    private Node parseIn(StringTokenizer tokens, String var) {
        String in = tokens.nextToken();
        if("in".equals(in) && tokens.hasMoreTokens()){
            String expr = tokens.nextToken();
            String groupingColumn = parseGroupingColumn(tokens);
            String dataObject = parseGroupDataObject(tokens);
            ForEachNode forNode = new ForEachNode(sheet);
            forNode.setVar(var);
            forNode.setRows(new SimpleExpression(expr));
            forNode.setGroupingColumn(groupingColumn);
            forNode.setGroupDataObject(dataObject);
            return forNode;
        }
        throw new IllegalArgumentException("Incorrectly formatted #for macro");
    }

    private String parseGroupingColumn(StringTokenizer tokens) {
        String groupingColumn = null;
        if (tokens.hasMoreElements()) {
            tokens.nextToken(); // group
            tokens.nextToken(); // by
            groupingColumn = tokens.nextToken();
        }
        return groupingColumn;
    }

    private String parseGroupDataObject(StringTokenizer tokens) {
        String dataObject = null;
        if (tokens.hasMoreElements()) {
            tokens.nextToken(); // as
            dataObject = tokens.nextToken();
        }
        return dataObject;
    }

    public Node parseEvalMacro(String macro) {
        StringTokenizer tokens = new StringTokenizer(macro, " \t\n\r");
        tokens.nextToken();
        if (tokens.hasMoreTokens()) {
            String expressionString = getRemainingTokens(tokens);
            XLSXExpression expression = new SimpleExpression(expressionString);
            return new EvalNode(sheet, expression);
        }
        throw new IllegalArgumentException("Could not parse malformed eval macro");
    }

    private String getRemainingTokens(StringTokenizer tokens) {
        StringBuilder builder = new StringBuilder();
        while (tokens.hasMoreTokens()) {
            builder.append(tokens.nextToken());
        }
        return builder.toString();
    }

    public Node parseIfMacro(String macro) {
        IfNode ifNode = new IfNode(sheet);
        SimpleExpression expr = new SimpleExpression(macro.substring(3));
        ifNode.setCondition(expr);
        return ifNode;
    }
}
