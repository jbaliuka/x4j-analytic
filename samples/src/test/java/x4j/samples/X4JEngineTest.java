package x4j.samples;
import static com.exigeninsurance.x4j.analytic.util.MockResultSet.cols;
import static com.exigeninsurance.x4j.analytic.util.MockResultSet.data;
import static com.exigeninsurance.x4j.analytic.util.MockResultSet.row;

import java.io.File;
import java.sql.ResultSet;

import org.junit.Test;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.api.ReportDataProvider;
import com.exigeninsurance.x4j.analytic.api.X4JEngine;
import com.exigeninsurance.x4j.analytic.util.MockReportDataProvider;
import com.exigeninsurance.x4j.analytic.util.MockResultSet;

/**
 * It is X4JEngine sample implemented as JUnit test.
 * Code demonstrates typical X4J Analytic usage to implement light weight reporting solution 
 * @author jbaliuka
 *
 */

public class X4JEngineTest {
	
	
	/**
	 * HelloWorld.xlsx template contains ${message} expression, it should evaluate to Hello World ! in output file
	 * 
	 */
	@Test
	public void helloWorld(){
		
		X4JEngine engine = new X4JEngine();	
		
		ReportContext context = engine.createContext("samples/HelloWorld.xml");
		File  saveTo = new File("target/HelloWorld.xlsx");		
		
		
		context.getParameters().put("message", "Hello World !");
		engine.transaform(context,saveTo);
	}

	/**
	 * This sample demonstrates mock data source, MockData.xlsx contains Excel table with name Table1.
	 * Table should be populated from query element with the same name. 
	 * Normally query string contains SQL but it might be any script or URL to call WebService 
	 * 
	 * This sample produces report in XLSX and PDF formats
	 */
	@Test
	public void mockData() {
		
		mockData("pdf");	
		mockData("xlsx");
			
		
		
	}
	
	private void mockData(String format) {
		
		X4JEngine engine = new X4JEngine();
		setupMockDataSource(engine);
		
		ReportContext context = engine.createContext("samples/MockData.xml");
		context.setOutputFormat(format);
		
		File  saveTo = new File("target/MockData." + format);		
		
		engine.transaform(context,saveTo);
		
	}
	
	
	/**
	 * This sample demonstrates pivot report, it uses same mock data but one of sheets contains pivot.
	 * Pivot should refresh data from Excel table and refresh should be configured in Pivot Options dialog 
	 */
	@Test
	public void pivotReport() {
		
		
		X4JEngine engine = new X4JEngine();
		setupMockDataSource(engine);
		
		ReportContext context = engine.createContext("samples/PivotReport.xml");
		File  saveTo = new File("target/PivotReport.xlsx");		
		
		engine.transaform(context,saveTo);
		
	}
	
	/**
	 * Scripting.xlsx template contains ${reportMetadata.name} expression, it should evaluate to report name defined Scripting.xml file 
	 * 
	 */
	@Test
	public void scripting(){
		
		X4JEngine engine = new X4JEngine();	
		
		ReportContext context = engine.createContext("samples/Scripting.xml");
		File  saveTo = new File("target/Scripting.xlsx");		
		
		engine.transaform(context,saveTo);
	}
	
	
	private void setupMockDataSource(X4JEngine engine){
		
		final ResultSet rs = MockResultSet.create(
	            cols("product","policy", "state","premium"),
	            data(
	            		row("Auto", "AU25636","CA",200),
	                    row("Home", "HO25636","CA",200),
	                    row("Auto", "AU12345","NY",195),
	                    row("Home", "HO23145","NY",186),
	                    row("Auto", "AU74125","CA",193)
	                    
	            )
	    );
		
		ReportDataProvider dataProvider = new MockReportDataProvider(rs);
		engine.setDataProvider(dataProvider);
		
	}
	

}
