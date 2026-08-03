package cl.dayflow.api.user.application;

public class DuplicateUserEmailException extends RuntimeException {

    public DuplicateUserEmailException(String email) {
        super("A user with email %s already exists".formatted(email));
    }
}
