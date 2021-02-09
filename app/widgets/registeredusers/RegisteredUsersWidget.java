package widgets.registeredusers;

import ch.insign.cms.models.FrontendWidget;
import ch.insign.commons.filter.Filterable;
import ch.insign.playauth.party.Party;
import ch.insign.playauth.party.PartyRepository;
import play.twirl.api.Html;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Widget shows list of users registered in play-cms
 */
public class RegisteredUsersWidget extends FrontendWidget {

    private static final String TAG = "registeredUsersWidget";
    private static final int MAX_USERS = 10;

    private final PartyRepository<? extends Party> partyRepository;

    @Inject
    public RegisteredUsersWidget(PartyRepository<? extends Party> partyRepository) {
        this.partyRepository = partyRepository;
    }

    @Override
    public String[] filterTags() {
        return new String[] {TAG};
    }

    @Override
    public Html render(String tag, List<String> params, Filterable source) {
        int usersMaxSize = MAX_USERS;

        if (params.size() > 1) {
            try {
                usersMaxSize = Integer.parseInt(params.get(1));
            } catch (NumberFormatException ignore) {}
        }

        // FIXME: Causes an exception ("Attempting to execute an operation on a closed EntityManager.")
        List<Party> users = new ArrayList<>();/*StreamSupport.stream(partyRepository.findAll().spliterator(), false)
                .limit(usersMaxSize)
                .collect(Collectors.toList());*/

        return widgets.registeredusers.html.registered_users.render(users);
    }

}
