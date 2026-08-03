package cl.dayflow.api.task.infrastructure.rest;

import cl.dayflow.api.task.application.TaskService;
import cl.dayflow.api.task.application.dto.ChangeTaskStatusRequest;
import cl.dayflow.api.task.application.dto.CreateTaskRequest;
import cl.dayflow.api.task.application.dto.TaskResponse;
import cl.dayflow.api.task.application.dto.UpdateTaskRequest;
import cl.dayflow.api.task.domain.TaskPriority;
import cl.dayflow.api.task.domain.TaskStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Tasks", description = "User task management")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @Operation(summary = "Create a task for a user")
    @ApiResponse(responseCode = "201", description = "Task created")
    ResponseEntity<TaskResponse> create(@Valid @RequestBody CreateTaskRequest request) {
        TaskResponse task = taskService.create(request);
        return ResponseEntity.created(URI.create("/api/v1/tasks/" + task.id())).body(task);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a task by id")
    ResponseEntity<TaskResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getById(id));
    }

    @GetMapping
    @Operation(summary = "List tasks for a user")
    ResponseEntity<Page<TaskResponse>> findAll(
            @RequestParam Long userId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) String title,
            @PageableDefault(size = 20, sort = "dueDate") Pageable pageable) {
        return ResponseEntity.ok(taskService.findAll(userId, active, status, priority, title, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a task")
    ResponseEntity<TaskResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateTaskRequest request) {
        return ResponseEntity.ok(taskService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Change task status")
    ResponseEntity<TaskResponse> changeStatus(
            @PathVariable Long id, @Valid @RequestBody ChangeTaskStatusRequest request) {
        return ResponseEntity.ok(taskService.changeStatus(id, request));
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Mark task as completed")
    ResponseEntity<TaskResponse> complete(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.complete(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate a task")
    ResponseEntity<Void> deactivate(@PathVariable Long id) {
        taskService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
