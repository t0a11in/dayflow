package cl.dayflow.api.user.application;

import cl.dayflow.api.user.application.dto.UserResponse;
import cl.dayflow.api.user.domain.RoleCode;
import cl.dayflow.api.user.infrastructure.persistence.RoleEntity;
import cl.dayflow.api.user.infrastructure.persistence.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserResponse toResponse(UserEntity user);

    default RoleCode map(RoleEntity role) {
        return role.getCode();
    }
}
