package data.form;

import ch.insign.playauth.party.ISOGender;
import play.data.validation.Constraints;

import static data.form.RegisterUserForm.PHONE_PATTERN;

public class UserProfileForm {

    @Constraints.Required
    @Constraints.Email
    private String email;
    @Constraints.Required
    private String password;
    @Constraints.Required
    private ISOGender gender;
    @Constraints.Required
    @Constraints.MaxLength(25)
    private String firstName;
    @Constraints.Required
    @Constraints.MaxLength(30)
    private String lastName;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
