package crud.page;

import ch.insign.cms.inject.AbstractBlockModule;

public class CarInventoryPageModule extends AbstractBlockModule {

    @Override
    protected void configure() {
        bindBlock().toInstance(CarInventoryPage.class);
        bindComponent().to(CarInventoryPageComponent.class);
    }

}
