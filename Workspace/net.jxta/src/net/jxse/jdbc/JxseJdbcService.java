package net.jxse.jdbc;

import net.jxse.jdbc.DataSourceServiceTracker;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.util.tracker.ServiceTracker;

public class JxseJdbcService implements BundleActivator {

	private ServiceTracker<DataSourceFactory, DataSourceFactory> dsfTracker = null;
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		dsfTracker = new ServiceTracker<DataSourceFactory, DataSourceFactory>(context,
				DataSourceFactory.class.getName(),
				new DataSourceServiceTracker(context));
		dsfTracker.open();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext vontext) throws Exception {
		dsfTracker.close();
	}
}
