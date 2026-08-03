package cl.dayflow.api.user.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Page<UserEntity> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    Page<UserEntity> findByEmailContainingIgnoreCaseAndActive(String email, boolean active, Pageable pageable);
}
