package util;

import blocks.errorblock.DefaultErrorPage;
import blocks.pageblock.DefaultPage;
import blocks.teaserblock.TeaserBlock;
import ch.insign.cms.CMSApi;
import ch.insign.cms.CMSTemplateApi;
import ch.insign.cms.repositories.BlockRepository;
import ch.insign.cms.blocks.backendlinkblock.BackendLinkBlock;
import ch.insign.cms.blocks.errorblock.ErrorPage;
import ch.insign.cms.blocks.horizontalcollection.HorizontalCollectionBlock;
import ch.insign.cms.models.*;
import ch.insign.cms.permissions.ApplicationPermission;
import ch.insign.cms.permissions.BlockPermission;
import ch.insign.cms.repositories.EmailTemplateRepository;
import ch.insign.commons.db.Model;
import ch.insign.playauth.authz.AccessControlManager;
import ch.insign.playauth.party.*;
import ch.insign.playauth.party.support.DefaultPartyRole;
import com.typesafe.config.Config;
import crud.data.service.BrandService;
import crud.data.service.CarService;
import crud.page.CarInventoryPage;
import crud.data.entity.Brand;
import crud.data.entity.Car;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import party.User;
import party.UserService;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

import static ch.insign.playauth.party.support.DefaultPartyRole.ROLE_USER;

public class DemoProjectBootstrapper extends DefaultBootstrapper {

    public static final String EMAIL_KEY_PASSWORD_RESET = "password.reset";
    public static final String EMAIL_KEY_PASSWORD_RECOVERY_SUCCESS = "password.recovery.success";
    public static final String EMAIL_KEY_REGISTRATION_WELCOME = "user.registration.welcome";
    public static final String EMAIL_KEY_CHANGE_EMAIL = "user.account.change.email";

    private static final Logger logger = LoggerFactory.getLogger(DemoProjectBootstrapper.class);

    // Predefined Demo Role
    private static final String ROLE_DEMO_ROLE = "DemoRole";

    private final CMSTemplateApi cmsTemplateApi;
    private final AccessControlManager accessControlManager;
    private final PartyRoleManager partyRoleManager;
    private final BlockRepository<AbstractBlock> abstractBlockRepository;
    private final BlockRepository<DefaultPage> defaultPageRepository;
    private final EmailTemplateRepository<EmailTemplate> emailTemplateRepository;
    private final UserService userService;
    private final CarService carService;
    private final BrandService brandService;

    @Inject
    public DemoProjectBootstrapper(
            Config config,
            JPAApi jpaApi,
            CMSApi cmsApi,
            CMSTemplateApi cmsTemplateApi,
            AccessControlManager accessControlManager,
            PartyRoleManager partyRoleManager,
            BlockRepository<AbstractBlock> abstractBlockRepository,
            BlockRepository<DefaultPage> defaultPageRepository,
            EmailTemplateRepository<EmailTemplate> emailTemplateRepository,
            UserService userService,
            CarService carService,
            BrandService brandService) {
        super(config, jpaApi, cmsApi, cmsTemplateApi, accessControlManager, partyRoleManager, abstractBlockRepository);
        this.cmsTemplateApi = cmsTemplateApi;
        this.accessControlManager = accessControlManager;
        this.partyRoleManager = partyRoleManager;
        this.abstractBlockRepository = abstractBlockRepository;
        this.defaultPageRepository = defaultPageRepository;
        this.emailTemplateRepository = emailTemplateRepository;
        this.userService = userService;
        this.carService = carService;
        this.brandService = brandService;
    }

    @Override
    protected void deleteExampleData() {
        super.deleteExampleData();
        carService.deleteAll();
        brandService.deleteAll();
    }

    @Override
    public synchronized void loadExampleSiteData(Sites.Site site) {
        super.loadExampleSiteData(site);

        createTeaserBlocks(site);
        createWidgetExampleBlock(site);
        createErrorTemplates(site);
        createWelcomeTemplate(site);
        createChangeEmailTemplate(site);
        createCrudExampleData();
        createCarInventoryBlock(site);
    }

    @Override
    protected void deleteEssentialData() {
        super.deleteEssentialData();

        emailTemplateRepository.deleteAll();
    }

