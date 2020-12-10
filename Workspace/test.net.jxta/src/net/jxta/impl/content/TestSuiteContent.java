package net.jxta.impl.content;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	EventAggregatorTest.class,
	ModuleLifecycleManagerTest.class,
	ModuleLifecycleTrackerTest.class,
	ModuleWrapperFactoryTest.class,
	TransferAggregatorTest.class
})
public class TestSuiteContent {

}
