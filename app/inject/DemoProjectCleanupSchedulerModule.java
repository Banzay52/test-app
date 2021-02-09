package inject;

import com.google.inject.AbstractModule;
import com.typesafe.config.Config;
import play.Environment;
import util.DemoProjectCleanupScheduler;

public class DemoProjectCleanupSchedulerModule extends AbstractModule {

    private final Config config;
    private final Environment environment;

    public DemoProjectCleanupSchedulerModule(Environment environment, Config config) {
        this.config = config;
        this.environment = environment;
    }

    @Override
    protected void configure() {
        if (config.getBoolean("demo.scheduler.reset.enabled")) {
            bind(DemoProjectCleanupScheduler.class).asEagerSingleton();
        }
    }
}
