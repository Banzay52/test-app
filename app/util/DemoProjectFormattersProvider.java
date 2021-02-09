package util;

import ch.insign.cms.formatters.FormattersProvider;
import crud.data.entity.Brand;
import crud.data.formatter.BrandFormatter;
import play.data.format.Formatters;
import play.i18n.MessagesApi;
import play.inject.Injector;

import javax.inject.Inject;
import javax.inject.Provider;

public class DemoProjectFormattersProvider extends FormattersProvider {

    private final Injector injector;

    @Inject
    public DemoProjectFormattersProvider(MessagesApi messagesApi, Injector injector) {
        super(messagesApi);
        this.injector = injector;
    }

    @Override
    public Formatters get() {
        Formatters formatters = super.get();
        formatters.register(Brand.class, injector.instanceOf(BrandFormatter.class));
        return formatters;
    }

}
