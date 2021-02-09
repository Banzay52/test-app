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

public class RegisterUserFormValidator<RegisterUserForm> extends FormValidator<RegisterUserForm> {
    private final PartyRepository<User> partyRepository;
    private final CMSApi cmsApi;

    @Inject
    public RegisterUserFormValidator(PartyRepository<User> partyRepository, CMSApi cmsApi) {
        this.partyRepository = partyRepository;
        this.cmsApi = cmsApi;
    }

    @Override
    protected List<ValidationError> validateOnCustomErrors(Form<RegisterUserForm> form, Http.Request request) {
        List<ValidationError> errors = new ArrayList<>();
        String email = form.rawData().get("email");
        boolean emailNotValid = true;
        if(StringUtils.isNotBlank(email)) {
            emailNotValid = partyRepository.findOneByEmail(email).isPresent();
        }
        if(emailNotValid) {
            errors.add(new ValidationError("email", "error.user.email_exists"));
        }

        String language = form.rawData().get("language");
        if(language != null
                && !cmsApi.getConfig().frontendLanguages(request).stream().anyMatch(language::equals)){
            errors.add(new ValidationError("language", "user.register.language.wrong"));
        }

        return errors;
    }
}
