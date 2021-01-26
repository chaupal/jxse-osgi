package net.jxse.service;

import org.osgi.service.component.annotations.Component;

import net.jxse.service.core.IAdvertisementFactoryProvider;
import net.jxta.document.AdvertisementFactory;

@Component( name=AdvertisementFactoryProvider.S_JXSE_ADVERTISEMENT_FACTORY, immediate=true)
public class AdvertisementFactoryProvider implements IAdvertisementFactoryProvider{

	public static final String S_JXSE_ADVERTISEMENT_FACTORY = "net.jxse.service.advertisment.factory.service";

	public AdvertisementFactoryProvider() {
	}

	@Override
	public AdvertisementFactory provide() {
		return AdvertisementFactory.getInstance();
	}

}
