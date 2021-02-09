package crud.page.views;

import crud.data.service.CarService;
import crud.page.CarInventoryPage;
import crud.data.repository.CarRepository;
import play.mvc.Http;
import play.twirl.api.Html;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DefaultCarInventoryPageView implements CarInventoryPageView {

    private CarInventoryPage page;
    private CarService service;

    @Override
    public CarInventoryPageView setPage(CarInventoryPage page) {
        this.page = page;
        return this;
    }

    @Override
    public CarInventoryPageView setService(CarService service) {
        this.service = service;
        return this;
    }

    @Override
    public Html render(Http.Request request) {
        return crud.page.views.html.show.render(
                page,
                StreamSupport.stream(service.findAll().spliterator(), false).collect(Collectors.toList()),
                request
        );
    }

}
