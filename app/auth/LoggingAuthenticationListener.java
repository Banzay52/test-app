package auth;

import ch.insign.playauth.PlayAuthApi;
import ch.insign.playauth.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import party.User;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

@Singleton
public class LoggingAuthenticationListener implements EventSubscriber {
    private final static Logger logger = LoggerFactory.getLogger(LoggingAuthenticationListener.class);

    private final JPAApi jpaApi;

    @Inject
    public LoggingAuthenticationListener(JPAApi jpaApi) {
        this.jpaApi = jpaApi;
    }

    @Override
    public Collection<Class<? extends EventObject>> getSubscribedEvents() {
        return Arrays.asList(PartyLoginSuccessEvent.class, PartyLoginFailureEvent.class, PartyLogoutEvent.class);
    }

    @Override
    public void handle(EventObject event) {
        try {
            jpaApi.withTransaction(() -> {
                if (event instanceof PartyLoginSuccessEvent) {
                    on((PartyLoginSuccessEvent) event);
                } else if (event instanceof PartyLoginFailureEvent) {
                    on((PartyLoginFailureEvent) event);
                } else if (event instanceof PartyLogoutEvent) {
                    on((PartyLogoutEvent) event);
                }
            });
        } catch (Throwable t) {
            logger.error("Can not handle login/logout AuthenticationListener", t);
        }
    }

    private void on(PartyLoginSuccessEvent e) {
        User user = (User) e.getParty();
        user.setLastLogin(new Date());
        user.setLoginCount(user.getLoginCount() + 1);

        logger.debug("Authenticated user {}", user.getEmail());
    }

    private void on(PartyLoginFailureEvent e) {
        logger.debug("Failed to authenticate user by submitted account identity {}", e.getToken().getPrincipal());
    }

    private void on(PartyLogoutEvent e) {
        User user = (User) e.getParty();
        if (user != null) {
            logger.debug("Logged out user {}", user.getEmail());
        }
    }
}
