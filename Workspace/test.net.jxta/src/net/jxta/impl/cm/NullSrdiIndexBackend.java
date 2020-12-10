package net.jxta.impl.cm;

import java.util.List;

import net.jxta.impl.cm.Srdi.Entry;
import net.jxta.peer.PeerID;

/**
 * Null object implementation of SrdiAPI
 */
public class NullSrdiIndexBackend implements SrdiAPI {

	@Override
	public void add(String primaryKey, String attribute, String value,
			PeerID pid, long expiration) {
		// do nothing
	}

	@Override
	public void clear() {
		// do nothing
	}

	@Override
	public void garbageCollect() {
		// do nothing
	}

	@Override
	public List<Entry> getRecord(String pkey, String skey, String value) {
		return null;
	}

	@Override
	public List<PeerID> query(String primaryKey, String attribute, String value, int threshold) {
		return null;
	}

	@Override
	public void remove(PeerID pid) {
		// do nothing
	}

	@Override
	public void stop() {
		// do nothing
	}

}
