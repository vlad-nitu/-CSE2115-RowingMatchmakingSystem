package nl.tudelft.sem.template.authentication.domain.user;

import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.authentication.domain.HasEvents;

import javax.persistence.*;
import java.util.Objects;

/**
 * A DDD entity representing an application user in our domain.
 */
@Entity
@Table(name = "users")
@NoArgsConstructor
public class AppUser extends HasEvents {
    /**
     * Identifier for the application user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "userid", nullable = false, unique = true)
    @Convert(converter = UserIdAttributeConverter.class)
    private UserId userId;

    @Column(name = "password_hash", nullable = false)
    @Convert(converter = HashedPasswordAttributeConverter.class)
    private HashedPassword password;

    /**
     * Create new application user.
     *
     * @param userId The UserId for the new user
     * @param password The password for the new user
     */
    public AppUser(UserId userId, HashedPassword password) {
        this.userId = userId;
        this.password = password;
        this.recordThat(new UserWasCreatedEvent(userId));
    }

    public void changePassword(HashedPassword password) {
        this.password = password;
        this.recordThat(new PasswordWasChangedEvent(this));
    }

    public UserId getUserId() {
        return userId;
    }

    public HashedPassword getPassword() {
        return password;
    }

    /**
     * Equality is only based on the identifier.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AppUser appUser = (AppUser) o;
        return id == (appUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
