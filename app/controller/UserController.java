package controller;

import ch.insign.cms.CMSApi;
import ch.insign.cms.controllers.GlobalActionWrapper;
import ch.insign.cms.email.EmailService;
import ch.insign.cms.forms.MyIdentity;
import ch.insign.cms.models.EmailTemplate;
import ch.insign.cms.models.StandalonePage;
import ch.insign.cms.models.party.PartyEvents;
import ch.insign.cms.repositories.EmailTemplateRepository;
import ch.insign.cms.utils.AjaxResult;
import ch.insign.cms.utils.Error;
import ch.insign.commons.db.MString;
import ch.insign.commons.i18n.Language;
import ch.insign.playauth.PlayAuthApi;
import ch.insign.playauth.authz.AccessControlManager;
import ch.insign.playauth.party.PartyRoleManager;
import ch.insign.playauth.party.support.DefaultPartyRole;
import ch.insign.playauth.permissions.PartyPermission;
import data.form.RegisterUserForm;
import data.form.ResetPasswordForm;
import data.mapper.UserMapper;
import data.model.TokenAction;
import data.validator.RegisterUserFormValidator;
import org.apache.commons.lang3.StringUtils;
import party.User;
import party.UserRepository;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.i18n.MessagesApi;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static util.DemoProjectBootstrapper.EMAIL_KEY_PASSWORD_RESET;
import static util.DemoProjectBootstrapper.EMAIL_KEY_REGISTRATION_WELCOME;

@With(GlobalActionWrapper.class)
public class UserController extends Controller {

    private final MessagesApi messagesApi;
    private final JPAApi jpaApi;
    private final CMSApi cmsApi;
    private final PlayAuthApi playAuthApi;
    private final Error error;
    private final AccessControlManager acm;
    private final PartyRoleManager partyRoleManager;
    private final PartyEvents partyEvents;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final EmailTemplateRepository<EmailTemplate> emailTemplateRepository;
    private final EmailService emailService;
    private final FormFactory formFactory;
    private final RegisterUserFormValidator registerUserFormValidator;
    private final views.html.user.register register;
    private final views.html.user.resetPassword resetPassword;
    private final views.html.user.resetPasswordConfirmation resetPasswordConfirmation;

    @Inject
    public UserController(
            MessagesApi messagesApi,
            JPAApi jpaApi,
            CMSApi cmsApi,
            PlayAuthApi playAuthApi,
            Error error,
            AccessControlManager acm,
            PartyRoleManager partyRoleManager,
            PartyEvents partyEvents,
            UserMapper userMapper,
            UserRepository userRepository,
            EmailTemplateRepository<EmailTemplate> emailTemplateRepository,
            EmailService emailService,
            FormFactory formFactory,
            RegisterUserFormValidator registerUserFormValidator,
            views.html.user.register register,
            views.html.user.resetPassword resetPassword,
            views.html.user.resetPasswordConfirmation resetPasswordConfirmation) {
        this.messagesApi = messagesApi;
        this.jpaApi = jpaApi;
        this.cmsApi = cmsApi;
        this.playAuthApi = playAuthApi;
        this.error = error;
        this.acm = acm;
        this.partyRoleManager = partyRoleManager;
        this.partyEvents = partyEvents;
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.emailTemplateRepository = emailTemplateRepository;
        this.emailService = emailService;
        this.formFactory = formFactory;
        this.registerUserFormValidator = registerUserFormValidator;
        this.register = register;
        this.resetPassword = resetPassword;
        this.resetPasswordConfirmation = resetPasswordConfirmation;
    }

    public Result showRegister(Http.Request request, String email) {
        RegisterUserForm newUserForm = new RegisterUserForm();
        if (StringUtils.isNotBlank(email)) {
            newUserForm.setEmail(email);
        }
        Form<RegisterUserForm> registerForm = formFactory.form(RegisterUserForm.class)
                .fill(newUserForm);

        return ok(cmsApi.getFilterManager().processOutput(
                register.render(
                        createStandalonePage(Language.getCurrentLanguage(request), messagesApi.preferred(request).at("register.title")),
                        registerForm, request), null));
    }

