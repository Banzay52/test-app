package party.view;

import ch.insign.cms.models.party.view.PartyMenuItemsView;
import ch.insign.cms.models.party.view.support.DefaultPartyProfileView;
import ch.insign.cms.views.html.admin.party.profile;
import play.mvc.Http;
import play.twirl.api.Html;

import javax.inject.Inject;

public class DemoPartyProfileView extends DefaultPartyProfileView {

    private final views.html.admin.user.profile profile;

    @Inject
    public DemoPartyProfileView(PartyMenuItemsView partyMenuItemsView,
                                ch.insign.cms.views.html.admin.party.profile partyProfile,
                                views.html.admin.user.profile profile) {
        super(partyMenuItemsView, partyProfile);
        this.profile = profile;
    }

    @Override
    public Html render(Http.Request request) {
        return profile.render(
                party,
                partyMenuItemsView.setParty(party).render(request),
                request
        );
    }

}
