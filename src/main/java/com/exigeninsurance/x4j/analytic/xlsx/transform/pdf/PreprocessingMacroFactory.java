/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.utils.MacroNodeFactoryImpl;


public class PreprocessingMacroFactory extends MacroNodeFactoryImpl {
    public PreprocessingMacroFactory(XSSFSheet sheet) {
        super(sheet);
    }

    @Override
    public Node parseEvalMacro(String macro) {
        return new Node(sheet);
    }
}
