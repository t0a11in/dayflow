package cl.dayflow.api.task.application.dto;

import cl.dayflow.api.task.domain.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeTaskStatusRequest(@NotNull TaskStatus status) {
}
