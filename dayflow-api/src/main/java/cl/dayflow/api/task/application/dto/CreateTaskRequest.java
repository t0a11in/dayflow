package cl.dayflow.api.task.application.dto;

import cl.dayflow.api.task.domain.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record CreateTaskRequest(
        @NotNull Long userId,
        @NotBlank @Size(max = 200) String title,
        @Size(max = 4000) String description,
        TaskPriority priority,
        Instant startDate,
        Instant dueDate) {
}
