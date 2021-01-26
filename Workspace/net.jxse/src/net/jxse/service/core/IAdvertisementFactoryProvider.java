package net.jxse.service.core;

import net.jxta.document.AdvertisementFactory;

public interface IAdvertisementFactoryProvider {

	/**
	 * Provide the advertisement factory
	 * @return
	 */
	public AdvertisementFactory provide();
}
