package cl.dayflow.api.task.infrastructure.persistence;

import cl.dayflow.api.shared.auditing.AuditableEntity;
import cl.dayflow.api.task.domain.TaskPriority;
import cl.dayflow.api.task.domain.TaskStatus;
import cl.dayflow.api.user.infrastructure.persistence.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.sql.Types;
import java.time.Instant;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "tasks")
public class TaskEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tasks_sequence")
    @SequenceGenerator(name = "tasks_sequence", sequenceName = "tasks_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", length = 4000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    private TaskPriority priority;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "due_date")
    private Instant dueDate;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "active", nullable = false)
    @JdbcTypeCode(Types.NUMERIC)
    private boolean active;

    protected TaskEntity() {
    }

    public static TaskEntity create(
            UserEntity user,
            String title,
            String description,
            TaskPriority priority,
            Instant startDate,
            Instant dueDate) {
        TaskEntity task = new TaskEntity();
        task.user = user;
        task.title = title.trim();
        task.description = normalizeDescription(description);
        task.status = TaskStatus.PENDING;
        task.priority = priority;
        task.startDate = startDate;
        task.dueDate = dueDate;
        task.active = true;
        return task;
    }

    public void update(String title, String description, TaskPriority priority, Instant startDate, Instant dueDate) {
        ensureModifiable();
        this.title = title.trim();
        this.description = normalizeDescription(description);
        this.priority = priority;
        this.startDate = startDate;
        this.dueDate = dueDate;
    }

    public void changeStatus(TaskStatus targetStatus) {
        ensureActive();
        if (!status.canTransitionTo(targetStatus)) {
            throw new IllegalStateException("Invalid task status transition from %s to %s".formatted(status, targetStatus));
        }
        this.status = targetStatus;
        this.completedAt = targetStatus == TaskStatus.COMPLETED ? Instant.now() : null;
    }

    public void complete() {
        ensureActive();
        if (status != TaskStatus.COMPLETED) {
            if (!status.canTransitionTo(TaskStatus.COMPLETED)) {
                throw new IllegalStateException("Task cannot be completed from status %s".formatted(status));
            }
            status = TaskStatus.COMPLETED;
            completedAt = Instant.now();
        }
    }

    public void deactivate() {
        active = false;
    }

    public Long getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public Instant getDueDate() {
        return dueDate;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public boolean isActive() {
        return active;
    }

    private void ensureModifiable() {
        ensureActive();
        if (status == TaskStatus.COMPLETED) {
            throw new IllegalStateException("Completed tasks require an explicit status operation before modification");
        }
    }

    private void ensureActive() {
        if (!active) {
            throw new IllegalStateException("Inactive tasks cannot be modified");
        }
    }

    private static String normalizeDescription(String description) {
        return description == null || description.isBlank() ? null : description.trim();
    }
}