    public Result doRegister(Http.Request request) {
        Form<RegisterUserForm> form = formFactory.form(RegisterUserForm.class).bindFromRequest(request);

        form = registerUserFormValidator.validate(form, request);

        if (form.hasErrors()) {
            return badRequest(cmsApi.getFilterManager().processOutput(
                    register.render(
                            createStandalonePage(Language.getCurrentLanguage(request), messagesApi.preferred(request).at("register.title")),
                            form, request), null));
        }

        RegisterUserForm userForm = form.get();
        User user = userMapper.fromForm(userForm);
        user.setName((user.getFirstName() + " " + user.getLastName()).trim());
        user.addRole(partyRoleManager.findOneByName(DefaultPartyRole.ROLE_USER));
        user.setCredentials(playAuthApi.getPasswordService().encryptPassword(form.get().getPassword()));
        user = userRepository.save(user);

        playAuthApi.withSubject(user, request);

        // Add permissions for the created user
        acm.allowPermission(user, PartyPermission.READ, user);
        acm.allowPermission(user, PartyPermission.EDIT, user);
        acm.allowPermission(user, PartyPermission.REQUEST_PASSWORD_RESET, user);
        acm.allowPermission(user, PartyPermission.EDIT_PASSWORD, user);
        sendWelcomeEmail(request, user);

        return redirect(controller.routes.AccountController.dashboard().url())
                .flashing("success-disappear", messagesApi.preferred(request).at("user.register.success"));
    }

    public Result sendResetPasswordConfirmation(Http.Request request) {
        final Form<MyIdentity> form = formFactory.form(MyIdentity.class).bindFromRequest(request);

        if (form.hasErrors()) {
            return AjaxResult.error(form);
        }

        userRepository.findOneByEmail(form.get().getEmail())
                .ifPresent(user -> sendEmailRestorePassword(request, user));

        return redirect(controller.routes.UserController.showResetPasswordPage())
                .flashing("success-disappear", messagesApi.preferred(request).at("reset.password.msg.reset_email_sent"));
    }

    public Result showResetPasswordPage(Http.Request request) {
        Form<MyIdentity> form = formFactory.form(MyIdentity.class);
        return ok(cmsApi.getFilterManager().processOutput(
                resetPassword.render(
                        createStandalonePage(Language.getCurrentLanguage(request), messagesApi.preferred(request).at("reset.password.title")),
                        form, request),
                null));
    }

    public Result showResetPasswordConfirmationPage(Http.Request request, String token, String email) {
        Optional<TokenAction> maybeTokenAction = jpaApi.withTransaction(em -> {
            return em.createNamedQuery("TokenAction.findByToken", TokenAction.class)
                    .setParameter("token", token)
                    .getResultList()
                    .stream()
                    .findFirst();
        });

        if (!maybeTokenAction.isPresent()) {
            return error.notFound(messagesApi.preferred(request).at("reset.password.msg.token_not_exists")).apply(request);
        }

        TokenAction tokenAction = maybeTokenAction.get();

        if (tokenAction.isExpired()) {
            return error.notFound(messagesApi.preferred(request).at("reset.password.msg.token_expired")).apply(request);
        }

        User user = tokenAction.getTargetUser();

        if (!StringUtils.equals(email, user.getEmail())) {
            return error.notFound(messagesApi.preferred(request).at("reset.password.msg.invalid_email")).apply(request);
        }

        ResetPasswordForm formData = new ResetPasswordForm();
        formData.setToken(tokenAction.getToken());

        Form<ResetPasswordForm> resetPasswordForm = formFactory
                .form(ResetPasswordForm.class)
                .fill(formData);

        return ok(cmsApi.getFilterManager().processOutput(
                resetPasswordConfirmation.render(
                        createStandalonePage(Language.getCurrentLanguage(request), messagesApi.preferred(request).at("reset.password.title")),
                        resetPasswordForm, request),
                null));
    }

