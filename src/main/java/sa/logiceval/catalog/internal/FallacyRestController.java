package sa.logiceval.catalog.internal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sa.logiceval.catalog.FallacyDTO;
import sa.logiceval.catalog.FallacyService;

import java.util.List;

@RestController
@RequestMapping("/api/fallacies")
public class FallacyRestController {

    private final FallacyService fallacyService;

    // Spring Boot 4 constructor injection
    FallacyRestController(FallacyService fallacyService) {
        this.fallacyService = fallacyService;
    }

    /**
     * API Version 1.0 - Standard list of fallacies
     */
    @GetMapping(version = "1.0")
    public ResponseEntity<List<FallacyDTO>> getAllFallaciesV1() {
        return ResponseEntity.ok(fallacyService.getAllFallacies());
    }

    /**
     * API Version 2.0 - Evolved endpoint
     * Imagine v2 adds metadata extensions or a modified payload format.
     * We use baseline formatting ("2.0+") if we want it to support 2.0 and above.
     */
    @GetMapping(version = "2.0")
    public ResponseEntity<List<FallacyDTO>> getAllFallaciesV2() {
        // For demonstration, we use the same service, but the routing is handled
        // natively
        return ResponseEntity.ok(fallacyService.getAllFallacies());
    }

    @GetMapping(value = "/{id}", version = "1.0+")
    public ResponseEntity<FallacyDTO> getFallacyById(@PathVariable Long id) {
        return fallacyService.getFallacyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}