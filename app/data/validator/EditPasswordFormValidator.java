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

public class EditPasswordFormValidator<EditPasswordForm> extends FormValidator<EditPasswordForm> {
    private final PlayAuthApi playAuthApi;

    @Inject
    public EditPasswordFormValidator(PlayAuthApi playAuthApi) {
        this.playAuthApi = playAuthApi;
    }

    @Override
    protected List<ValidationError> validateOnCustomErrors(Form<EditPasswordForm> form, Http.Request request) {
        List<ValidationError> errors = new ArrayList<>();
        String password = form.rawData().get("oldPassword");
        if (StringUtils.isBlank(password) ||
                playAuthApi.getCurrentParty(request)
                        .map(Party::getCredentials)
                        .map(String::valueOf)
                        .map(encrypted -> !playAuthApi.getPasswordService().passwordsMatch(password, encrypted))
                        .orElse(false)) {
            errors.add(new ValidationError("oldPassword", "account.dashboard.editProfile.password.wrong"));
        }
        return errors;
    }
}