    public Result doResetPassword(Http.Request request) {
        final Form<ResetPasswordForm> resetPasswordForm = formFactory
                .form(ResetPasswordForm.class)
                .bindFromRequest(request);

        if (resetPasswordForm.hasErrors()) {
            return badRequest(cmsApi.getFilterManager().processOutput(
                    resetPasswordConfirmation.render(
                            createStandalonePage(Language.getCurrentLanguage(request), messagesApi.preferred(request).at("reset.password.title")),
                            resetPasswordForm, request),
                    null));
        }

        ResetPasswordForm formData = resetPasswordForm.get();

        return jpaApi.withTransaction(em -> {
            Optional<TokenAction> maybeTokenAction = em
                    .createNamedQuery("TokenAction.findByToken", TokenAction.class)
                    .setParameter("token", formData.getToken())
                    .getResultList()
                    .stream()
                    .findFirst();

            if (!maybeTokenAction.isPresent()) {
                return error.notFound(messagesApi.preferred(request).at("reset.password.msg.token_not_exists")).apply(request);
            }

            TokenAction tokenAction = maybeTokenAction.get();

            if (tokenAction.isExpired()) {
                return error.notFound(messagesApi.preferred(request).at("reset.password.msg.token_expired")).apply(request);
            }

            User user = tokenAction.getTargetUser();
            user.setCredentials(playAuthApi.getPasswordService().encryptPassword(formData.getPassword()));
            user = em.merge(user);

            partyEvents.onPasswordUpdate(resetPasswordForm, user, request);

            em.remove(tokenAction);

            playAuthApi.withSubject(user, request);

            return redirect(controller.routes.AccountController.dashboard())
                    .flashing("success-disappear", messagesApi.preferred(request).at("reset.password.msg.success"));
        });
    }

    /**
     * Send email with link to reset password
     */
    private void sendEmailRestorePassword(Http.Request request, User user) {
        final String token = UUID.randomUUID().toString();
        TokenAction ta = TokenAction.create(token, user, user.getEmail());
        jpaApi.withTransaction(em -> { em.persist(ta); });

        HashMap<String, String> emailData = new HashMap<>();
        emailData.put("firstname", user.getFirstName());
        emailData.put("lastname", user.getLastName());
        emailData.put("url", controller.routes.UserController.showResetPasswordConfirmationPage(token, user.getEmail())
                .absoluteURL(request, true));

        emailTemplateRepository.findByKey(EMAIL_KEY_PASSWORD_RESET, cmsApi.getSites().current(request).key)
                .ifPresent(emailTemplate -> emailService.send(
                        emailService.fromTemplate(emailTemplate, Language.getCurrentLang(request))
                                .addTo(user.getEmail()),
                        Language.getCurrentLang(request),
                        emailData
                ));
    }

    /**
     * Send email with welcome message after registration
     */
    private void sendWelcomeEmail(Http.Request request, User user) {
        HashMap<String, String> emailData = new HashMap<>();
        emailData.put("firstname", user.getFirstName());
        emailData.put("lastname", user.getLastName());
        emailData.put("email", user.getEmail());

        emailTemplateRepository.findByKey(EMAIL_KEY_REGISTRATION_WELCOME, cmsApi.getSites().current(request).key)
                .ifPresent(emailTemplate -> emailService.send(
                        emailService.fromTemplate(emailTemplate, Language.getCurrentLang(request))
                                .addTo(user.getEmail()),
                        Language.getCurrentLang(request),
                        emailData
                ));
    }

    private StandalonePage createStandalonePage(String lang, String title){
        MString pageTitle = new MString();
        pageTitle.set(lang, title);
        return new StandalonePage(pageTitle, null);
    }
}
