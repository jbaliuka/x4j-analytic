package x4j.samples;
import static com.exigeninsurance.x4j.analytic.util.MockResultSet.cols;
import static com.exigeninsurance.x4j.analytic.util.MockResultSet.data;
import static com.exigeninsurance.x4j.analytic.util.MockResultSet.row;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Test;

import com.exigeninsurance.x4j.analytic.api.DefaultReportDataProvider;
import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.api.ReportDataProvider;
import com.exigeninsurance.x4j.analytic.api.X4JEngine;
import com.exigeninsurance.x4j.analytic.util.MockReportDataProvider;
import com.exigeninsurance.x4j.analytic.util.MockResultSet;

/**
 * It is X4JEngine sample implemented as JUnit test.
 * Code demonstrates typical X4J Analytic usage to implement light weight reporting solution.
 *  
 *  Samples save output to  samples/target directory
 *   
 * @author jbaliuka
 *
 */

public class X4JEngineTest {



	/**
	 * HelloWorld.xlsx template contains ${message} expression, 
	 * it should evaluate to <i>Hello World !</i> in output file
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
	 * MockData sample demonstrates mock data source for unit testing, 
	 * MockData.xlsx file contains Excel table (table name is Table1).
	 * Table should be populated from query element with the same name. 
	 * Normally query string contains SQL but it might be any script or URL to call WebService,
	 * 
	 * 
	 * This sample produces report in XLSX and PDF formats
	 */
	@Test
	public void mockData() {

		mockData("pdf");	
		mockData("xlsx");

	}

	public void mockData(String format) {

		X4JEngine engine = new X4JEngine();
		setupMockDataSource(engine);

		ReportContext context = engine.createContext("samples/MockData.xml");
		context.setOutputFormat(format);

		File  saveTo = new File("target/MockData." + format);		

		engine.transaform(context,saveTo);

	}


	/**
	 * PivotReport sample demonstrates pivot report, 
	 * it uses same mock data but one of sheets contains pivot.
	 * Pivot should refresh data from Excel table 
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
	 * Demonstrates style override without changes in template
	 * 
	 */
	@Test
	public void overrideStyles() {


		X4JEngine engine = new X4JEngine();
		engine.getStyles().add("samples/customStyles.xlsx");
		
		setupMockDataSource(engine);

		ReportContext context = engine.createContext("samples/PivotReport.xml");
		
		context.setTableStyleName("customTableStyle");
		context.setPivotStyleName("customPivotStyle");
		
		File  saveTo = new File("target/OverrideStyles.xlsx");		

		engine.transaform(context,saveTo);

	}

	/**
	 * Scripting.xlsx template contains ${reportMetadata.name} expression, 
	 * it should evaluate to report name defined Scripting.xml file 
	 * 
	 */
	@Test
	public void scripting(){

		X4JEngine engine = new X4JEngine();	

		ReportContext context = engine.createContext("samples/Scripting.xml");
		File  saveTo = new File("target/Scripting.xlsx");		

		engine.transaform(context,saveTo);
	}

	/**
	 * h2DataSource example use h2 DB connection for data access 
	 *
	 */
	@Test
	public void h2DataSource() throws Exception{

		X4JEngine engine = new X4JEngine();
		Connection connection = getConnection();
		try{
			puplateDB(connection);
			engine.setDataProvider( new DefaultReportDataProvider(connection) );

			ReportContext context = engine.createContext("samples/h2Datasource.xml");
			context.getParameters().put("top_premium", 0);

			File  saveTo = new File("target/h2Datasource.xlsx");		
			engine.transaform(context,saveTo);

			drop(connection);
		}finally{
			connection.close();
		}

	}

	/**
	 * RollupReport example demonstrates advanced #for loop and currency formatting
	 *
	 */
	@Test
	public void rollupReport() throws Exception{

		X4JEngine engine = new X4JEngine();
		Connection connection = getConnection();
		try{
			puplateDB(connection);
			engine.setDataProvider( new DefaultReportDataProvider(connection) );

			ReportContext context = engine.createContext("samples/RollupReport.xml");
			context.setOutputFormat("pdf");
			context.getParameters().put("formatter", NumberFormat.getCurrencyInstance());

			File  saveTo = new File("target/RollupReport.pdf");		
			engine.transaform(context,saveTo);

			drop(connection);
		}finally{
			connection.close();
		}

	}



	private void drop(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		try {
			statement.execute(" DROP TABLE POLICY_SUMMARY");	
		}finally{
			statement.close();
		}


	}

	private void puplateDB(Connection connection) throws SQLException {

		Statement statement = connection.createStatement();
		try {
			statement.execute(
					" CREATE TABLE POLICY_SUMMARY(        " +
							"       PRODUCT VARCHAR(255), " +
							"       POLICY CHAR(7) ,      " +
							"       STATE CHAR(2) ,       " +
							"       PREMIUM DECIMAL(9,2)  " +
							"    )                        "
					);	
		}finally{
			statement.close();
		}

		PreparedStatement pstatement = 	connection.prepareStatement(
				"INSERT INTO POLICY_SUMMARY VALUES (?,?,?,?)"
				);
		try {
			for(Object[] nextRow : data ){
				int i = 0;
				for(Object next : nextRow ){
					pstatement.setObject(++i, next);
				}
				pstatement.execute();
			}
			connection.commit();
		}finally{
			pstatement.close();
		}


	}

	private void setupMockDataSource(X4JEngine engine){
		
		ResultSet rs = MockResultSet.create(cols,data);
		ReportDataProvider dataProvider = new MockReportDataProvider(rs);
		engine.setDataProvider(dataProvider);

	}


	private Connection getConnection() throws Exception{

		return ds.getConnection();

	}

	private JdbcDataSource ds = new JdbcDataSource();
	{
		ds.setURL("jdbc:h2:mem:db");
		ds.setUser("sa");
		ds.setPassword("sa");
	}

	private String[] cols = 
			cols("PRODUCT","POLICY", "STATE","PREMIUM");
	private Object[][] data = data(
			row("Auto", "AU25636","CA",200),
			row("Home", "HO25636","CA",200),
			row("Auto", "AU12345","NY",195),
			row("Home", "HO23145","NY",186),
			row("Auto", "AU74125","CA",193),
			row("Auto", "AU74135","NM",198),
			row("Auto", "AU72135","NC",198),
			row("Auto", "AU72135","NC",198),			
			row("Auto", "AU25636","CA",200),
			row("Home", "HO29636","CA",200),
			row("Auto", "AU12745","NY",195),
			row("Home", "HO03145","NY",186),
			row("Auto", "AU70125","CA",193),
			row("Auto", "AU70135","NM",198),
			row("Auto", "AU70135","NC",198),
			row("Auto", "AU70135","NC",198),			
			row("Auto", "AU25630","CA",200),
			row("Home", "HO25630","CA",200),
			row("Auto", "AU12340","NY",195),
			row("Home", "HO23140","NY",186),
			row("Auto", "AU74120","CA",193),
			row("Auto", "AU74350","NM",198),
			row("Auto", "AU72350","NC",198),
			row("Auto", "AU72350","NC",198),			
			row("Auto", "AU25360","CA",200),
			row("Home", "HO29360","CA",200),
			row("Auto", "AU12450","NY",195),
			row("Home", "HO03450","NY",186),
			row("Auto", "AU70250","CA",193),
			row("Auto", "AU70350","NM",198),
			row("Auto", "AU70350","NC",198),
			row("Auto", "AU70350","NC",198)
			);

}



