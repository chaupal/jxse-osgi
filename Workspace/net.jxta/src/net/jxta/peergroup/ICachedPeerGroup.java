package net.jxta.peergroup;


public interface ICachedPeerGroup extends PeerGroup{

	/**
	 * @return the cache manager associated with this group.
	 */
	public abstract ICacheManager getCacheManager();

}