package util;

import auth.LoggingAuthenticationListener;
import ch.insign.cms.CMSApi;
import ch.insign.cms.CMSApiLifecycleImpl;
import ch.insign.cms.blocks.jotformpageblock.service.JotFormService;
import ch.insign.cms.models.NavigationItem;
import ch.insign.cms.repositories.NavigationItemRepository;
import ch.insign.cms.views.html.admin.shared.flashPartial;
import ch.insign.playauth.PlayAuthApi;
import ch.insign.playauth.party.Party;
import ch.insign.playauth.party.PartyRepository;
import com.typesafe.config.Config;
import play.db.jpa.JPAApi;
import widgets.registeredusers.RegisteredUsersWidget;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DemoProjectCmsApiLifecycle extends CMSApiLifecycleImpl {

    private final CMSApi cmsApi;
    private final PlayAuthApi playAuthApi;
    private final PartyRepository<? extends Party> partyRepository;
    private final LoggingAuthenticationListener loggingAuthenticationListener;

    @Inject
    public DemoProjectCmsApiLifecycle(
            Config config,
            JPAApi jpaApi,
            CMSApi cmsApi,
            PlayAuthApi playAuthApi,
            NavigationItemRepository<NavigationItem> navigationItemRepository,
            PartyRepository<? extends Party> partyRepository,
            LoggingAuthenticationListener loggingAuthenticationListener
    ) {
        super(config, jpaApi, cmsApi, navigationItemRepository);
        this.cmsApi = cmsApi;
        this.playAuthApi = playAuthApi;
        this.partyRepository = partyRepository;
        this.loggingAuthenticationListener = loggingAuthenticationListener;
    }

    @Override
    public void onStart() {
        super.onStart();
        playAuthApi.getEventDispatcher().addSubscriber(loggingAuthenticationListener);
    }

    @Override
    protected void registerContentFilters() {
        super.registerContentFilters();
        cmsApi.getFilterManager().register(new RegisteredUsersWidget(partyRepository));
    }

    @Override
    protected void registerUncachedPartials() {
        super.registerUncachedPartials();
        cmsApi.getUncachedManager().register("flashPartial", flashPartial::render);
    }

}
