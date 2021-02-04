package net.chaupal.practical.jxta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.chaupal.practical.jxta.examples.ConfigurationObjects;
import net.chaupal.practical.jxta.examples.CreateLocalConfigurationExample;
import net.chaupal.practical.jxta.examples.CreatingAndSettingSeeds;
import net.chaupal.practical.jxta.examples.EdgeAnna;
import net.chaupal.practical.jxta.examples.EdgeChihiro;
import net.chaupal.practical.jxta.examples.RendezVousAminah;
import net.chaupal.practical.jxta.examples.RendezVousJack;
import net.chaupal.practical.jxta.examples.RetrieveModifySaveExistingConfigurationExample;
import net.chaupal.practical.jxta.examples.StartingAndStoppingJXTAExample;
import net.chaupal.practical.jxta.examples.multicastsockets.EdgeTeyacapanAnotherMulticastParticipant;

@RunWith(Suite.class)
@SuiteClasses({
	StartingAndStoppingJXTAExample.class,//100
	CreateLocalConfigurationExample.class,//110
	RetrieveModifySaveExistingConfigurationExample.class,//120
	ConfigurationObjects.class,//150
	//ConnectingWithOSGi.class,//160
	CreatingAndSettingSeeds.class,
	EdgeAnna.class,
	EdgeChihiro.class,
	RendezVousAminah.class,
	RendezVousJack.class
})
public class TestSuite {

	private static TestSuite suite = new TestSuite();
	
	public static TestSuite getSuite() {
		return suite;
	}

	public void runTests() {
		try {		
			Properties props = System.getProperties();
			Collection<String> attributes = new ArrayList<>();
			Iterator<Map.Entry<Object, Object>> iterator = props.entrySet().iterator();
			while( iterator.hasNext()) {
				Map.Entry<Object, Object> entry = iterator.next();
				attributes.add(entry.getKey().toString() + "=" + entry.getValue());
			}
			String[] args = attributes.toArray( new String[ attributes.size()]);		
			//ConfigurationObjects.main(args);
			//ConnectingWithOSGi.main(args);
			//CreateLocalConfigurationExample.main(args);
			//CreatingAndSettingSeeds.main(args);
<<<<<<< HEAD
			EdgeAnna.main(args);
=======
			//EdgeAnna.main(args);
			//CreateLocalConfigurationExample.main(args);
			EdgeTeyacapanAnotherMulticastParticipant.main(args );
>>>>>>> branch 'main' of https://github.com/chaupal/jxse-osgi.git
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}
	}
}
