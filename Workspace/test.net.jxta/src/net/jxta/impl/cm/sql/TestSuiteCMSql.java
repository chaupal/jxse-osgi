package net.jxta.impl.cm.sql;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	DerbyAdvertisementCacheTest.class,
	H2AdvertisementCacheTest.class,
	H2CmConcurrencyTest.class,
})
public class TestSuiteCMSql {

}
