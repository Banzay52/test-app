package party.view;

import ch.insign.cms.models.party.view.PartyMenuItemsView;
import ch.insign.cms.models.party.view.support.DefaultPartyEditView;
import ch.insign.cms.views.html.admin.party.editForm;
import com.google.inject.Inject;
import play.mvc.Http;
import play.twirl.api.Html;

public class DemoPartyEditView extends DefaultPartyEditView {

    private final views.html.admin.user.editForm editForm;

    @Inject
    public DemoPartyEditView(PartyMenuItemsView partyMenuItemsView,
                             editForm partyEditForm,
                             views.html.admin.user.editForm editForm) {
        super(partyMenuItemsView, partyEditForm);
        this.editForm = editForm;
    }

    @Override
    public Html render(Http.Request request) {
        return editForm.render(
            partyForm,
            customerEmails,
            party,
            request
        );
    }

}
