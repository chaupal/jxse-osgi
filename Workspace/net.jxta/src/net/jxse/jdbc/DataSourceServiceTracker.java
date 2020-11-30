package net.jxse.jdbc;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * The DataSourceServiceTracker does the work to populate the DB.  It listens for
 * a service that can allow us to connect to our Derby DB and then uses that service to
 * create a connection and populate the database.
 */
public class DataSourceServiceTracker implements ServiceTrackerCustomizer<DataSourceFactory, DataSourceFactory> {

	private BundleContext context;
	
	private JdbcDataSourceFactoryDispatcher dispatcher = JdbcDataSourceFactoryDispatcher.getInstance();
	
	public DataSourceServiceTracker(BundleContext context){
		this.context = context;
	}
	
	/**
	 * This method will handle the majority of the work done by this bundle
	 * It check to ensure the service we get is the one we are looking for, it will
	 * create a Datasource factory and use that factory to get a connection
	 */
	@Override
	public DataSourceFactory addingService(ServiceReference<DataSourceFactory> ref) {

		Bundle b = ref.getBundle();
		DataSourceFactory service = b.getBundleContext().getService(ref);
		
		String driver = (String)ref.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_CLASS);
		if (driver!= null) {
			System.out.println("Test notified of service: " + driver);
		}
 		DataSourceFactory dsf = lookupDsf( dispatcher.getDriverName() );
		dispatcher.setDataSourceFactory(dsf);
		return service; 
	}
	
	/**
	 * This method handles the lookup of the correct datasource factory
	 * It uses a filter to obtain a reference to the correct DataSourceFactory
	 * and returns it.
	 * @param driverName
	 * @param version
	 * @return
	 */
	protected DataSourceFactory lookupDsf(String driverName) {
		System.out.println("Lookup (" + driverName + ")");
		String filter =
				"(&("+DataSourceFactory.OSGI_JDBC_DRIVER_CLASS+"="+driverName+"))";
		ServiceReference<?>[] refs = null;
		try {
			refs = context.getServiceReferences(DataSourceFactory.class.getName(), filter); 
		} catch (InvalidSyntaxException isEx) {
			new RuntimeException("Bad filter", isEx);
		}
		System.out.println("DSF Service refs looked up from registry: " + refs);
		return (refs == null) ? null : (DataSourceFactory) context.getService(refs[0]);
	}
	
	/**
	 * This method is not implemented by this simple demo as all the functionality from this demo occurs
	 * as soon as the service is available.
	 */
	@Override
	public void modifiedService(ServiceReference<DataSourceFactory> reference, DataSourceFactory service) {	
		System.out.println("Modifies " + service.getClass().getName() );
	}

	/**
	 * This method is not implemented by this simple demo as all the functionality from this demo occurs
	 * as soon as the service is available.
	 */
	@Override
	public void removedService(ServiceReference<DataSourceFactory> reference, DataSourceFactory service) {
		System.out.println("Removed " + service.getClass().getName() );
	}	
}
