package kg.ogogo.academy.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

@SpringBootApplication
public class WebApplication {

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer problemObjectMapperModules() {
		return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.modules(
				new ProblemModule(),
				new ConstraintViolationProblemModule()
		);
	}

	public static void main(String[] args) {
		SpringApplication.run(WebApplication.class, args);
	}

}
