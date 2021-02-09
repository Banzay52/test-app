package inject;

import ch.insign.cms.models.party.PartyEvents;
import ch.insign.cms.models.party.view.*;
import ch.insign.cms.models.party.view.support.*;
import ch.insign.cms.services.PartyDatatableService;
import party.DemoPartyHandler;
import party.view.*;
import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import scala.collection.Seq;
import service.UserDatatableService;

public class DemoProjectPartyViewModule extends play.api.inject.Module {
    @Override
    public Seq<Binding<?>> bindings(Environment environment, Configuration configuration) {
        return seq(
                bind(PartyDatatableService.class).to(UserDatatableService.class),
                bind(PartyEvents.class).to(DemoPartyHandler.class),
                bind(PartyEditView.class).to(DemoPartyEditView.class),
                bind(PartyMenuItemsView.class).to(DefaultPartyMenuItemsView.class),
                bind(PartyListView.class).to(DemoPartyListView.class),
                bind(PartyCreateView.class).to(DemoPartyCreateView.class),
                bind(PartyChangePasswordView.class).to(DemoPartyChangePasswordView.class),
                bind(PartyProfileView.class).to(DemoPartyProfileView.class),
                bind(PartyEditRolesView.class).to(DefaultPartyEditRolesView.class));
    }
}
