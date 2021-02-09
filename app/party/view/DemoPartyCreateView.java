package party.view;

import ch.insign.cms.models.party.view.support.DefaultPartyCreateView;
import ch.insign.cms.views.html.admin.party.createForm;
import play.mvc.Http;
import play.twirl.api.Html;

import javax.inject.Inject;

public class DemoPartyCreateView extends DefaultPartyCreateView {

    private final views.html.admin.user.createForm createForm;

    @Inject
    public DemoPartyCreateView(
            createForm partyCreateForm,
            views.html.admin.user.createForm createForm
    ) {
        super(partyCreateForm);
        this.createForm = createForm;
    }

    @Override
    public Html render(Http.Request request) {
        return createForm.render(partyForm, customerEmails, request);
    }

}
