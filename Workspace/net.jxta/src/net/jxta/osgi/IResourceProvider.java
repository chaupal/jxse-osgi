package net.jxta.osgi;

import java.net.URL;

/**
 * Provides resources as a declarative service
 * @author Kees
 *
 */
public interface IResourceProvider {

	/**
	 * Get the resource with the given name
	 * @param name
	 * @return
	 */
	public URL[] getResources( String name );
}
