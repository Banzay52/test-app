package inject;

import blocks.DemoPageWrapper;
import ch.insign.cms.inject.AbstractBlockModule;

public class DemoPageWrapperModule extends AbstractBlockModule {

    @Override
    protected void configure() {
        bindComponent().to(DemoPageWrapper.class);
    }

}
