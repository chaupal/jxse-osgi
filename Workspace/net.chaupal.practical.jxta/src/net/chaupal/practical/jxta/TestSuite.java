package net.chaupal.practical.jxta;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.chaupal.practical.jxta.examples.ConfigurationObjects;
import net.chaupal.practical.jxta.examples.ConnectingWithOSGi;
import net.chaupal.practical.jxta.examples.CreateLocalConfigurationExample;
import net.chaupal.practical.jxta.examples.CreatingAndSettingSeeds;
import net.chaupal.practical.jxta.examples.EdgeAnna;
import net.chaupal.practical.jxta.examples.EdgeChihiro;
import net.chaupal.practical.jxta.examples.RendezVousAminah;
import net.chaupal.practical.jxta.examples.RendezVousJack;
import net.chaupal.practical.jxta.examples.RetrieveModifySaveExistingConfigurationExample;
import net.chaupal.practical.jxta.examples.StartingAndStoppingJXTAExample;

@RunWith(Suite.class)
@SuiteClasses({
	StartingAndStoppingJXTAExample.class,//100
	CreateLocalConfigurationExample.class,//110
	RetrieveModifySaveExistingConfigurationExample.class,//120
	ConfigurationObjects.class,//150
	ConnectingWithOSGi.class,//160
	CreatingAndSettingSeeds.class,
	EdgeAnna.class,
	EdgeChihiro.class,
	RendezVousAminah.class,
	RendezVousJack.class
})
public class TestSuite {

}
