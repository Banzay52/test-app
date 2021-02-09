package inject;

import ch.insign.cms.models.Bootstrapper;
import util.DemoProjectBootstrapper;
import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import scala.collection.Seq;

public class DemoProjectBootstrapperModule extends play.api.inject.Module {
    @Override
    public Seq<Binding<?>> bindings(Environment environment, Configuration configuration) {
        return seq(bind(Bootstrapper.class).to(DemoProjectBootstrapper.class));
    }
}
