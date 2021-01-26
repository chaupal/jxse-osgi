package net.jxse.service;

import java.util.HashMap;
import java.util.Map;

import net.jxse.service.core.IAdvertisementFactoryProvider;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.AdvertisementFactory.Instantiator;
import net.jxta.util.ClassFactory;

public class OsgiAdvertisementFactory extends ClassFactory<String, AdvertisementFactory.Instantiator>{

	private Map<IAdvertisementFactoryProvider, AdvertisementFactory> factories;
	
	protected OsgiAdvertisementFactory() {
		super();
		factories = new HashMap<>();
	}

	public void addFactory( IAdvertisementFactoryProvider factory) {
		this.factories.put(factory, factory.provide());
	}

	public void removeFactory( IAdvertisementFactoryProvider factory) {
		this.factories.remove(factory);
	}

	@Override
	protected Map<String, Instantiator> getAssocTable() {
		Map<String, Instantiator> results = new HashMap<>();
		for( AdvertisementFactory factory: factories.values() ) {
			results.putAll( factory.getAssocTable());
		}
		return results;
	}

    /**
     *  {@inheritDoc}
     */
    @Override
    public Class<Instantiator> getClassOfInstantiators() {
        // our key is the doctype names.
        return Instantiator.class;
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public Class<String> getClassForKey() {
        // our key is the doctype names.
        return java.lang.String.class;
    }
}
