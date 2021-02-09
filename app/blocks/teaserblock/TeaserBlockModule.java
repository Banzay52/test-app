package blocks.teaserblock;

import ch.insign.cms.inject.AbstractBlockModule;

public class TeaserBlockModule extends AbstractBlockModule {

    @Override
    protected void configure() {
        bindBlock().toInstance(TeaserBlock.class);
        bindComponent().to(TeaserBlockComponent.class);
    }

}
