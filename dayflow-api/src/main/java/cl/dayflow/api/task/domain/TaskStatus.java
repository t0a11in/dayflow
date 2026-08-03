package cl.dayflow.api.task.domain;

import java.util.Set;

public enum TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

    public boolean canTransitionTo(TaskStatus target) {
        return switch (this) {
            case PENDING -> Set.of(IN_PROGRESS, COMPLETED, CANCELLED).contains(target);
            case IN_PROGRESS -> Set.of(PENDING, COMPLETED, CANCELLED).contains(target);
            case COMPLETED -> false;
            case CANCELLED -> target == PENDING;
        };
    }
}
