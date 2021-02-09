package data.form;

import ch.insign.playauth.party.RepeatedPassword;
import ch.insign.playauth.party.validation.PartyConstrains;
import play.data.validation.Constraints;

import static data.form.RegisterUserForm.PASSWORD_PATTERN;

@PartyConstrains.ValidateRepeatedPassword
public class ResetPasswordForm implements RepeatedPassword {

    @Constraints.Required
    private String token;

    @Constraints.Required
    @Constraints.Pattern(value = PASSWORD_PATTERN, message = "auth.login.password.pattern.not.match")
    private String password;

    @Constraints.Required
    private String repeatPassword;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
}
