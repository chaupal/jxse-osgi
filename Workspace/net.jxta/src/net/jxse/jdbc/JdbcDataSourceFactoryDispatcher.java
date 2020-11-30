package net.jxse.jdbc;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.jdbc.DataSourceFactory;

public class JdbcDataSourceFactoryDispatcher {

	//public static String CLIENT_DRIVER_NAME = "org.apache.derby.jdbc.ClientDriver";
	public static final String JXTA_JDBC_CACHE_CLIENT = "net.jxse.jdbc.cache";
	

	private static JdbcDataSourceFactoryDispatcher dispatcher = new JdbcDataSourceFactoryDispatcher();
	private DataSourceFactory factory;
	
	private Map<String,String> convertor;
	
	private JdbcDataSourceFactoryDispatcher() {
		convertor = new HashMap<String, String>();
		convertor.put("org.h2.Driver", "jdbc:h2://");
		convertor.put("org.apache.derby.jdbc.ClientDriver", "jdbc:derby://");
	}

	public static JdbcDataSourceFactoryDispatcher getInstance(){
		return dispatcher;
	}
	
	public void setDataSourceFactory( DataSourceFactory factory ){
		this.factory = factory;
	}

	public void unsetDataSourceFactory( DataSourceFactory factory ){
		factory = null;
	}

	public DataSourceFactory getFactory() {
		return factory;
	}
	
	public String getDriverName(){
	   	String dataSource = System.getProperty( JXTA_JDBC_CACHE_CLIENT );
	   	return  dataSource;

	}

	public String getJndiName(){
	   	String dataSource = System.getProperty( JXTA_JDBC_CACHE_CLIENT );
	   	return  convertor.get( dataSource );
	}

}
