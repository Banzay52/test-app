package blocks;

import ch.insign.cms.blocks.BlockWrapper;
import ch.insign.cms.blocks.pages.AnyPagePluggableComponent;
import ch.insign.cms.models.PageBlock;
import com.google.inject.Inject;
import play.mvc.Http;
import play.twirl.api.Html;

public class DemoPageWrapper implements BlockWrapper<PageBlock>, AnyPagePluggableComponent {

    private final views.html.main main;

    @Inject
    public DemoPageWrapper(views.html.main main) {
        this.main = main;
    }

    @Override
    public Html wrapContent(Http.Request request, Html content, PageBlock block) {
        return main.render(
                block,
                Html.apply(""),
                Html.apply(""),
                content,
                request
        );
    }

}
