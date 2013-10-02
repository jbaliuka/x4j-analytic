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
 * Code demonstrates typical X4J Analytic usage to implement light weight reporting solution 
 * @author jbaliuka
 *
 */

public class X4JEngineTest {


	private String[] cols = cols("PRODUCT","POLICY", "STATE","PREMIUM");
	private Object[][] data = data(
			row("Auto", "AU25636","CA",200),
			row("Home", "HO25636","CA",200),
			row("Auto", "AU12345","NY",195),
			row("Home", "HO23145","NY",186),
			row("Auto", "AU74125","CA",193)

			);

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

		}finally{
			connection.close();
		}



	}





	private void puplateDB(Connection connection) throws SQLException {

		Statement statement = connection.createStatement();
		try {
			statement.execute(
					"CREATE TABLE POLICY_SUMMARY(  "+
							"PRODUCT VARCHAR(255), "+
							"POLICY CHAR(7) ,      "+
							"STATE CHAR(2) ,       "+
							"PREMIUM DECIMAL(9,2)  "+
							")"
					);	
		}finally{
			statement.close();
		}

		PreparedStatement pstatement = 	connection.prepareStatement("INSERT INTO POLICY_SUMMARY VALUES (?,?,?,?)");
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



		final ResultSet rs = MockResultSet.create(cols,data);

		ReportDataProvider dataProvider = new MockReportDataProvider(rs);
		engine.setDataProvider(dataProvider);

	}


	public Connection getConnection() throws Exception{

		JdbcDataSource ds = new JdbcDataSource();
		ds.setURL("jdbc:h2:mem:db");
		ds.setUser("sa");
		ds.setPassword("sa");

		return ds.getConnection();

	}



}



