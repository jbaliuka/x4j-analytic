/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MergedRegionTest {
	
	@Test
	public void testMergedParsing() {
		String regionString = "A1:B2";
		MergedRegion region = new MergedRegion(regionString);
		assertEquals(regionString, region.toString());
	}
	
	@Test
	public void testDeriving() {
		String regionString = "A1:B2";
		MergedRegion region = new MergedRegion(regionString);
		MergedRegion derived = region.derive(3);
		assertEquals("A3:B4", derived.toString());
	}
}