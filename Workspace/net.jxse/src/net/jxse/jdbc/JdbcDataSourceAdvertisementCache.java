package net.jxse.jdbc;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.ConnectionPoolDataSource;

import org.osgi.service.jdbc.DataSourceFactory;

import net.jxta.impl.cm.sql.JdbcAdvertisementCache;
import net.jxta.util.threads.TaskManager;

public class JdbcDataSourceAdvertisementCache extends JdbcAdvertisementCache {
	
	public JdbcDataSourceAdvertisementCache(URI storeRoot, String areaName, TaskManager taskManager) throws IOException {
		super(storeRoot, areaName, taskManager);
	}
	
	public JdbcDataSourceAdvertisementCache(URI storeRoot, String areaName, TaskManager taskManager, long gcinterval, boolean trackDeltas) throws IOException {
		super(storeRoot, areaName, taskManager, gcinterval, trackDeltas);
	}
	
	/**
	 * Returns true if a JDBC datasource factory has been registered
	 * @return
	 */
	public static boolean hasJdbcdataSource(){
		JdbcDataSourceFactoryDispatcher dispatcher = JdbcDataSourceFactoryDispatcher.getInstance();
		return ( dispatcher.getFactory() != null );
	}
	
	@Override
	protected ConnectionPoolDataSource createDataSource() {
		JdbcDataSourceFactoryDispatcher dispatcher = JdbcDataSourceFactoryDispatcher.getInstance();
		DataSourceFactory factory = dispatcher.getFactory();
		if( factory == null )
			return null;
		try {
			return factory.createConnectionPoolDataSource( getUrlProperties( dispatcher.getJndiName()));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Return the DB properties to use for the connection
	 * @return
	 */
	private Properties getUrlProperties( String driverName ) {
		Properties props = new Properties();
		//"props.put(DataSourceFactory.OSGI_JDBC_DRIVER_NAME, driverName );
		props.put(DataSourceFactory.JDBC_URL.toUpperCase(), driverName + super.dbDir.toURI() );
		//props.put(DataSourceFactory.JDBC_USER, "app");
		//props.put(DataSourceFactory.JDBC_PASSWORD, "app");
		return props;
	}

	@Override
	protected void shutdownDb() throws SQLException {
		// TODO Auto-generated method stub
		
	}

}
