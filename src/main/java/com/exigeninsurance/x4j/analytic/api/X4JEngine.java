package com.exigeninsurance.x4j.analytic.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.exigeninsurance.x4j.analytic.model.ReportMetadata;
import com.exigeninsurance.x4j.analytic.util.CursorManager;
import com.exigeninsurance.x4j.analytic.util.ReportUtil;
import com.exigeninsurance.x4j.analytic.xlsx.transform.csv.XLSXWorkbookToCsvTransform;
import com.exigeninsurance.x4j.analytic.xlsx.transform.html.XLSXWorkbookToHTMLTransaform;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.XLSXWorkbookToPdfTransform;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLSXWorkbookTransaform;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xml.XLSXWorkbookToXMLTransform;


/**
 *  X4JEngine is facade class for {@code Transform} implementations. Application code should use  com.exigeninsurance.x4j.analytic.api.*, com.exigeninsurance.x4j.analytic.model.* and com.exigeninsurance.x4j.analytic.util.* packages, 
 *  other packages are internal and they might be changed without deprecation notice.
 *   X4JEngine implementation  is not reentrant and new instance should be used for every new report. 
 *  <p> Default input and output format is xlsx and all of resources are resolved from class path by default.
 *        
 *  {@code
 *      
 *   X4JEngine engine = new X4JEngine();
 *   ReportContext context = engine.createContext("myReportDefinition.xml");
 *   File myReportFile = new File("myReportFile.xlsx");
 *   engine.transaform(context,myReportFile);
 *    
 *  }
 *  
 *  <p> Output format might be changed using  {@code ReportContext.setOutputFormat(format) } method.
 *  
 *  {@code
 *  
 *   X4JEngine engine = new X4JEngine();
 *   engine.setResolver(new MyFileResolver());
 *   ReportContext context = engine.createContext("myReportDefinition.xml");
 *   context.setOutputFormat("pdf");
 *   File myReportFile = new File("myReportFile.pdf");
 *   engine.transaform(context,myReportFile);
 *  
 *  }
 *  
 *  <p> Normally reports are dynamic and engine depends data provider to execute SQL queries. Default data provider uses javax.sql.Datasource.
 *  Data source name is optional query attribute in report definition file but normally all of reports use default data source  
 *   e.g. {@code engine.setDataSourceName("java:comp/env/jdbc/MyDataSource"); }
 *   Custom data providers are supported by X4JEngine used too.
 * 
 * @author jbaliuka
 *
 */
public final class X4JEngine {

	 

	private class DefaultTemplateResolver implements TemplateResolver {
		@Override
		public InputStream openTemplate(String name)
				throws FileNotFoundException {		
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
			if (is == null){
				throw new FileNotFoundException(name);
			}

			return is;
		}
	}

	private class DefaultMetadataUnmarshaler implements MetadataUnmarshaler {
		@Override
		public ReportMetadata unmarshal(InputStream is) {
			try{
				ReportMetadata md =  ReportUtil.unmarshal(is);
				if (md.getParent() != null && md.getParent().trim().length() > 0  ){
					InputStream parentStream = resolver.openTemplate(md.getParent());
					try{				
						md.setPrentMetadata(unmarshal(parentStream));
					}finally{
						parentStream.close();
					}
				}

				return md;
			}catch(Exception e){
				throw new ReportException(e);
			}
		}
	}

	private Map<String,Transform> transformations = new HashMap<String, Transform>();
	private TemplateResolver resolver = new DefaultTemplateResolver();
	private MetadataUnmarshaler  unmarshaler = new DefaultMetadataUnmarshaler(); 
	private ReportDataProvider dataProvider ;
	String dataSourceName;

	{
		transformations.put("xlsx", new XLSXWorkbookTransaform());
		transformations.put("xml", new XLSXWorkbookToXMLTransform());
		transformations.put("pdf", new XLSXWorkbookToPdfTransform());
		transformations.put("html", new XLSXWorkbookToHTMLTransaform());
		transformations.put("csv", new XLSXWorkbookToCsvTransform());

	}

	

	/**
	 * Factory method resolves report definition file to create initial context.
	 * Engine should be configured before to create context
	 * @param name
	 * @return
	 */
	public ReportContext createContext(String name){

		try{
			InputStream is = resolver.openTemplate(name);
			try {
				ReportMetadata metadata = unmarshaler.unmarshal(is);

				ReportContext reportContext = new ReportContext(metadata);				
				CursorManager cursorManager = new CursorManager(reportContext, dataProvider);
				reportContext.setCursorManager(cursorManager);
				return reportContext;
			}finally {
				is.close();
			}
		}catch(Exception e ){
			throw new ReportException(e);

		}

	}
	
	

	/**
	 *  Facade method to invoke Transform implementation
	 * 
	 * @param context runtime context with current report meta data and parameters
	 * @param file output
	 */

	public void transaform(ReportContext context, File file){

		try {
			Transform t = transformations.get(context.getOutputFormat());

			if ( t == null){
				throw new ReportException("unable to find transformation for output fomat " + context.getOutputFormat());
			}

			t.setTemplateProvider(resolver);
			t.setDataProvider(dataProvider == null ?  new DefaultReportDataProvider(dataSourceName) : dataProvider );

			InputStream template = resolver.openTemplate(context.getMetadata().getTemplate());
			try {
				t.process(context, template , file);
			}finally{
				template.close();
			}
		} catch (Exception e) {
			throw new ReportException(e);
		}finally{
			context.getCursorManager().releaseManagedResources();
		}

	}

	/**
	 * Return resolver to resolve resources, resolver is typically used for templates and report definition files  
	 * @return resolver
	 */

	public TemplateResolver getResolver() {
		return resolver;
	}

	public void setResolver(TemplateResolver resolver) {
		this.resolver = resolver;
	}

	public ReportDataProvider getDataProvider() {
		return dataProvider;
	}

	public void setDataProvider(ReportDataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	/**
	 * JNDI name for javax.sql.Datasource, normally it is configured java:comp/env/jdbc context 
	 * @param dataSourceName
	 */
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public MetadataUnmarshaler getUnmarshaler() {
		return unmarshaler;
	}

	public void setUnmarshaler(MetadataUnmarshaler unmarshaler) {
		this.unmarshaler = unmarshaler;
	}





}
