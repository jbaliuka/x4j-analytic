package com.exigeninsurance.x4j.analytic.api;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.exigeninsurance.x4j.analytic.model.Query;


/**
 * 
 * X4JENgine uses DefaultReportDataProvider to populate Excel tables with query results,
 * queries should be declared in report definition xml file 
 * 
 * @author jbaliuka
 *
 */
public class DefaultReportDataProvider implements ReportDataProvider {

	private static Pattern paramPattern = Pattern.compile(":(\\w+)");
	/**
	 * 
	 */
	private String dataSourceName;
	private Connection connection;

	/**
	 * Public constructor to create default data provider instance, it uses data source configured via JNDI,
	 * Default implementation does not start or end internal transactions 
	 * @param dataSourceName
	 */
	public DefaultReportDataProvider(String  dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	/**
	 * Public constructor to create default  instance with user provided connection, user provided connections are never closed by engine,
	 * caller should open and close provided connection
	 * @param dataSourceName
	 */
	public DefaultReportDataProvider(Connection  connection) {
		this.connection = connection;
	}

	@Override
	public void execute(Query query, ReportContext context,
			ReportDataCallback callback) throws SQLException {

		try {
			if(connection == null){
				useDataSource(query, context, callback);
			}else {
				execute(query, context, callback, connection);
			}
		} catch (Exception e) {
			throw new ReportException(e);
		}

	}

	private void execute(Query query, ReportContext context,
			ReportDataCallback callback, Connection conn)
					throws SQLException, Exception {

		CallableStatement call = conn.prepareCall(query.getSql());
		try {

			setParameters(context,query, call);
			ResultSet rs = call.executeQuery();
			try {
				callback.process(rs);
			}finally {
				rs.close();
			}

		}finally{
			call.close();
		}
	}

	private void useDataSource(Query query, ReportContext context,
			ReportDataCallback callback) throws Exception {
		if(query.getDataSource() == null && dataSourceName == null){
			throw new ReportException("no data source name is provided");
		}

		InitialContext init = new InitialContext();
		try {
			DataSource ds = lookup(query, init);
			Connection conn = ds.getConnection();
			try {
				execute(query, context, callback, conn);
			}finally{
				conn.close();
			}

		}finally{
			init.close();
		}

	}

	/**
	 * Returns DataSource for query
	 * 
	 * @param query
	 * @param init
	 * @return default if query meta data does not specify data source name 
	 * @throws NamingException
	 */
	protected DataSource lookup(Query query, InitialContext init)
			throws NamingException {
		return (DataSource) init.lookup(query.getDataSource() == null ? dataSourceName : query.getDataSource());
	}

	/**
	 * Searches parameter in SQL query, parameter name should match regular expression :\w+,
	 * Custom implementation  might use JDBC parameters ? 
	 * 
	 * @param query is any string but normally it should be the SQL query
	 * @return
	 */

	protected Collection<String> getParameterNames(String query){

		Collection<String> results = new ArrayList<String>();		
		Matcher matcher = paramPattern.matcher(query);

		while(matcher.find()){
			results.add(matcher.group(1));
		}

		return results;

	}

	/**
	 * 
	 * Default implementation uses named parameters and it performs no type conversion,
	 * It might be useful to override this method to workaround driver specific issues
	 * 
	 * @param context
	 * @param query
	 * @param call
	 * @throws SQLException
	 */

	protected void setParameters(ReportContext context,Query query, CallableStatement call)
			throws SQLException {

		for( String name : getParameterNames(query.getSql())){
			Object value = context.getParameters().get(name);
			if(value != null){
				call.setObject(name,value );
			}else {
				call.setNull(name, Types.OTHER);
			}
		}
	}
}