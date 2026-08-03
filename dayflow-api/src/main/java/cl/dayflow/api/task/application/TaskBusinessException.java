package cl.dayflow.api.task.application;

public class TaskBusinessException extends RuntimeException {

    public TaskBusinessException(String message) {
        super(message);
    }
}
