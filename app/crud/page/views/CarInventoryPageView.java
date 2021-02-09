package crud.page.views;

import ch.insign.cms.models.HtmlView;
import crud.data.service.CarService;
import crud.page.CarInventoryPage;

public interface CarInventoryPageView extends HtmlView {
    CarInventoryPageView setPage(CarInventoryPage page);
    CarInventoryPageView setService(CarService service);
}
