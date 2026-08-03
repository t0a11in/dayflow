package cl.dayflow.api.user.application;

public class InvalidRoleAssignmentException extends RuntimeException {

    public InvalidRoleAssignmentException() {
        super("One or more requested roles do not exist");
    }
}
