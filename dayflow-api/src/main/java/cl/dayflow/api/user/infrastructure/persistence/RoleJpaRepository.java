package cl.dayflow.api.user.infrastructure.persistence;

import cl.dayflow.api.user.domain.RoleCode;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleJpaRepository extends JpaRepository<RoleEntity, Long> {

    List<RoleEntity> findByCodeIn(Collection<RoleCode> codes);
}
