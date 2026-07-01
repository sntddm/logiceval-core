package sa.logiceval.validator.internal;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LogicEval Validation Engine API")
                        .version("1.0/2.0")
                        .description("Automated RAG-Driven Logical Fallacy Detection and Schema Auditing Platform.")
                        .license(new License().name("Apache 2.0").url("https://spring.io")));
    }

    @Bean
    public GroupedOpenApi publicApiV1() {
        return GroupedOpenApi.builder()
                .group("Validation-API-v1.0")
                .pathsToMatch("/api/validate")
                // Customizes the scan selector to target our 1.0 endpoint header states
                .build();
    }
}