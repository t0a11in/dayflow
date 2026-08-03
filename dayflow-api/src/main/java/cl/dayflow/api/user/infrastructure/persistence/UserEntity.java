package cl.dayflow.api.user.infrastructure.persistence;

import cl.dayflow.api.shared.auditing.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "app_users")
public class UserEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_users_sequence")
    @SequenceGenerator(name = "app_users_sequence", sequenceName = "app_users_seq", allocationSize = 1)
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 254)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "active", nullable = false)
    @JdbcTypeCode(Types.NUMERIC)
    private boolean active;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles = new HashSet<>();

    protected UserEntity() {
    }

    public static UserEntity create(String email, String passwordHash, String firstName, String lastName, Set<RoleEntity> roles) {
        UserEntity user = new UserEntity();
        user.email = email.trim().toLowerCase();
        user.passwordHash = passwordHash;
        user.firstName = firstName.trim();
        user.lastName = lastName.trim();
        user.active = true;
        user.roles.addAll(roles);
        return user;
    }

    public String getEmail() {
        return email;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isActive() {
        return active;
    }

    public Set<RoleEntity> getRoles() {
        return Set.copyOf(roles);
    }

    public void update(String email, String firstName, String lastName, Set<RoleEntity> roles) {
        this.email = email.trim().toLowerCase();
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.roles.clear();
        this.roles.addAll(roles);
    }

    public void changePassword(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void changeActiveStatus(boolean active) {
        this.active = active;
    }
}
