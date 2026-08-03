package cl.dayflow.api.user.application;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "dayflow.bootstrap.admin")
public record InitialAdminProperties(
        String email,
        String password,
        String firstName,
        String lastName) {
}
