package party.view;

import ch.insign.cms.models.party.view.PartyMenuItemsView;
import ch.insign.cms.models.party.view.support.DefaultPartyChangePasswordView;
import ch.insign.cms.views.admin.utils.AdminContext;
import com.google.inject.Inject;
import play.mvc.Http;
import play.twirl.api.Html;
import views.html.admin.user.passwordForm;

public class DemoPartyChangePasswordView extends DefaultPartyChangePasswordView {

    private final passwordForm passwordForm;

    @Inject
    public DemoPartyChangePasswordView(PartyMenuItemsView partyMenuItemsView,
                                       ch.insign.cms.views.html.admin.party.passwordForm partyPasswordForm,
                                       passwordForm passwordForm) {
        super(partyMenuItemsView, partyPasswordForm);
        this.passwordForm = passwordForm;
    }

    @Override
    public Html render(Http.Request request) {
        return passwordForm.render(
                new AdminContext(),
                party,
                form,
                partyMenuItemsView.setParty(party).render(request),
                request);
    }

}

