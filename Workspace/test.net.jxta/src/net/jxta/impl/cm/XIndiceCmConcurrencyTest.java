package net.jxta.impl.cm;

import java.io.IOException;

import org.junit.Ignore;

<<<<<<< HEAD
import net.jxta.util.threads.TaskManager;
=======
import net.jxta.impl.util.threads.TaskManager;
>>>>>>> branch 'main' of https://github.com/chaupal/jxse-osgi.git

@Ignore("Takes too long to run")
public class XIndiceCmConcurrencyTest extends AbstractCmConcurrencyTest {

    @Override
    protected AdvertisementCache createWrappedCache(String areaName, TaskManager taskManager) throws IOException {
        return new XIndiceAdvertisementCache(testFileStore.getRoot().toURI(), areaName, taskManager);
    }
}
