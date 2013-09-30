/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import java.util.List;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.Point;

public class PageContents {
    private List<List<Word>> lines;
    private List<ColoredRegion> regions;

    public PageContents(List<List<Word>> lines, List<ColoredRegion> rects) {
        this.lines = lines;
        regions = rects;
    }

    public boolean containsWord( String text) {
        for (List<Word> line : lines) {
            for (Word word : line) {
                if (word.toString().equals(text)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Word findWord(String value) {
        for (List<Word> line : lines) {
            for (Word word : line) {
                if (word.toString().equals(value)) {
                    return word;
                }
            }
        }
        return null;
    }

    public int getNumberOfLines() {
        return lines.size();
    }

    public List<Word> getLine(int number) {
        return lines.get(number);
    }

    public int getRegionCount() {
        return regions.size();
    }

    public ColoredRegion getRegionByLowerLeftCoords(float x, float y) {
        for (ColoredRegion region : regions) {
            Point lowerLeft = region.getRectangle().getLowerLeft();
            if (Float.compare(lowerLeft.getX(), x) == 0 && Float.compare(lowerLeft.getY(), y) == 0) {
                return region;
            }
        }
        return null;
    }
}
