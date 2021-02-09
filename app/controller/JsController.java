package controller;

import ch.insign.cms.controllers.CspHeader;
import ch.insign.cms.controllers.GlobalActionWrapper;
import play.mvc.Http;
import views.js.js._config;
import com.google.inject.Inject;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;

@With({GlobalActionWrapper.class, CspHeader.class})
public class JsController extends Controller {

    @Inject
    public JsController() {}

    /**
     * Renders a scala template (_config.scala.js) to provide initial javascript configuration objects used to initialize fronted libraries
     * By default template includes config-settings from Admin-area, which can be overwritten in the web-app-specific template (to extend functionality)
     * As an example, see implementation of plantUml extension for tinyMce configured in _config.scala.js
     *
     * @See ch.insign.cms.controllers.JsController
     *
     * @return javascript object called "jsconfig" with global javascript-config variables
     */
    public Result config(Http.Request request) {
        return ch.insign.commons.util.Etag.resultWithETag(request, _config.render(request));
    }

}
