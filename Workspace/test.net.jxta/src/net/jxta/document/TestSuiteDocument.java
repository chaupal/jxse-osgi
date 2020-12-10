package net.jxta.document;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	AdvertisementTest.class,
	AdvertisementSerializableTest.class,
	DocumentTest.class,
	MimeMediaTypeTest.class
})
public class TestSuiteDocument {

}
