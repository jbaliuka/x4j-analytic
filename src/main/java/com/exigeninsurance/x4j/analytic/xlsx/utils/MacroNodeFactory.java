/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.utils;

import org.apache.poi.util.Internal;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;

@Internal
public interface MacroNodeFactory {

    public Node parseSetMacro(String macro);

    public Node parseForMacro(String macro);

    public Node parseEvalMacro(String macro);

    public Node parseIfMacro(String macro);
}
