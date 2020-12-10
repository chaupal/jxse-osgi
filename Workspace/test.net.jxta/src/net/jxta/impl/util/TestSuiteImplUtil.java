package net.jxta.impl.util;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	BASE64Test.class,
	LRUCacheTest.class,
	SeedingManagerTest.class,
	TimeUtilsTest.class
})
public class TestSuiteImplUtil {

}
