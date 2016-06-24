/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.util;

import static org.junit.Assert.assertTrue;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.junit.Assert;
import org.junit.Test;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.IfNode;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.utils.MacroNodeFactoryImpl;
import com.exigeninsurance.x4j.analytic.xlsx.utils.MacroParser;

public class MacroParserTest {

    @Test
    public void testMacroRecognition() {
        assertTrue(MacroParser.isMacro("#end"));
        Assert.assertFalse(MacroParser.isMacro("macro"));
        Assert.assertFalse(MacroParser.isMacro("#macro"));

        Assert.assertTrue(MacroParser.isBranchMacro("#for"));
        Assert.assertFalse(MacroParser.isLeafMacro("#for"));

        Assert.assertTrue(MacroParser.isLeafMacro("#eval"));
        Assert.assertFalse(MacroParser.isBranchMacro("#eval"));

    }

    @Test
    public void testParseValidMacro() {
        XSSFSheet sheet = null;
        MacroParser parser = new MacroParser(new MacroNodeFactoryImpl(sheet));

        Node ifNode = parser.createMacroNode("#if (1==1)");

        Assert.assertTrue(ifNode instanceof IfNode);

        Assert.assertEquals("(1==1)", ((IfNode) ifNode).getCondition().toString().trim());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseMalformedMacro() {
        XSSFSheet sheet = null;
        MacroParser parser = new MacroParser(new MacroNodeFactoryImpl(sheet));

        parser.createMacroNode("#for row in");
    }
}
