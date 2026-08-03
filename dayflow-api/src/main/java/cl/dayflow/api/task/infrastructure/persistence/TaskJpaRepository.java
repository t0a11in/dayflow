package cl.dayflow.api.task.infrastructure.persistence;

import cl.dayflow.api.task.domain.TaskPriority;
import cl.dayflow.api.task.domain.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskJpaRepository extends JpaRepository<TaskEntity, Long> {

    @Query("""
            select task from TaskEntity task
            where task.user.id = :userId
              and (:active is null or task.active = :active)
              and (:status is null or task.status = :status)
              and (:priority is null or task.priority = :priority)
              and (:title is null or lower(task.title) like lower(concat('%', :title, '%')))
            """)
    Page<TaskEntity> findByFilters(
            @Param("userId") Long userId,
            @Param("active") Boolean active,
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority,
            @Param("title") String title,
            Pageable pageable);
}
