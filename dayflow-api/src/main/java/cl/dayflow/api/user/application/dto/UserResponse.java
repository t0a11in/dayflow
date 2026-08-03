package cl.dayflow.api.user.application.dto;

import cl.dayflow.api.user.domain.RoleCode;
import java.util.Set;

public record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        boolean active,
        Set<RoleCode> roles) {
}
