package inject;

import auth.LoggingAuthenticationListener;
import ch.insign.playauth.party.Party;
import ch.insign.playauth.party.PartyRepository;
import ch.insign.playauth.party.PartyService;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import party.User;
import party.UserRepository;
import party.UserService;

public class DemoProjectAuthModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(PartyRepository.class).to(UserRepository.class);
        bind(new TypeLiteral<PartyRepository<? extends Party>>(){}).to(UserRepository.class);
        bind(new TypeLiteral<PartyRepository<User>>(){}).to(UserRepository.class);
        bind(PartyService.class).to(UserService.class);
        bind(new TypeLiteral<PartyService<? extends Party>>(){}).to(UserService.class);
        bind(new TypeLiteral<PartyService<User>>(){}).to(UserService.class);
        bind(LoggingAuthenticationListener.class).asEagerSingleton();
    }

}
