package cl.dayflow.api.task.application;

import cl.dayflow.api.task.application.dto.TaskResponse;
import cl.dayflow.api.task.infrastructure.persistence.TaskEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {

    @Mapping(target = "userId", source = "user.id")
    TaskResponse toResponse(TaskEntity task);
}
