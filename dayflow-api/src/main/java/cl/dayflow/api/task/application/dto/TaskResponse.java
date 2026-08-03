package cl.dayflow.api.task.application.dto;

import cl.dayflow.api.task.domain.TaskPriority;
import cl.dayflow.api.task.domain.TaskStatus;
import java.time.Instant;

public record TaskResponse(
        Long id,
        Long userId,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        Instant startDate,
        Instant dueDate,
        Instant completedAt,
        boolean active) {
}
