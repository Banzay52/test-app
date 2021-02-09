package inject;

import ch.insign.cms.CMSApi;
import ch.insign.cms.CMSApiImpl;
import ch.insign.cms.CMSApiLifecycle;
import ch.insign.cms.events.BlockEventHandlerProvider;
import ch.insign.cms.events.BlockEventHandlerProviderImpl;
import ch.insign.cms.models.DefaultRouteResolver;
import ch.insign.playauth.controllers.RouteResolver;
import crud.data.service.BrandService;
import crud.data.service.CarService;
import crud.data.service.DemoBrandService;
import crud.data.service.DemoCarService;
import util.DemoProjectCmsApiLifecycle;
import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import scala.collection.Seq;

public class DemoProjectApiModule extends play.api.inject.Module {

    @Override
    public Seq<Binding<?>> bindings(Environment environment, Configuration configuration) {
        return seq(
                bind(BlockEventHandlerProvider.class).to(BlockEventHandlerProviderImpl.class),
                bind(CMSApiLifecycle.class).to(DemoProjectCmsApiLifecycle.class),
                bind(RouteResolver.class).to(DefaultRouteResolver.class),
                bind(CMSApi.class).to(CMSApiImpl.class),
                bind(BrandService.class).to(DemoBrandService.class),
                bind(CarService.class).to(DemoCarService.class));
    }

}
