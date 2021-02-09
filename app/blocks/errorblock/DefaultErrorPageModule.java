package blocks.errorblock;

import ch.insign.cms.inject.AbstractBlockModule;

public class DefaultErrorPageModule extends AbstractBlockModule {

    @Override
    protected void configure() {
        bindBlock().toInstance(DefaultErrorPage.class);
        bindComponent().to(DefaultErrorPageComponent.class);
    }

}
