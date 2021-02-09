package data.model;

import party.User;
import play.data.format.Formats;
import javax.persistence.*;
import java.util.Date;

@NamedQueries({
        @NamedQuery(
                name = "TokenAction.findByToken",
                query = "SELECT ta FROM TokenAction ta WHERE ta.token = :token"
        ),
        @NamedQuery(
                name = "TokenAction.deleteByUser",
                query = "DELETE FROM TokenAction ta WHERE ta.targetUser = :user "
        )
})
@Entity
@Table(name = "demo_token_action")
public class TokenAction {

    /**
     * Time frame until token expiration in seconds. Defaults to one week
     */
    public final static long EXPIRATION_TIME = 7 * 24 * 3600;

    @Id
    @GeneratedValue
    private long id;

    @Column(unique = true)
    private String token;

    @ManyToOne
    private User targetUser;

    private String email;

    @Temporal(TemporalType.TIMESTAMP)
    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expires;

    /**
     * Convenience method for creating a new token action without an email
     *
     * @param token The generated token id
     * @param user The target user
     * @return The newly created (but not yet persisted) token action
     */
    public static TokenAction create(final String token, final User user) {
        return create(token, user, null);
    }

    /**
     * Convenience method for creating a new token action with an email
     *
     * @param token The generated token id
     * @param user The target user
     * @param email The email associated with this token action
     * @return The newly created (but not yet persisted) token action
     */
    public static TokenAction create(final String token, final User user, final String email) {
        final TokenAction tokenAction = new TokenAction();
        tokenAction.targetUser = user;
        tokenAction.email = email;
        tokenAction.token = token;

        final Date created = new Date();
        tokenAction.created = created;
        tokenAction.expires = new Date(created.getTime() + EXPIRATION_TIME * 1000);

        return tokenAction;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public boolean isExpired() {
        return this.expires.before(new Date());
    }
}
