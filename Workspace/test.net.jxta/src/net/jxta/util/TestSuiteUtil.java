package net.jxta.util;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	CountingOutputStreamTest.class,
	QueuingServerPipeAcceptorTest.class
})
public class TestSuiteUtil {

}
