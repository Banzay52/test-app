package party;

import ch.insign.playauth.party.ISOGender;
import ch.insign.playauth.party.support.DefaultParty;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.Date;

import static data.form.RegisterUserForm.PHONE_PATTERN;

/**
 * Demo user class extends DefaultParty with custom fields
 */
@Entity
public class User extends DefaultParty {

    @Constraints.Required
    @Constraints.MaxLength(25)
    private String firstName;

    @Constraints.Required
    @Constraints.MaxLength(30)
    private String lastName;

    private String image;

    @Constraints.Required
    @Enumerated(EnumType.STRING)
    public ISOGender gender;

    @Constraints.Pattern(value = PHONE_PATTERN, message = "user.register.phone.wrong.format")
    public String phone;

    public String language;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin;

    private int loginCount = 0;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
    public String getName() {
        return this.firstName + " " + this.lastName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(int logins) {
        this.loginCount = logins;
    }
}
