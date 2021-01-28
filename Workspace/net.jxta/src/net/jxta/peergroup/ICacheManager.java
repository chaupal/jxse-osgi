package net.jxta.peergroup;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.jxta.document.Advertisement;
import net.jxta.util.TimeUtils;
import net.jxta.protocol.SrdiMessage.Entry;

public interface ICacheManager {

	int NO_THRESHOLD = Integer.MAX_VALUE;
	String CACHE_IMPL_SYSPROP = "net.jxta.impl.cm.cache.impl";
	/**
	 * @deprecated use {@link XIndiceAdvertisementCache#DEFAULT_GC_MAX_INTERVAL } instead
	 */
	@Deprecated
	long DEFAULT_GC_MAX_INTERVAL = 1 * TimeUtils.ANHOUR;

	List<Entry> getDeltas(String dn);

	List<Entry> getEntries(String dn, boolean clearDeltas);

	long getExpirationtime(String dn, String fn);

	InputStream getInputStream(String dn, String fn) throws IOException;

	long getLifetime(String dn, String fn);

	/**
	 * Gets the list of all the files into the given folder
	 *
	 * @param dn          contains the name of the folder
	 * @param threshold   the max number of results
	 * @param expirations List to contain expirations
	 * @return List Strings containing the name of the
	 *         files
	 */
	List<InputStream> getRecords(String dn, int threshold, List<Long> expirations);

	List<InputStream> getRecords(String dn, int threshold, List<Long> expirations, boolean purge);

	void remove(String dn, String fn) throws IOException;

	void save(String dn, String fn, Advertisement adv) throws IOException;

	void save(String dn, String fn, Advertisement adv, long lifetime, long expiration) throws IOException;

	void save(String dn, String fn, byte[] data, long lifetime, long expiration) throws IOException;

	List<InputStream> search(String dn, String attribute, String value, int threshold, List<Long> expirations);

	void setTrackDeltas(boolean trackDeltas);

	void stop();

	void garbageCollect();

	String getImplClassName();

}