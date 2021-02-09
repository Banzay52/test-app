package blocks.pageblock;

import ch.insign.cms.inject.AbstractBlockModule;

public class DefaultPageModule extends AbstractBlockModule {

    @Override
    protected void configure() {
        bindBlock().toInstance(DefaultPage.class);
        bindComponent().to(DefaultPageComponent.class);
    }

}
