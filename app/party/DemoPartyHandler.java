package party;

import ch.insign.cms.CMSApi;
import ch.insign.cms.email.EmailService;
import ch.insign.cms.models.EmailTemplate;
import ch.insign.cms.models.party.DefaultPartyHandler;
import ch.insign.cms.repositories.EmailTemplateRepository;
import ch.insign.commons.i18n.Language;
import ch.insign.playauth.PlayAuthApi;
import ch.insign.playauth.party.Party;
import ch.insign.playauth.party.support.DefaultPartyRole;
import controller.AccountController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.i18n.Lang;
import play.mvc.Http;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;

import static util.DemoProjectBootstrapper.EMAIL_KEY_PASSWORD_RECOVERY_SUCCESS;


public class DemoPartyHandler extends DefaultPartyHandler {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private final CMSApi cmsApi;
    private final PlayAuthApi playAuthApi;
    private final EmailTemplateRepository<EmailTemplate> emailTemplateRepository;
    private final EmailService emailService;
    private final UserService userService;

    @Inject
    public DemoPartyHandler(CMSApi cmsApi,
                            PlayAuthApi playAuthApi,
                            EmailTemplateRepository<EmailTemplate> emailTemplateRepository,
                            EmailService emailService,
                            UserService userService) {
        this.cmsApi = cmsApi;
        this.playAuthApi = playAuthApi;
        this.emailTemplateRepository = emailTemplateRepository;
        this.emailService = emailService;
        this.userService = userService;
    }

    @Override
    public void onCreate(Form form, Party party) {
        party.addRole(playAuthApi.getPartyRoleManager().findOneByName(DefaultPartyRole.ROLE_USER));

        User user = (User) party;
        User userForm = (User) form.get();
        if (!StringUtils.isBlank(userForm.getImage())) {
            try {
                userService.saveFileWithUniqueAvatarName(user, userForm.getImage());
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        user.addRole(playAuthApi.getPartyRoleManager().findOneByName(DefaultPartyRole.ROLE_USER));
        userService.save(user);
    }

    @Override
    public void onUpdate(Form form, Party party) {
        User user = (User) party;
        User userForm = (User) form.get();
        if (!StringUtils.isBlank(userForm.getImage())) {
            try {
                userService.saveFileWithUniqueAvatarName(user, userForm.getImage());
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        } else {
            userService.deletePreviousAvatarImage(user);
            user.setImage(null);
        }

    }

    @Override
    public void onPasswordUpdate(Form form, Party party, Http.Request request) {
        User user = (User) party;
        HashMap<String, String> emailData = new HashMap<>();
        emailData.put("name", user.getName());

        emailTemplateRepository.findByKey(EMAIL_KEY_PASSWORD_RECOVERY_SUCCESS, cmsApi.getSites().current(request).key)
                .ifPresent(emailTemplate -> emailService.send(
                        emailService.fromTemplate(
                                emailTemplate,
                                Language.getCurrentLang(request)
                        ).addTo(user.getEmail()),
                        Language.getCurrentLang(request),
                        emailData
                ));
    }

}
