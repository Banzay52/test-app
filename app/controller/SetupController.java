/*
 * Copyright 2019 insign gmbh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controller;

import ch.insign.cms.actors.ResetActor;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import ch.insign.cms.CMSApi;
import ch.insign.cms.controllers.CspHeader;
import ch.insign.cms.controllers.GlobalActionWrapper;
import play.Logger;
import play.db.jpa.JPAApi;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;
import play.twirl.api.Html;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.time.Duration;
import java.util.concurrent.ExecutionException;

/**
 * Holds all actions for setting up or resetting the initial cms data,
 * e.g. default page, trash and draft navigation entries etc.
 */
@With({GlobalActionWrapper.class, CspHeader.class})
public class SetupController extends Controller {

    private static final Duration RESET_TIMEOUT = Duration.ofSeconds(30);
    private static final Logger.ALogger logger = Logger.of(SetupController.class);

    private final CMSApi cmsApi;
    private final JPAApi jpaApi;
    private final ActorRef resetActor;

    @Inject
    public SetupController(CMSApi cmsApi, JPAApi jpaApi, @Named(ResetActor.ACTOR_REF) ActorRef resetActor) {
        this.cmsApi = cmsApi;
        this.jpaApi = jpaApi;
        this.resetActor = resetActor;
    }

    /**
     * Resets and initializes all DB data, then adds some example cms data.
     * This is useful during development.
     */
    public Result reset(Http.Request request) {
        if (cmsApi.getConfig().isResetRouteEnabled()) {
            try {
                Patterns.ask(
                        resetActor,
                        ResetActor.message(request.attrs().get(ch.insign.cms.controllers.Attrs.CURRENT_SITE)),
                        RESET_TIMEOUT
                ).toCompletableFuture().get();

                // Flush all changes to the database
                jpaApi.withTransaction(em -> {
                    em.getEntityManagerFactory().getCache().evictAll();
                });

                return ok(Html.apply("<a href='/'>Data initialized, now go to /</a>"));
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error during resetting site.", e);
                return internalServerError("Error while executing reset - see logs for details.");
            }
        }

        return internalServerError("Reset is not enabled.");
    }

}
