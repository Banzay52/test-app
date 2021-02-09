package inject;

import com.google.inject.AbstractModule;
import crud.page.CarInventoryPage;

public class DemoProjectStaticCmsBlockModule extends AbstractModule {
    @Override
    protected void configure() {
        requestStaticInjection(CarInventoryPage.class);
    }
}
