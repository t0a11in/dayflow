package cl.dayflow.api.user.application;

import cl.dayflow.api.user.domain.RoleCode;
import cl.dayflow.api.user.infrastructure.persistence.RoleEntity;
import cl.dayflow.api.user.infrastructure.persistence.RoleJpaRepository;
import cl.dayflow.api.user.infrastructure.persistence.UserEntity;
import cl.dayflow.api.user.infrastructure.persistence.UserJpaRepository;
import java.util.List;
import java.util.Set;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
@ConditionalOnProperty(prefix = "dayflow.bootstrap.admin", name = "enabled", havingValue = "true")
public class InitialAdminBootstrapper implements ApplicationRunner {

    private final InitialAdminProperties properties;
    private final UserJpaRepository userRepository;
    private final RoleJpaRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public InitialAdminBootstrapper(
            InitialAdminProperties properties,
            UserJpaRepository userRepository,
            RoleJpaRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.properties = properties;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        validateProperties();
        String normalizedEmail = properties.email().trim().toLowerCase();
        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            return;
        }

        List<RoleEntity> roles = roleRepository.findByCodeIn(Set.of(RoleCode.ADMIN, RoleCode.USER));
        if (roles.size() != 2) {
            throw new IllegalStateException("Initial roles are not available");
        }

        UserEntity user = UserEntity.create(
                normalizedEmail,
                passwordEncoder.encode(properties.password()),
                properties.firstName(),
                properties.lastName(),
                Set.copyOf(roles));
        userRepository.save(user);
    }

    private void validateProperties() {
        if (isBlank(properties.email()) || isBlank(properties.password())
                || isBlank(properties.firstName()) || isBlank(properties.lastName())) {
            throw new IllegalStateException("Initial admin bootstrap requires email, password, first name, and last name");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
