package data.form;

import ch.insign.playauth.party.ISOGender;
import ch.insign.playauth.party.RepeatedPassword;
import ch.insign.playauth.party.validation.PartyConstrains;
import play.data.validation.Constraints;

/**
 * Form to register a new user
 */
@PartyConstrains.ValidateRepeatedPassword
public class RegisterUserForm implements RepeatedPassword {

    /**
     * Password validation regex pattern
     *
     *  - must contains one digit from 0-9
     *  - must contains one lowercase or uppercase characters
     *  - length at least 6 characters
     */
    public static final String PASSWORD_PATTERN = "^((?=.*\\d)(?=.*[a-zA-Z])).{6,}$";
    /**
     * Phone validation regex pattern
     *
     *  - must contain from 6 to 11 characters, only digits
     */
    public static final String PHONE_PATTERN = "^\\d{6,11}$";

    @Constraints.Required
    @Constraints.Email
    private String email;
    @Constraints.Required
    private ISOGender gender;
    @Constraints.Required
    @Constraints.MaxLength(25)
    private String firstName;
    @Constraints.Required
    @Constraints.MaxLength(30)
    private String lastName;
    @Constraints.Required
    @Constraints.Pattern(value = PASSWORD_PATTERN, message = "auth.login.password.pattern.not.match")
    private String password;
    @Constraints.Required
    private String repeatPassword;
    @Constraints.Pattern(value = PHONE_PATTERN, message = "user.register.phone.wrong.format")
    private String phone;
    private String language;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ISOGender getGender() {
        return gender;
    }

    public void setGender(ISOGender gender) {
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getRepeatPassword() {
        return repeatPassword;
    }

    @Override
    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
