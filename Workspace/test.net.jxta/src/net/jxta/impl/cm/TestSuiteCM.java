package net.jxta.impl.cm;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	CmRandomLoadTester.class,
	CmXIndiceImplTest.class,
	IndexRandomLoadTester.class,
	InMemorySrdiConcurrencyTest.class,
	InMemorySrdiLoadTest.class,
	InMemorySrdiTest.class,
	SrdiManagerPeriodicPushTaskTest.class,
	SrdiReplicaTest.class,
	SrdiTest.class,
	XIndiceCmConcurrencyTest.class,
	XIndiceSrdiConcurrencyTest.class,
	XIndiceSrdiIndexBackendOldLoadTest.class,
	XIndiceCmTest.class,
	XIndiceSrdiLoadTest.class,
	XIndiceSrdiTest.class
})
public class TestSuiteCM {

}
