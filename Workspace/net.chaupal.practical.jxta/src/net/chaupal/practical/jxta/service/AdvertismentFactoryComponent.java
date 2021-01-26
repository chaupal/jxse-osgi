package net.chaupal.practical.jxta.service;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import net.jxse.service.OsgiAdvertisementFactory;
import net.jxse.service.core.IAdvertisementFactoryProvider;

@Component( name=AdvertismentFactoryComponent.S_OSGI_ADVERTISEMENT_FACTORY, immediate=true)
public class AdvertismentFactoryComponent extends OsgiAdvertisementFactory {

	public static final String S_OSGI_ADVERTISEMENT_FACTORY = "net.chaupal.practical.jxta.service.advertisment.factory.service";

	public AdvertismentFactoryComponent() {
	}

	@Reference( cardinality = ReferenceCardinality.AT_LEAST_ONE,
			policy=ReferencePolicy.DYNAMIC)
	@Override
	public void addFactory(IAdvertisementFactoryProvider factory) {
		super.addFactory(factory);
	}

	@Override
	public void removeFactory(IAdvertisementFactoryProvider factory) {
		super.removeFactory(factory);
	}
}
