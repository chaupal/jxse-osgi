package net.jxta.module;

import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.core.IJxtaLoader;
import net.jxta.peergroup.core.Module;

public interface IJxtaModuleFactory<T extends Module> extends
	Module, IModuleManager<T> {

	/**
	 * The peer group that owns the moodule manager
	 * @return
	 */
	public PeerGroup getPeergroup();

	IJxtaLoader getLoader();
}
