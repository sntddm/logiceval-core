package sa.logiceval.catalog.internal;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import sa.logiceval.catalog.FallacyDTO;
import sa.logiceval.catalog.FallacyService;

import java.util.List;
import java.util.Optional;

@Controller
class FallacyGraphQLController {

    private final FallacyService fallacyService;

    // Spring Boot constructor injection
    FallacyGraphQLController(FallacyService fallacyService) {
        this.fallacyService = fallacyService;
    }

    // Maps directly to the 'allFallacies' query in schema.graphqls
    @QueryMapping
    public List<FallacyDTO> allFallacies() {
        return fallacyService.getAllFallacies();
    }

    // Maps directly to the 'fallacyById' query in schema.graphqls
    @QueryMapping
    public Optional<FallacyDTO> fallacyById(@Argument Long id) {
        return fallacyService.getFallacyById(id);
    }
}