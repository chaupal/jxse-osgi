package net.jxta.endpoint;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	ByteArrayMessageElementTest.class,
	EndpointAddressTest.class,
	InputStreamMessageElementTest.class,
	MessageTest.class,
	MessengerStateBarrierTest.class,
	MessengerStateListenerListTest.class,
	//SerializationPerformanceTest.class,
	StringMessageElementTest.class
})
public class TestSuiteEndpoint {

}
