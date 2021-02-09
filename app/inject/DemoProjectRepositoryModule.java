package inject;

import crud.data.repository.BrandRepository;
import crud.data.repository.CarRepository;
import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import scala.collection.Seq;

public class DemoProjectRepositoryModule extends play.api.inject.Module {
    @Override
    public Seq<Binding<?>> bindings(Environment environment, Configuration configuration) {
        return seq(
                bind(CarRepository.class).toSelf(),
                bind(BrandRepository.class).toSelf()
        );
    }
}
