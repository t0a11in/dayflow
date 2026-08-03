package cl.dayflow.api.shared.exception;

import cl.dayflow.api.shared.config.TraceIdFilter;
import cl.dayflow.api.user.application.DuplicateUserEmailException;
import cl.dayflow.api.user.application.InvalidRoleAssignmentException;
import cl.dayflow.api.user.application.UserNotFoundException;
import cl.dayflow.api.task.application.TaskBusinessException;
import cl.dayflow.api.task.application.TaskNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final RequestErrorLogger requestErrorLogger;

    public GlobalExceptionHandler(RequestErrorLogger requestErrorLogger) {
        this.requestErrorLogger = requestErrorLogger;
    }

    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity<ApiErrorResponse> handleUserNotFound(UserNotFoundException exception, HttpServletRequest request) {
        requestErrorLogger.logExpected("USER_NOT_FOUND", exception, request, HttpStatus.NOT_FOUND.value());
        return error(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(DuplicateUserEmailException.class)
    ResponseEntity<ApiErrorResponse> handleDuplicateEmail(DuplicateUserEmailException exception, HttpServletRequest request) {
        requestErrorLogger.logExpected("USER_EMAIL_ALREADY_EXISTS", exception, request, HttpStatus.CONFLICT.value());
        return error(HttpStatus.CONFLICT, "USER_EMAIL_ALREADY_EXISTS", exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(InvalidRoleAssignmentException.class)
    ResponseEntity<ApiErrorResponse> handleInvalidRole(InvalidRoleAssignmentException exception, HttpServletRequest request) {
        requestErrorLogger.logExpected("INVALID_ROLE_ASSIGNMENT", exception, request, HttpStatus.BAD_REQUEST.value());
        return error(HttpStatus.BAD_REQUEST, "INVALID_ROLE_ASSIGNMENT", exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(TaskNotFoundException.class)
    ResponseEntity<ApiErrorResponse> handleTaskNotFound(TaskNotFoundException exception, HttpServletRequest request) {
        requestErrorLogger.logExpected("TASK_NOT_FOUND", exception, request, HttpStatus.NOT_FOUND.value());
        return error(HttpStatus.NOT_FOUND, "TASK_NOT_FOUND", exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(TaskBusinessException.class)
    ResponseEntity<ApiErrorResponse> handleTaskBusinessError(TaskBusinessException exception, HttpServletRequest request) {
        requestErrorLogger.logExpected("TASK_BUSINESS_RULE_VIOLATION", exception, request, HttpStatus.BAD_REQUEST.value());
        return error(HttpStatus.BAD_REQUEST, "TASK_BUSINESS_RULE_VIOLATION", exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        List<FieldErrorResponse> fieldErrors = exception.getBindingResult().getFieldErrors().stream()
                .map(this::toFieldError)
                .toList();
        requestErrorLogger.logExpected("VALIDATION_ERROR", exception, request, HttpStatus.BAD_REQUEST.value());
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "The request data is invalid", request, fieldErrors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiErrorResponse> handleUnreadableRequest(HttpMessageNotReadableException exception, HttpServletRequest request) {
        requestErrorLogger.logExpected("MALFORMED_REQUEST", exception, request, HttpStatus.BAD_REQUEST.value());
        return error(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST", "The request body is malformed or contains invalid values", request, List.of());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiErrorResponse> handleUnexpected(Exception exception, HttpServletRequest request) {
        requestErrorLogger.logUnexpected(exception, request);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected error occurred", request, List.of());
    }

    private FieldErrorResponse toFieldError(FieldError error) {
        return new FieldErrorResponse(error.getField(), error.getDefaultMessage());
    }

    private ResponseEntity<ApiErrorResponse> error(
            HttpStatus status, String code, String message, HttpServletRequest request, List<FieldErrorResponse> fieldErrors) {
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                code,
                message,
                request.getRequestURI(),
                (String) request.getAttribute(TraceIdFilter.TRACE_ID_ATTRIBUTE),
                fieldErrors);
        return ResponseEntity.status(status).body(response);
    }
}