    @Override
    public synchronized void loadEssentialSiteData(Sites.Site site) {
        super.loadEssentialSiteData(site);

        EmailTemplate template = new EmailTemplate();
        template.setTemplateKey(EMAIL_KEY_PASSWORD_RESET);
        template.setSite(site.key);
        template.setSender("admin@localhost");
        template.setCategory(EmailTemplate.EmailTemplateCategory.EXTERN);
        template.getContent().set("en", "<p>Click next link in order to change your password</p>" +
                "<p><a href=\"{url}\">{url}</a></p> ");
        template.getContent().set("de", "<p>Click next link in order to change your password</p>" +
                "<p><a href=\"{url}\">{url}</a></p>  ");
        template.getDescription().set("en", "Password reset requested.");
        template.getDescription().set("en", "Password reset requested.");
        template.getSubject().set("en", "Password reset requested.");
        template.getSubject().set("de", "Password reset requested.");
        emailTemplateRepository.save(template);
    }

    @Override
    protected void createBackendNavigation(Sites.Site site) {
        super.createBackendNavigation(site);

        AbstractBlock root = abstractBlockRepository.findBackend(site).get();

        // Adding our example apps to the backend

        String key = "_backend_myapp";

        if (!abstractBlockRepository.findByKey(site, key).isPresent()) {
            BackendLinkBlock page = new BackendLinkBlock();
            root.getSubBlocks().add(page);
            page.setParentBlock(root);
            page.setKey(key);
            page.getLinkTarget().set("en", "/admin/myapp");
            page.getLinkTarget().set("de", "/admin/myapp");
            page.getNavTitle().set("en", "My app");
            page.getNavTitle().set("de", "Meine app");
            page.setLinkIcon("fa-link");
            abstractBlockRepository.save(page);
        }

        String crud_page_key = "_backend_crud_example";

        if (!abstractBlockRepository.findByKey(site, crud_page_key).isPresent()) {
            BackendLinkBlock page = new BackendLinkBlock();
            root.getSubBlocks().add(page);
            page.setParentBlock(root);
            page.setKey(crud_page_key);
            page.getLinkTarget().set("en", crud.controller.routes.CarController.list().url());
            page.getLinkTarget().set("de", crud.controller.routes.CarController.list().url());
            page.getNavTitle().set("en", "CRUD Example");
            page.getNavTitle().set("de", "CRUD Beispiel");
            page.setLinkIcon("fa-link");
            abstractBlockRepository.save(page);
        }
    }

    @Override
    protected void createPartyRoles() {
        super.createPartyRoles();

        PartyRole demoRole = partyRoleManager.create(ROLE_DEMO_ROLE);

        // Add default permissions to new DemoRole
        accessControlManager.allowPermission(demoRole, ApplicationPermission.BROWSE_BACKEND);
        accessControlManager.allowPermission(demoRole, BlockPermission.MODIFY);
    }

    @Override
    protected void createParties() {
        super.createParties();

        PartyRole superuserRole = partyRoleManager.findOneByName(DefaultPartyRole.ROLE_SUPERUSER);
        PartyRole demoRole = partyRoleManager.findOneByName(ROLE_DEMO_ROLE);
        PartyRole userRole = partyRoleManager.findOneByName(ROLE_USER);

        if (!userService.findOneByEmail("admin@insign.ch").isPresent()) {
            User superuser = new User();
            superuser.setName("admin");
            superuser.setCredentials("temp123");
            superuser.setEmail("admin@insign.ch");
            superuser.setFirstName("admin");
            superuser.setLastName("insign");
            superuser.setGender(ISOGender.MALE);
            superuser = userService.create(superuser);
            superuser.addRole(superuserRole);
            userService.save(superuser);
        }

        if (!userService.findOneByEmail("demouser@insign.ch").isPresent()) {
            User demouser = new User();
            demouser.setName("demouser");
            demouser.setCredentials("temp123");
            demouser.setEmail("demouser@insign.ch");
            demouser.setFirstName("demouser");
            demouser.setLastName("insign");
            demouser.setGender(ISOGender.MALE);
            demouser = userService.create(demouser);
            demouser.addRole(demoRole);
            demouser.addRole(userRole);
            userService.save(demouser);
        }
    }

