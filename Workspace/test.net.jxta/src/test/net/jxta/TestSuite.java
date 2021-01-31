package test.net.jxta;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.jxta.configuration.TestSuiteConfiguration;
import net.jxta.document.TestSuiteDocument;
import net.jxta.endpoint.TestSuiteEndpoint;
import net.jxta.id.IDTest;
import net.jxta.impl.access.always.TestSuiteAlways;
import net.jxta.impl.access.simpleACL.TestSuiteSimpleACL;
import net.jxta.impl.cm.TestSuiteCM;
import net.jxta.impl.content.TestSuiteContent;
import net.jxta.impl.content.defprovider.TestSuiteDefProvider;
import net.jxta.impl.content.srdisocket.TestSuiteSRDISocket;
import net.jxta.impl.endpoint.TestSuiteImplEndpoint;
import net.jxta.impl.endpoint.msgframing.TestSuiteWelcomeMessage;
import net.jxta.impl.endpoint.servlethttp.TestSuiteServletHttp;
import net.jxta.impl.id.UUID.TestSuiteUUID;
import net.jxta.impl.id.binaryID.TestSuiteBinaryID;
import net.jxta.impl.loader.TestSuiteRefJxtaLoader;
import net.jxta.impl.membership.pse.TestSuitePseMembership;
import net.jxta.impl.peergroup.TestSuitePeerGroup;
import net.jxta.impl.util.TestSuiteImplUtil;
import net.jxta.impl.util.pipe.reliable.TestSuiteReliable;
import net.jxta.impl.util.threads.TestSuiteThreads;
import net.jxta.impl.xindice.TestSuiteXIndice;
import net.jxta.peergroup.TestSuiteLightWeightPeerGroup;
import net.jxta.pipe.TestSuitePipe;
import net.jxta.platform.TestSuiteNetworkConfigurator;
import net.jxta.protocol.TestSuiteProtocol;
import net.jxta.rendezvous.TestSuiteRendezvous;
import net.jxta.socket.TestSuiteSocket;
import net.jxta.util.TestSuiteUtil;

@RunWith(Suite.class)
@SuiteClasses({
	TestSuiteUtil.class,
	TestSuiteImplUtil.class,
	TestSuiteConfiguration.class,
	TestSuiteDocument.class,
	TestSuiteEndpoint.class,
	IDTest.class,
	TestSuiteAlways.class,
	TestSuiteSimpleACL.class,
	TestSuiteCM.class,
//	TestSuiteCMSql.class,
	TestSuiteContent.class,
	TestSuiteDefProvider.class,
	TestSuiteSRDISocket.class,
	TestSuiteImplEndpoint.class,
	TestSuiteWelcomeMessage.class,
//	TestSuiteNetty.class,
	TestSuiteServletHttp.class,
	TestSuiteBinaryID.class,
	TestSuiteUUID.class,
	TestSuiteRefJxtaLoader.class,
	TestSuitePseMembership.class,
	TestSuitePeerGroup.class,
	TestSuiteReliable.class,
	TestSuiteThreads.class,
	TestSuiteXIndice.class,
	TestSuiteLightWeightPeerGroup.class,
	TestSuitePipe.class,
	TestSuiteNetworkConfigurator.class,
	TestSuiteProtocol.class,
	TestSuiteRendezvous.class,
	TestSuiteSocket.class
})
public class TestSuite {

}
