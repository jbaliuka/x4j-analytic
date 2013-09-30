/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform;

import org.apache.poi.xssf.usermodel.XSSFPictureData;


public class Picture {
	
	private XSSFPictureData pictureData;
	private String id;
	private int fromRow;
	private int fromCol;
	private int toRow;
	private int toCol;
    private float xOffset;
    private float yOffset;
    private int emuWidth;
    private int emuHeight;
	
	

	public Picture(XSSFPictureData pictureData, String id, int fromRow,
			int fromCol, int toRow, int toCol) {
		super();
		this.pictureData = pictureData;
		this.id = id;
		this.fromRow = fromRow;
		this.fromCol = fromCol;
		this.toRow = toRow;
		this.toCol = toCol;
	}

	public XSSFPictureData getPictureData() {
		return pictureData;
	}

	public void setPictureData(XSSFPictureData pictureData) {
		this.pictureData = pictureData;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getFromRow() {
		return fromRow;
	}

	public void setFromRow(int fromRow) {
		this.fromRow = fromRow;
	}

	public int getFromCol() {
		return fromCol;
	}

	public void setFromCol(int fromCol) {
		this.fromCol = fromCol;
	}

	public int getToRow() {
		return toRow;
	}

	public void setToRow(int toRow) {
		this.toRow = toRow;
	}

	public int getToCol() {
		return toCol;
	}

	public void setToCol(int toCol) {
		this.toCol = toCol;
	}

    public float getxOffset() {
        return xOffset;
    }

    public void setxOffset(float xOffset) {
        this.xOffset = xOffset;
    }

    public float getyOffset() {
        return yOffset;
    }

    public void setyOffset(float yOffset) {
        this.yOffset = yOffset;
    }

    public int getEmuWidth() {
        return emuWidth;
    }

    public void setEmuWidth(int emuWidth) {
        this.emuWidth = emuWidth;
    }

    public int getEmuHeight() {
        return emuHeight;
    }

    public void setEmuHeight(int emuHeight) {
        this.emuHeight = emuHeight;
    }
}