    private void createTeaserBlocks(Sites.Site site) {
        logger.info("Creating teaser blocks");

        AbstractBlock homepage = abstractBlockRepository.findByKey(site, PageBlock.KEY_HOMEPAGE).get();
        CollectionBlock bottomPane = cmsTemplateApi.addBlockToSlot(HorizontalCollectionBlock.class, homepage, "bottom");
        abstractBlockRepository.save(bottomPane);

        TeaserBlock block, block2, block3;
        block = new TeaserBlock();
        block.getTitle().set("en", "Teaser block");
        block.getSubtitle().set("en", "Lorep Ipsum");
        block.getContent().set("en", "This is an example of customizing cms content blocks");
        block.getLogoUrl().set("en", "/assets/images/yacht.png");
        block.getLinkUrl().set("en", "http://play-cms.com/doc/cms/create-your-own-cms-block");
        block.getLinkText().set("en", "Learn more");
        block.setSite(site.key);
        abstractBlockRepository.addSubBlock(bottomPane, abstractBlockRepository.save(block));

        block2 = new TeaserBlock();
        block2.getTitle().set("en", "Teaser block");
        block2.getSubtitle().set("en", "Lorep Ipsum");
        block2.getContent().set("en", "This is an example of customizing cms content blocks");
        block2.getLogoUrl().set("en", "/assets/images/yacht2.jpg");
        block2.getLinkUrl().set("en", "http://play-cms.com/doc/cms/create-your-own-cms-block");
        block2.getLinkText().set("en", "Learn more");
        block2.setSite(site.key);
        abstractBlockRepository.addSubBlock(bottomPane, abstractBlockRepository.save(block2));

        block3 = new TeaserBlock();
        block3.getTitle().set("en", "Teaser block");
        block3.getSubtitle().set("en", "Lorep Ipsum");
        block3.getContent().set("en", "This is an example of customizing cms content blocks");
        block3.getLogoUrl().set("en", "/assets/images/yacht3.jpg");
        block3.getLinkUrl().set("en", "http://play-cms.com/doc/cms/create-your-own-cms-block");
        block3.getLinkText().set("en", "Learn more");
        block3.setSite(site.key);
        abstractBlockRepository.addSubBlock(bottomPane, abstractBlockRepository.save(block3));
    }

    private void createWidgetExampleBlock(Sites.Site site) {
        logger.info("Creating widget example block");

        DefaultPage homepage = defaultPageRepository.findByKey(site, PageBlock.KEY_HOMEPAGE)
                .map(page -> {
                    page.setActivateSlider(true);
                    return abstractBlockRepository.save(page);
                })
                .get();

        CollectionBlock sidebar = abstractBlockRepository.save(cmsTemplateApi.addBlockToSlot(CollectionBlock.class, homepage, "sidebar"));

        ContentBlock contentBlock = abstractBlockRepository.addSubBlock(sidebar, new ContentBlock());
        contentBlock.getTitle().set("en", "Widgets");
        contentBlock.getContent().set("en",
                "<p>Content filters are a great tool to let the user add variables freely inside any content, " +
                        "e.g. [[currentUserCount]] which your filter resolves to the current value " +
                        "when displaying the page.</p>\n" +
                        "<h4>Content filter example.</h4>\n" +
                        "<p>Last registered users: [[registeredUsersWidget:5]]</p>\n" +
                        "<p><a href=\"http://play-cms.com/\">" +
                        "Learn more </a> about filter framework</p>");
        abstractBlockRepository.save(contentBlock);
    }

    private void createCarInventoryBlock(Sites.Site site) {
        logger.info("Creating car inventory block");

        String frontendBlockKeyForCRUD = "_frontend_crud_example";

        if (!abstractBlockRepository.findByKey(site, frontendBlockKeyForCRUD).isPresent()) {
            CarInventoryPage page = new CarInventoryPage();
            page.setSite(site.key);
            page.setDealerTitle("Your favourite car dealer");
            page.setKey(frontendBlockKeyForCRUD);
            page.getPageTitle().set("en", "Car Inventory");
            page.getPageTitle().set("de", "Car Inventory");
            page.getMetaTitle().set("en", "Car Inventory");
            page.getMetaTitle().set("de", "Car Inventory");
            page.getNavTitle().set("en", "Car Inventory");
            page.getNavTitle().set("de", "Car Inventory");
            abstractBlockRepository.findFrontend(site).ifPresent((frontendBlock) -> {
                frontendBlock.getSubBlocks().add(page);
                page.setParentBlock(frontendBlock);
            });

            createNavItem(page, "/carInventory", "en", site);
            page.getVpath().set("en", "/carInventory");

            abstractBlockRepository.save(page);
        }
    }

