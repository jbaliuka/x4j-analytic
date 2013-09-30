/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


public class ChildProcessor implements Processor {

    private Node node;

    public ChildProcessor(Node node) {
        this.node = node;
    }

    @Override
    public void process(XLXContext context) throws Exception {
        for (Node child : node.getChildren()) {
            child.process(context);
        }
    }
}
