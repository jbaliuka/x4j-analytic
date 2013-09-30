package com.exigeninsurance.x4j.analytic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Test;

import com.exigeninsurance.x4j.analytic.model.ReportMetadata;
import com.exigeninsurance.x4j.analytic.model.Script;
import com.exigeninsurance.x4j.analytic.util.ReportUtil;
import com.exigeninsurance.x4j.analytic.util.StringUtils;

public class ReportUtilTest{

	private final ReportMetadata mdata = new ReportMetadata();

	{

		Script script = new Script();
		script.setName("script");
		script.setText("2+2");
		mdata.getSelfScript().add(script);

		Script params = new Script();
		params.setName("params");
		params.setText("a+b");
		mdata.getSelfScript().add(params);


		script = new Script();
		script.setName("quarter");
		script.setText(	"(new( \"java.util.Date\",2000,11,1).getMonth() / 3) + 1");
		mdata.getSelfScript().add(script);


	}

	

	
	@Test
	public void testEquals(){

		assertTrue(StringUtils.equals(null, null));
		assertTrue(StringUtils.equals("", ""));
		assertFalse(StringUtils.equals(null, ""));
		assertFalse(StringUtils.equals("", null));
		assertFalse(StringUtils.equals("A", "B"));
		assertFalse(StringUtils.equals("B", "A"));

	}

	

	@Test
	public void testUnmarshal() {
		InputStream is = this.getClass().getResourceAsStream("/TestData.xml");
		ReportMetadata metadata = ReportUtil.unmarshal(is);

		String name = metadata.getName();
		String expectedName = "Parent";

		assertEquals(expectedName, name);
	}

	


}