    private void createCrudExampleData() {
        deleteExampleData();
        logger.info("Adding CRUD example data");

        Brand brand1 = new Brand();
        brand1.setTitle("Porsche");
        brandService.save(brand1);

        Brand brand2 = new Brand();
        brand2.setTitle("OPEL");
        brandService.save(brand2);

        Car car1 = new Car();
        car1.setModel("Carrera");
        car1.setBuyDate(Date.from(Instant.now()));
        car1.setBrand(brand1);
        car1.setPrice(new BigDecimal(15000));
        car1.setRegistrationId("AA 5676 CH");
        carService.save(car1);

        Car car2 = new Car();
        car2.setModel("Boxter");
        car2.setBuyDate(Date.from(Instant.now()));
        car2.setBrand(brand1);
        car2.setPrice(new BigDecimal(20000));
        car2.setRegistrationId("M78699");
        carService.save(car2);

        Car car3 = new Car();
        car3.setModel("Targa");
        car3.setBrand(brand1);
        car3.setPrice(new BigDecimal(11455));
        car3.setRegistrationId("CD ZH8 38");
        carService.save(car3);

        Car car4 = new Car();
        car4.setModel("Kadett");
        car4.setBrand(brand2);
        car4.setPrice(new BigDecimal(10500));
        car4.setRegistrationId("CD Z56461");
        carService.save(car4);
    }

    @Override
    protected PageBlock getNewPageInstance() {
        return new DefaultPage();
    }

    @Override
    protected ErrorPage getErrorPageInstance() {
        return new DefaultErrorPage();
    }

    /**
     * Create an EmailTemplate for sending email to user after password reset
     */
    private void createErrorTemplates(Sites.Site site) {
        logger.info("Creating error templates");

        EmailTemplate template = new EmailTemplate();
        template.setTemplateKey(EMAIL_KEY_PASSWORD_RECOVERY_SUCCESS);
        template.setSender("admin@localhost");
        template.setCategory(EmailTemplate.EmailTemplateCategory.EXTERN);
        template.getContent().set("en", "Hello {name}. Password for your account has been updated. ");
        template.getContent().set("de", "Hello {name}. Password for your account has been updated. ");
        template.getDescription().set("en", "Password has been updated.");
        template.getDescription().set("en", "Password has been updated.");
        template.getSubject().set("en", "Password has been updated.");
        template.getSubject().set("de", "Password has been updated.");
        template.setSite(site.key);

        emailTemplateRepository.save(template);
    }

    /**
     * Create an EmailTemplate for sending email to user after registration
     */
    private void createWelcomeTemplate(Sites.Site site) {
        logger.info("Creating welcome email template");

        EmailTemplate template = new EmailTemplate();
        template.setTemplateKey(EMAIL_KEY_REGISTRATION_WELCOME);
        template.setSender("admin@localhost");
        template.setCategory(EmailTemplate.EmailTemplateCategory.EXTERN);
        template.getContent().set("en", "Hello {firstname} {lastname}. New account for this email {email} has been created. ");
        template.getContent().set("de", "Hello {firstname} {lastname}. New account for this email {email} has been created. ");
        template.getDescription().set("en", "Welcome to play-cms-demo");
        template.getDescription().set("en", "Welcome to play-cms-demo.");
        template.getSubject().set("en", "Welcome to play-cms-demo.");
        template.getSubject().set("de", "Welcome to play-cms-demo.");
        template.setSite(site.key);

        emailTemplateRepository.save(template);
    }

    /**
     * Create an EmailTemplate for sending email to user after change email
     */
    private void createChangeEmailTemplate(Sites.Site site) {
        logger.info("Creating welcome email template");

        EmailTemplate template = new EmailTemplate();
        template.setTemplateKey(EMAIL_KEY_CHANGE_EMAIL);
        template.setSender("admin@localhost");
        template.setCategory(EmailTemplate.EmailTemplateCategory.EXTERN);
        template.getContent().set("en", "Hello {firstname} {lastname}. Email for your account has been changed. Old email: {oldEmail}. New email: {newEmail}.");
        template.getContent().set("de", "Hello {firstname} {lastname}. Email for your account has been changed. Old email: {oldEmail}. New email: {newEmail}.");
        template.getDescription().set("en", "Email was changed for account on play-cms-demo.");
        template.getDescription().set("en", "Email was changed for account on play-cms-demo.");
        template.getSubject().set("en", "Email was changed for account on play-cms-demo.");
        template.getSubject().set("de", "Email was changed for account on play-cms-demo.");
        template.setSite(site.key);

        emailTemplateRepository.save(template);
    }

}
