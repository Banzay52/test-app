package data.validator;

import ch.insign.cms.CMSApi;
import ch.insign.playauth.PlayAuthApi;
import ch.insign.playauth.party.Party;
import ch.insign.playauth.party.PartyRepository;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import party.User;
import play.data.Form;
import play.data.validation.ValidationError;
import play.mvc.Http;

import java.util.ArrayList;
import java.util.List;

public class UserProfileFormValidator<UserProfileForm> extends FormValidator<UserProfileForm> {

    private final PartyRepository<User> partyRepository;
    private final PlayAuthApi playAuthApi;
    private final CMSApi cmsApi;

    @Inject
    public UserProfileFormValidator(PartyRepository<User> partyRepository, PlayAuthApi playAuthApi, CMSApi cmsApi) {
        this.partyRepository = partyRepository;
        this.playAuthApi = playAuthApi;
        this.cmsApi = cmsApi;
    }

    @Override
    protected List<ValidationError> validateOnCustomErrors(Form<UserProfileForm> form, Http.Request request) {
        List<ValidationError> errors = new ArrayList<>();
        String email = form.rawData().get("email");
        boolean emailNotValid = true;
        if(StringUtils.isNotBlank(email)) {
            emailNotValid = partyRepository.findOneByEmail(email)
                    .map(Party::getId)
                    .map(id -> playAuthApi.getCurrentParty(request)
                            .map(Party::getId)
                            .filter(id::equals)
                            .isPresent())
                    .orElse(false);
        }
        if(emailNotValid) {
            errors.add(new ValidationError("email", "error.user.email_exists"));
        }

        String language = form.rawData().get("language");
        if(language != null
                && !cmsApi.getConfig().frontendLanguages(request).stream().anyMatch(language::equals)){
            errors.add(new ValidationError("language", "user.register.language.wrong"));
        }

        String password = form.rawData().get("password");
        if (StringUtils.isBlank(password) ||
                playAuthApi.getCurrentParty(request)
                .map(Party::getCredentials)
                .map(String::valueOf)
                .map(encrypted -> !playAuthApi.getPasswordService().passwordsMatch(password, encrypted))
                .orElse(false)) {
            errors.add(new ValidationError("password", "account.dashboard.editProfile.password.wrong"));
        }

        return errors;
    }
}
