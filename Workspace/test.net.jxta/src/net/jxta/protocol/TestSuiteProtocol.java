package net.jxta.protocol;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	DiscoveryQueryMsgTest.class,
	DiscoveryResponseMsgTest.class,
	GroupConfigTest.class,
	PeerAdvertisementTest.class,
	PipeAdvertisementTest.class,
	RdvAdvertisementTest.class,
	ResolverMsgTest.class,
	ResolverQueryMsgTest.class,
	ResolverResponseMsgTest.class,
	ResolverSrdiMsgTest.class,
	SignedAdvertisementTest.class,
	SrdiMessageTest.class
})
public class TestSuiteProtocol {

}
