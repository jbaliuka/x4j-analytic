/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.utils;

import org.apache.poi.util.Internal;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;

@Internal
public interface MacroNodeFactory {

    Node parseSetMacro(String macro);

    Node parseForMacro(String macro);

    Node parseEvalMacro(String macro);

    Node parseIfMacro(String macro);
}
