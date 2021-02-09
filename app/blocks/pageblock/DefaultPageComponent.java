package blocks.pageblock;

import blocks.pageblock.html.defaultEdit;
import blocks.pageblock.html.defaultShowSite1;
import blocks.pageblock.html.defaultShowSite2;
import ch.insign.cms.CMSApi;
import ch.insign.cms.blocks.Cacheable;
import ch.insign.cms.blocks.Displayable;
import ch.insign.cms.blocks.GenericPageComponent;
import ch.insign.cms.cache.BlockCache;
import ch.insign.cms.models.NavigationItem;
import ch.insign.cms.models.PageBlock;
import ch.insign.cms.repositories.BlockRepository;
import ch.insign.cms.repositories.NavigationItemRepository;
import ch.insign.cms.views.admin.utils.BackUrl;
import ch.insign.commons.db.SmartFormFactory;
import play.data.Form;
import play.mvc.Http;
import play.twirl.api.Html;

import javax.inject.Inject;

import static blocks.pageblock.DefaultPage.SITE_1_KEY;
import static blocks.pageblock.DefaultPage.SITE_2_KEY;

public class DefaultPageComponent extends GenericPageComponent<DefaultPage> implements Cacheable<DefaultPage> {

    private final CMSApi cmsApi;
    private final defaultShowSite1 showSite1;
    private final defaultShowSite2 showSite2;
    private final defaultEdit edit;

    @Inject
    public DefaultPageComponent(
            CMSApi cmsApi,
            SmartFormFactory formFactory,
            BlockRepository<DefaultPage> blockRepository,
            NavigationItemRepository<NavigationItem> navigationItemRepository,
            defaultShowSite1 showSite1,
            defaultShowSite2 showSite2,
            defaultEdit edit
    ) {
        super(cmsApi, formFactory, blockRepository, navigationItemRepository, DefaultPage.class);
        this.cmsApi = cmsApi;
        this.showSite1 = showSite1;
        this.showSite2 = showSite2;
        this.edit = edit;
    }

    @Override
    public boolean supports(String blockName) {
        return DefaultPage.class.getName().equals(blockName);
    }

    @Override
    public Html cached(Http.Request request, BlockCache cache, DefaultPage block) {
        return PageBlock.KEY_HOMEPAGE.equals(block.getKey())
                ? display(request, block)
                : cache.getOrElse(() -> display(request, block));
    }

    @Override
    public Html display(Http.Request request, DefaultPage block) {
        switch (cmsApi.getSites().current(request).key) {
            case SITE_1_KEY:
                return showSite1.render(block, request);

            case SITE_2_KEY:
                return showSite2.render(block, request);

            default:
                throw new RuntimeException("Invalid site: " + cmsApi.getSites().current(request).name);
        }
    }

    @Override
    public Html edit(Http.Request request, DefaultPage block, Form<DefaultPage> form) {
        return edit.render(block, form, BackUrl.get(request), Html.apply(""), request);
    }

}
