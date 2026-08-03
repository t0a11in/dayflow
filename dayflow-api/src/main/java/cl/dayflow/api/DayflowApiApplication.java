package cl.dayflow.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import cl.dayflow.api.user.application.InitialAdminProperties;

@SpringBootApplication
@EnableConfigurationProperties(InitialAdminProperties.class)
public class DayflowApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(DayflowApiApplication.class, args);
	}

}
