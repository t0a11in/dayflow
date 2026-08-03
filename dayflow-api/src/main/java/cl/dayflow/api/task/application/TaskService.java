package cl.dayflow.api.task.application;

import cl.dayflow.api.task.application.dto.ChangeTaskStatusRequest;
import cl.dayflow.api.task.application.dto.CreateTaskRequest;
import cl.dayflow.api.task.application.dto.TaskResponse;
import cl.dayflow.api.task.application.dto.UpdateTaskRequest;
import cl.dayflow.api.task.domain.TaskPriority;
import cl.dayflow.api.task.domain.TaskStatus;
import cl.dayflow.api.task.infrastructure.persistence.TaskEntity;
import cl.dayflow.api.task.infrastructure.persistence.TaskJpaRepository;
import cl.dayflow.api.user.application.UserNotFoundException;
import cl.dayflow.api.user.infrastructure.persistence.UserEntity;
import cl.dayflow.api.user.infrastructure.persistence.UserJpaRepository;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    private final TaskJpaRepository taskRepository;
    private final UserJpaRepository userRepository;
    private final TaskMapper taskMapper;

    public TaskService(TaskJpaRepository taskRepository, UserJpaRepository userRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskMapper = taskMapper;
    }

    @Transactional
    public TaskResponse create(CreateTaskRequest request) {
        validateDates(request.startDate(), request.dueDate());
        UserEntity user = findActiveUser(request.userId());
        TaskEntity task = TaskEntity.create(
                user,
                request.title(),
                request.description(),
                request.priority() == null ? TaskPriority.MEDIUM : request.priority(),
                request.startDate(),
                request.dueDate());
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public TaskResponse getById(Long id) {
        return taskMapper.toResponse(findTask(id));
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> findAll(
            Long userId, Boolean active, TaskStatus status, TaskPriority priority, String title, Pageable pageable) {
        return taskRepository.findByFilters(userId, active, status, priority, normalizeTitle(title), pageable)
                .map(taskMapper::toResponse);
    }

    @Transactional
    public TaskResponse update(Long id, UpdateTaskRequest request) {
        validateDates(request.startDate(), request.dueDate());
        TaskEntity task = findTask(id);
        try {
            task.update(
                    request.title(),
                    request.description(),
                    request.priority() == null ? task.getPriority() : request.priority(),
                    request.startDate(),
                    request.dueDate());
        } catch (IllegalStateException exception) {
            throw new TaskBusinessException(exception.getMessage());
        }
        return taskMapper.toResponse(task);
    }

    @Transactional
    public TaskResponse changeStatus(Long id, ChangeTaskStatusRequest request) {
        TaskEntity task = findTask(id);
        try {
            task.changeStatus(request.status());
        } catch (IllegalStateException exception) {
            throw new TaskBusinessException(exception.getMessage());
        }
        return taskMapper.toResponse(task);
    }

    @Transactional
    public TaskResponse complete(Long id) {
        TaskEntity task = findTask(id);
        try {
            task.complete();
        } catch (IllegalStateException exception) {
            throw new TaskBusinessException(exception.getMessage());
        }
        return taskMapper.toResponse(task);
    }

    @Transactional
    public void deactivate(Long id) {
        findTask(id).deactivate();
    }

    private TaskEntity findTask(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }

    private UserEntity findActiveUser(Long id) {
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        if (!user.isActive()) {
            throw new TaskBusinessException("Tasks can only be created for active users");
        }
        return user;
    }

    private void validateDates(Instant startDate, Instant dueDate) {
        if (startDate != null && dueDate != null && dueDate.isBefore(startDate)) {
            throw new TaskBusinessException("Due date must not be before start date");
        }
    }

    private String normalizeTitle(String title) {
        return title == null || title.isBlank() ? null : title.trim();
    }
}
