package test.net.jxta.standalone;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import test.net.jxta.standalone.test.TestSuite;

public class Activator implements BundleActivator {

	private static BundleContext context;

	private TestSuite suite = TestSuite.getInstance();
	
	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		suite.runTest();
		
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
