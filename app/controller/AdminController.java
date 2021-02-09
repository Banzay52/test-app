package controller;

import ch.insign.cms.controllers.GlobalActionWrapper;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;

import javax.inject.Inject;

@With({GlobalActionWrapper.class})
public class AdminController extends Controller {

    private final views.html.admin.myapp.main main;

    @Inject
    public AdminController(views.html.admin.myapp.main main) {
        this.main = main;
    }

    public Result myApp(Http.Request request) {
        return ok(main.render(request));
    }

}
