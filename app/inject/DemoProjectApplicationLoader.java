package inject;

import play.inject.guice.GuiceApplicationBuilder;
import play.inject.guice.GuiceApplicationLoader;

public class DemoProjectApplicationLoader extends GuiceApplicationLoader {
    @Override
    public GuiceApplicationBuilder builder(play.ApplicationLoader.Context context) {
        return super.builder(context);
    }
}
