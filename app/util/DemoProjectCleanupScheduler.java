package util;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import ch.insign.cms.CMSApi;
import ch.insign.cms.actors.ResetActor;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.time.Duration;

@Singleton
public class DemoProjectCleanupScheduler {

    private final static Duration RESET_TIMEOUT = Duration.ofHours(1);

    @Inject
    public DemoProjectCleanupScheduler(
            CMSApi cmsApi,
            ActorSystem actorSystem,
            @Named(ResetActor.ACTOR_REF)ActorRef resetActor
    ) {
        cmsApi.getSites().getAll().stream().forEach(s -> actorSystem.scheduler().schedule(
                RESET_TIMEOUT,
                RESET_TIMEOUT,
                resetActor,
                ResetActor.message(s),
                actorSystem.dispatcher(), ActorRef.noSender()
        ));
    }

}
