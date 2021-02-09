package controller;

import ch.insign.cms.CMSApi;
import ch.insign.cms.controllers.GlobalActionWrapper;
import ch.insign.cms.email.EmailService;
import ch.insign.cms.models.CMS;
import ch.insign.cms.models.EmailTemplate;
import ch.insign.cms.models.StandalonePage;
import ch.insign.cms.repositories.EmailTemplateRepository;
import ch.insign.commons.db.MString;
import ch.insign.commons.i18n.Language;
import ch.insign.playauth.PlayAuthApi;
import ch.insign.playauth.controllers.actions.RequiresUser;
import ch.insign.playauth.party.Party;
import com.fasterxml.jackson.databind.node.ObjectNode;
import data.form.EditPasswordForm;
import data.form.UserProfileForm;
import data.mapper.UserProfileMapper;
import data.validator.EditPasswordFormValidator;
import data.validator.UserProfileFormValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import party.User;
import party.UserRepository;
import party.UserService;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.Lang;
import play.i18n.MessagesApi;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;
import views.html.account.dashboard;
import views.html.account.editPassword;
import views.html.account.editProfile;

import javax.inject.Inject;
import java.io.File;
import java.util.HashMap;
import java.util.Optional;

import static util.DemoProjectBootstrapper.EMAIL_KEY_CHANGE_EMAIL;
import static util.DemoProjectBootstrapper.EMAIL_KEY_PASSWORD_RECOVERY_SUCCESS;

@With(GlobalActionWrapper.class)
@RequiresUser
public class AccountController extends Controller {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private final PlayAuthApi playAuthApi;
    private final CMSApi cmsApi;
    private final MessagesApi messagesApi;
    private final UserRepository userRepository;
    private final UserService userService;
    private final FormFactory formFactory;
    private final UserProfileMapper userProfileMapper;
    private final EmailTemplateRepository<EmailTemplate> emailTemplateRepository;
    private final EmailService emailService;
    private final UserProfileFormValidator userProfileFormValidator;
    private final EditPasswordFormValidator editPasswordFormValidator;
    private final views.html.account.dashboard dashboard;
    private final views.html.account.editProfile editProfile;
    private final views.html.account.editPassword editPassword;

    @Inject
    public AccountController(
            PlayAuthApi playAuthApi,
            CMSApi cmsApi,
            UserProfileMapper userProfileMapper,
            UserRepository userRepository,
            MessagesApi messagesApi,
            FormFactory formFactory,
            EmailTemplateRepository<EmailTemplate> emailTemplateRepository,
            EmailService emailService,
            UserService userService,
            UserProfileFormValidator userProfileFormValidator,
            EditPasswordFormValidator editPasswordFormValidator,
            views.html.account.dashboard dashboard,
            views.html.account.editProfile editProfile,
            views.html.account.editPassword editPassword
    ) {
        this.playAuthApi = playAuthApi;
        this.cmsApi = cmsApi;
        this.userRepository = userRepository;
        this.messagesApi = messagesApi;
        this.userProfileMapper = userProfileMapper;
        this.formFactory = formFactory;
        this.emailTemplateRepository = emailTemplateRepository;
        this.emailService = emailService;
        this.userService = userService;
        this.userProfileFormValidator = userProfileFormValidator;
        this.editPasswordFormValidator = editPasswordFormValidator;
        this.dashboard = dashboard;
        this.editProfile = editProfile;
        this.editPassword = editPassword;
    }

    public Result dashboard(Http.Request request) {
        return ok(cmsApi.getFilterManager().processOutput(
                dashboard.render(
                        createStandalonePage(Language.getCurrentLanguage(request), messagesApi.preferred(request).at("account.dashboard")),
                        (User) playAuthApi.getCurrentParty(request).get(),
                        request),
                null));
    }

    public Result editProfile(Http.Request request) {
        User user = (User) playAuthApi.getCurrentParty(request).get();

        return ok(cmsApi.getFilterManager().processOutput(
                editProfile.render(
                        createStandalonePage(
                                Language.getCurrentLanguage(request),
                                messagesApi.preferred(request).at("account.dashboard.editProfile")),
                        formFactory.form(UserProfileForm.class).fill(userProfileMapper.toForm(user)),
                        user,request),
                null));
    }

    public Result doEditProfile(Http.Request request) {
        User user = (User) playAuthApi.getCurrentParty(request).get();
        Form<UserProfileForm> form = formFactory.form(UserProfileForm.class).bindFromRequest(request);
        form = userProfileFormValidator.validate(form, request);
        if (form.hasErrors()) {
            return badRequest(cmsApi.getFilterManager().processOutput(
                    editProfile.render(
                            createStandalonePage(
                                    Language.getCurrentLanguage(request),
                                    messagesApi.preferred(request).at("account.dashboard.editProfile")),
                            form,
                            user,
                            request),
                    null));
        }

        UserProfileForm userForm = form.get();
        String oldEmail = user.getEmail();

        user = userProfileMapper.update(userForm, user);
        user.setName((user.getFirstName() + " " + user.getLastName()).trim());
        userRepository.save(user);

        if (!user.getEmail().equals(oldEmail)){
            sendChangeEmailEmails(request, user, oldEmail);
        }

        return ok(cmsApi.getFilterManager().processOutput(
                editProfile.render(
                        createStandalonePage(
                                Language.getCurrentLanguage(request),
                                messagesApi.preferred(request).at("account.dashboard.editProfile")),
                        form,
                        user,
                        request),
                null))
                .flashing("success-disappear", messagesApi.preferred(request).at("account.dashboard.editProfile.success"));
    }

