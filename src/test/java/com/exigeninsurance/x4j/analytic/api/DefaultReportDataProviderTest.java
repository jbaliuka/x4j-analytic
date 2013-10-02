package com.exigeninsurance.x4j.analytic.api;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

public class DefaultReportDataProviderTest {

	@Test
	public void testGetParameterNames() {
		
		DefaultReportDataProvider provider = new DefaultReportDataProvider("test");
		
		Collection<String> actual = provider.getParameterNames(":test_param :1 \n(:2):3><");
		
		Assert.assertEquals(Arrays.asList("test_param","1","2","3"), actual);
		
		
	}

}
