package cl.dayflow.api.user.application.dto;

import jakarta.validation.constraints.NotNull;

public record ChangeUserStatusRequest(@NotNull Boolean active) {
}
