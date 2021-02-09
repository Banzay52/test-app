package blocks.errorblock;

import blocks.errorblock.html.defaultErrorShow;
import ch.insign.cms.blocks.BlockWrapper;
import ch.insign.cms.blocks.Cacheable;
import ch.insign.cms.blocks.Displayable;
import play.mvc.Http;
import play.twirl.api.Html;

import javax.inject.Inject;

public class DefaultErrorPageComponent implements Cacheable<DefaultErrorPage>, BlockWrapper<DefaultErrorPage> {

    private final defaultErrorShow defaultErrorShow;
    private final views.html.main main;

    @Inject
    public DefaultErrorPageComponent(defaultErrorShow defaultErrorShow,
                                     views.html.main main) {
        this.defaultErrorShow = defaultErrorShow;
        this.main = main;
    }

    @Override
    public boolean supports(String blockName) {
        return DefaultErrorPage.class.getName().equals(blockName);
    }

    @Override
    public Html display(Http.Request request, DefaultErrorPage block) {
        return defaultErrorShow.render(block, request);
    }

    @Override
    public Html wrapContent(Http.Request request, Html content, DefaultErrorPage block) {
        return main.render(block, Html.apply(""), Html.apply(""), content, request);
    }

}
