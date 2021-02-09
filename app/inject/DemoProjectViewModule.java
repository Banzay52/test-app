package inject;

import crud.page.views.CarInventoryPageView;
import crud.page.views.DefaultCarInventoryPageView;
import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import scala.collection.Seq;

public class DemoProjectViewModule extends play.api.inject.Module {
    @Override
    public Seq<Binding<?>> bindings(Environment environment, Configuration configuration) {
        return seq(
                bind(CarInventoryPageView.class).to(DefaultCarInventoryPageView.class)
        );
    }
}
