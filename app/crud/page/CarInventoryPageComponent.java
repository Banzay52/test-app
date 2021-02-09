package crud.page;

import ch.insign.cms.CMSApi;
import ch.insign.cms.blocks.Cacheable;
import ch.insign.cms.blocks.Displayable;
import ch.insign.cms.blocks.GenericPageComponent;
import ch.insign.cms.models.NavigationItem;
import ch.insign.cms.repositories.BlockRepository;
import ch.insign.cms.repositories.NavigationItemRepository;
import ch.insign.commons.db.SmartFormFactory;
import crud.data.service.CarService;
import crud.page.views.CarInventoryPageView;
import play.data.Form;
import play.mvc.Http;
import play.twirl.api.Html;

import javax.inject.Inject;
import javax.inject.Provider;

public class CarInventoryPageComponent extends GenericPageComponent<CarInventoryPage> implements Cacheable<CarInventoryPage> {

    private final CarService carService;
    private final Provider<CarInventoryPageView> viewProvider;
    private final crud.page.views.html.edit edit;

    @Inject
    public CarInventoryPageComponent(
            CMSApi cmsApi,
            SmartFormFactory formFactory,
            BlockRepository<CarInventoryPage> blockRepository,
            NavigationItemRepository<NavigationItem> navigationItemRepository,
            CarService carService,
            Provider<CarInventoryPageView> viewProvider,
            crud.page.views.html.edit edit
    ) {
        super(cmsApi, formFactory, blockRepository, navigationItemRepository, CarInventoryPage.class);
        this.carService = carService;
        this.viewProvider = viewProvider;
        this.edit = edit;
    }

    @Override
    public boolean supports(String blockName) {
        return CarInventoryPage.class.getName().equals(blockName);
    }

    @Override
    public Html display(Http.Request request, CarInventoryPage block) {
        return viewProvider.get()
                .setPage(block)
                .setService(carService)
                .render(request);
    }

    @Override
    public Html edit(Http.Request request, CarInventoryPage block, Form<CarInventoryPage> form) {
        return edit.render(block, form, request.getQueryString("backURL"), null, request);
    }

}
