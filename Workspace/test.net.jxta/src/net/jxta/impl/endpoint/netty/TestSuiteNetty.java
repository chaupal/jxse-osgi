package net.jxta.impl.endpoint.netty;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	InetSocketAddressTranslatorTest.class,
	JxtaProtocolHandlerTest.class,
	MessageDispatchHandlerTest.class,
	NettyTransportClientTest.class,
	NettyTransportServerTest.class
})
public class TestSuiteNetty {

}