    public Result editPassword(Http.Request request) {
        User user = (User) playAuthApi.getCurrentParty(request).get();
        return ok(cmsApi.getFilterManager().processOutput(
                editPassword.render(
                        createStandalonePage(
                                Language.getCurrentLanguage(request),
                                messagesApi.preferred(request).at("account.dashboard.editPassword")),
                        formFactory.form(EditPasswordForm.class),
                        user,
                        request),
                null));
    }

    public Result doEditPassword(Http.Request request) {
        User user = (User) playAuthApi.getCurrentParty(request).get();
        Form<EditPasswordForm> form = formFactory.form(EditPasswordForm.class).bindFromRequest(request);
        form = editPasswordFormValidator.validate(form, request);

        if (form.hasErrors()) {
            return badRequest(cmsApi.getFilterManager().processOutput(
                    editPassword.render(
                            createStandalonePage(
                                    Language.getCurrentLanguage(request),
                                    messagesApi.preferred(request).at("account.dashboard.editPassword")),
                            form,
                            user,
                            request),
                    null));
        }

        user.setCredentials(playAuthApi.getPasswordService().encryptPassword(form.get().getPassword()));
        userService.save(user);
        sendChangePasswordConfirmationEmail(request, user);
        return redirect(controller.routes.AccountController.dashboard())
                .flashing("success-disappear", messagesApi.preferred(request).at("account.dashboard.editPassword.success"));
    }

    public Result uploadUserAvatar(Http.Request request) {
            ObjectNode result = Json.newObject();
            Optional<? extends Party> maybeUser = playAuthApi.getCurrentParty(request);
            Http.MultipartFormData body = request.body().asMultipartFormData();
            Http.MultipartFormData.FilePart picture = body.getFile("file");
            if (maybeUser.isPresent()) {
                User user =(User) maybeUser.get();
                userService.deletePreviousAvatarImage(user);
                user.setImage(null);
                if (picture != null && picture.getContentType().toLowerCase().startsWith("image")) {
                    File tmpFile = (File) picture.getFile();
                    try {
                        userService.saveFileWithUniqueAvatarName(user, tmpFile);
                    } catch (Exception e) {
                        logger.error("error: " + e.getMessage());
                        result.put("status", "error");
                        return internalServerError(result);
                    }
                    result.put("status", "ok");
                    result.put("filename", cmsApi.getConfig().imageUploadWWWRoot() + File.separator + user.getId());
                    return ok(result);
                } else {
                    result.put("status", "error");
                    return badRequest(result);
                }
            } else {
                result.put("status", "error");
                return forbidden(result);
            }
    }

    public Result deleteAvatarImage(Http.Request request) {
        Optional<? extends Party> maybeUser = playAuthApi.getCurrentParty(request);
        if(maybeUser.isPresent()){
            try {
                User user = (User) maybeUser.get();
                userService.deletePreviousAvatarImage(user);
                user.setImage(null);
                return redirect(controller.routes.AccountController.editProfile().url())
                        .flashing("success-disappear", messagesApi.preferred(request).at("account.dashboard.delete.image.success"));
            } catch (Exception e) {
                    e.printStackTrace();
                    logger.debug("error: " + e.getMessage());
                    return internalServerError("error");
            }

        } else {
            return forbidden("error");
        }
    }

    private void sendChangePasswordConfirmationEmail(Http.Request request, User user) {
        HashMap<String, String> emailData = new HashMap<>();
        emailData.put("name", user.getFirstName() + " " + user.getLastName());
        emailData.put("email", user.getEmail());

        emailTemplateRepository.findByKey(EMAIL_KEY_PASSWORD_RECOVERY_SUCCESS, cmsApi.getSites().current(request).key)
                .ifPresent(emailTemplate -> emailService.send(
                        emailService.fromTemplate(emailTemplate, Language.getCurrentLang(request))
                                .addTo(user.getEmail()),
                        Language.getCurrentLang(request),
                        emailData
                ));
    }

    /**
     * Send emails with info message when user change email
     */
    private void sendChangeEmailEmails (Http.Request request, User user, String oldEmail) {

        HashMap<String, String> emailData = new HashMap<>();
        emailData.put("firstname", user.getFirstName());
        emailData.put("lastname", user.getLastName());
        emailData.put("oldEmail", oldEmail);
        emailData.put("newEmail", user.getEmail());

        // send to new email
        emailTemplateRepository.findByKey(EMAIL_KEY_CHANGE_EMAIL, cmsApi.getSites().current(request).key)
                .ifPresent(emailTemplate -> emailService.send(
                        emailService.fromTemplate(emailTemplate, Language.getCurrentLang(request))
                                .addTo(user.getEmail()),
                        Language.getCurrentLang(request),
                        emailData
                ));

        // send to old email
        emailTemplateRepository.findByKey(EMAIL_KEY_CHANGE_EMAIL, cmsApi.getSites().current(request).key)
                .ifPresent(emailTemplate -> emailService.send(
                        emailService.fromTemplate(emailTemplate, Language.getCurrentLang(request))
                                .addTo(oldEmail),
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
