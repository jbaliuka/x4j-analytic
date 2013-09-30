/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.node;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


 public class Node {

	private List<Node> children;
	private final XSSFSheet sheet;
	private int id;
	
	
	public Node(XSSFSheet sheet) {
		this.sheet = sheet;
		
	}
	
	public List<Node> getChildren() {
        if(children == null){
        	children = new ArrayList<Node>();
        }
		return children;
	}
	
	public void setChildren(List<Node> children) {
		this.children = children;
	}

	public XSSFSheet getSheet() {
		return sheet;
	}

	public void process(XLXContext context) throws Exception {
		
		for(Node n : getChildren()){
			n.process(context);
		}
	}

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}

	
}
