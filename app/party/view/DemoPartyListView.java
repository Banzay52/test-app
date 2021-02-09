package party.view;

import ch.insign.cms.models.party.view.support.DefaultPartyListView;
import ch.insign.cms.views.html.admin.party.list;
import play.mvc.Http;
import play.twirl.api.Html;

import javax.inject.Inject;

public class DemoPartyListView extends DefaultPartyListView {

    private final views.html.admin.user.list list;

    @Inject
    public DemoPartyListView(ch.insign.cms.views.html.admin.party.list partyList,
                             views.html.admin.user.list list) {
        super(partyList);
        this.list = list;
    }

    @Override
    public Html render(Http.Request request) {
        return list.render(request);
    }

}
