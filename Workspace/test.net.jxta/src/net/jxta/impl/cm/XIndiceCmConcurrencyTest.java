package net.jxta.impl.cm;

import java.io.IOException;

import org.junit.Ignore;

import net.jxta.util.threads.TaskManager;

@Ignore("Takes too long to run")
public class XIndiceCmConcurrencyTest extends AbstractCmConcurrencyTest {

    @Override
    protected AdvertisementCache createWrappedCache(String areaName, TaskManager taskManager) throws IOException {
        return new XIndiceAdvertisementCache(testFileStore.getRoot().toURI(), areaName, taskManager);
    }
}
