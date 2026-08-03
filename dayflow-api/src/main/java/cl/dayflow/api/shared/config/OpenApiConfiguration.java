package cl.dayflow.api.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    OpenAPI dayflowOpenApi() {
        return new OpenAPI().info(new Info()
                .title("DayFlow API")
                .version("v1")
                .description("API for personal organization and task management."));
    }
}
