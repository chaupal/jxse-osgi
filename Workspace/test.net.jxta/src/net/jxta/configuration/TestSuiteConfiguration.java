package net.jxta.configuration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	JxtaConfigurationTest.class,
	JxtaPeerConfigurationTest.class,
	PropertiesUtilTest.class
})
public class TestSuiteConfiguration {

}
