package party;

import ch.insign.playauth.party.support.AbstractPartyRepository;
import play.db.jpa.JPAApi;

import javax.inject.Inject;


public class UserRepository extends AbstractPartyRepository<User> {

    @Inject
    public UserRepository(JPAApi jpaApi) {
        super(jpaApi, User.class);
    }

}
