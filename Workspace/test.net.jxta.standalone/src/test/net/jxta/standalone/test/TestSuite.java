package test.net.jxta.standalone.test;

import java.net.URI;
import java.util.logging.Logger;
import org.junit.Test;
import test.net.jxta.standalone.library.DocumentTest;

import test.net.jxta.standalone.library.RefJxtaLoaderTest;

public class TestSuite {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public static final String S_JXTA_JAR_PATH = "https://repo1.maven.org/maven2/net/jxta/jxta-jxse/2.5/jxta-jxse-2.5.jar";
	public static final String S_JXTA_JAR_CLASS = "net.jxta.impl.endpoint.servlethttp.ServletHttpTransport";
	
	private static TestSuite suite = new TestSuite();
	
	
	public static TestSuite getInstance() {
		return suite;
	}
	
	@Test
	public void runTest(){
		try {
			//loadClassesFromWebsite( S_JXTA_JAR_PATH, S_JXTA_JAR_CLASS);
			documentTest();
			logger.info("completed");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadClassesFromWebsite( String path, String code ) throws Exception {
        URI uri = URI.create( path );
        //Class<?> clazz = loadClass( code, uri, false);
	
	}
	
	private void refJxtaLoaderTest() throws Exception{
		RefJxtaLoaderTest test = new RefJxtaLoaderTest();
		test.setup();		
	}
	
	private void documentTest() throws Exception{
		DocumentTest test = new DocumentTest();
		test.testPlainTextDoc();
		test.testLiteXMLStructuredDoc();
		test.testDOMXMLStructuredDoc();
		test.testExtensionMapping();
		test.testIssue102();
		test.testIssue1282();
		test.testIssue1372();
		test.testIssue13XX();
		test.testIssue15();
		
		test.run();		
	}

}
