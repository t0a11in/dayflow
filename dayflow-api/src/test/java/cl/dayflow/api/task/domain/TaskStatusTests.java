package cl.dayflow.api.task.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TaskStatusTests {

    @Test
    void shouldAllowExpectedTransitionsFromPending() {
        assertThat(TaskStatus.PENDING.canTransitionTo(TaskStatus.IN_PROGRESS)).isTrue();
        assertThat(TaskStatus.PENDING.canTransitionTo(TaskStatus.COMPLETED)).isTrue();
        assertThat(TaskStatus.PENDING.canTransitionTo(TaskStatus.CANCELLED)).isTrue();
    }

    @Test
    void shouldRejectAnyTransitionFromCompleted() {
        assertThat(TaskStatus.COMPLETED.canTransitionTo(TaskStatus.PENDING)).isFalse();
        assertThat(TaskStatus.COMPLETED.canTransitionTo(TaskStatus.IN_PROGRESS)).isFalse();
    }
}
