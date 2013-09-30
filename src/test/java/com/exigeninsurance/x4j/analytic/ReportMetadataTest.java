package com.exigeninsurance.x4j.analytic;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.exigeninsurance.x4j.analytic.model.Attribute;
import com.exigeninsurance.x4j.analytic.model.Format;
import com.exigeninsurance.x4j.analytic.model.Parameter;
import com.exigeninsurance.x4j.analytic.model.Query;
import com.exigeninsurance.x4j.analytic.model.ReportMetadata;
import com.exigeninsurance.x4j.analytic.model.Templates;

public class ReportMetadataTest {

	private ReportMetadata metadata;

	@Before
	public void setUp() throws Exception {
		metadata = new ReportMetadata();
	}

	@Test
	public void testTemplateFormat(){
		metadata.setTemplate("test.xlsx");
		assertThat(metadata.getTemplateFormat("PDF"), equalTo("xlsx"));
	}
	
	@Test
	public void testSupportedFormatParsing() {
		metadata.setFormats("PDF,excel,html");
		assertThat(metadata.parseFormats(), hasSize(3));
	
		metadata.setFormats("PDF ,excel, html");
		assertTrue(metadata.parseFormats().containsAll(Arrays.asList("pdf", "html", "excel")));
	}

	@Test
	public void parsingEmptySupportedFormatString_shouldReturnEmptyList() {
		metadata.setFormats("");
		assertThat(metadata.parseFormats(), hasSize(0));
	}

	@Test
	public void formatIncludedInSupportedFormats_isSupported() {
		metadata.setFormats("html");
		assertThat(metadata.supports("html"), is(true));
	}

	@Test
	public void formatNotIncludedInSupportedFormats_isNotSupported() {
		assertThat(metadata.supports("excel"), is(false));
	}

	@Test
	public void testParameterByName(){
		Parameter p = new Parameter();
		p.setName("test");
		p.setDefaultValue("default");
		List<Parameter> list = new ArrayList<Parameter>();
		list.add(p);
		metadata.setParameter(list);
		
		assertEquals("default", metadata.getParameterByName("test").getDefaultValue());
	}

	@Test
	public void testBuildNumber(){
		Attribute a = new Attribute();
		a.setName(ReportMetadata.VERSION);
		a.setValue("1");
		List<Attribute> list = new ArrayList<Attribute>();
		list.add(a);
		metadata.setAttribute(list);
		
		assertEquals("1", metadata.getBuildNumber());
	}

	
	@Test(expected = IllegalArgumentException.class)  
	public void testDelveryError(){
		metadata.setDelivery("printer");
	}
	
	@Test	
	public void testDelivery(){
		metadata.setDelivery("efolder,mail");
		assertTrue(metadata.deliverable("mail"));
		assertTrue(metadata.deliverable("efolder"));
		assertFalse(metadata.deliverable("printer"));
	}

	@Test
	public void testDeliveryWithWhitespace() {
		metadata.setDelivery("efolder    , mail ");
		assertThat(metadata.deliverable("efolder"), is(true));
		assertThat(metadata.deliverable("mail"), is(true));
	}
	
	@Test
	public void testQueryByName(){
		Query q = new Query();
		q.setName("test");
		q.setSql("SELECT");
		List<Query> list = new ArrayList<Query>();
		list.add(q);
		metadata.setQuery(list);

		assertEquals("SELECT", metadata.getQueryByName("test").getSql());
	}
	
	@Test
	public void formatIncludedInOverridingTemplates_isSupported() {
		String template = "template";
		String outputFormat = "PDF";

		Templates templates = new Templates();
		List<Format> formats = new ArrayList<Format>();
		Format format = new Format();
		format.setName(outputFormat);
		format.setTemplate(template);
		formats.add(format);
		templates.setFormat(formats );
		metadata.setTemplates(templates);
		
		assertTrue(metadata.supports(outputFormat));
		assertEquals(template, metadata.getTemplate(outputFormat));
	}
}
