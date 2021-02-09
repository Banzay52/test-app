package formatters;

import play.data.format.Formatters;
import play.i18n.MessagesApi;
import play.inject.Injector;

import javax.inject.Inject;

public class FormattersProvider extends ch.insign.cms.formatters.FormattersProvider {

    /**
     * Use injector to provision registered formatters
     */
    private final Injector injector;

    @Inject
    public FormattersProvider(MessagesApi messagesApi, Injector injector) {
        super(messagesApi);
        this.injector = injector;
    }

    @Override
    public Formatters get() {
        Formatters formatters = super.get();

        // Register your formatters here:
        // formatters.register(YourEntity.class, injector.instanceOf(YourEntityFormatter.class));

        return formatters;
    }
}
