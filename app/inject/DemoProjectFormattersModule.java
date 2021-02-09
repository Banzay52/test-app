package inject;

import util.DemoProjectFormattersProvider;
import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import play.data.format.Formatters;
import scala.collection.Seq;

public class DemoProjectFormattersModule extends play.api.inject.Module {

    @Override
    public Seq<Binding<?>> bindings(Environment environment, Configuration configuration) {
        return seq(
                bind(Formatters.class).toProvider(DemoProjectFormattersProvider.class)
        );
    }

}
