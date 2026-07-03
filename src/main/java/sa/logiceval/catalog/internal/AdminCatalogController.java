package sa.logiceval.catalog.internal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/fallacies")
@PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
class AdminCatalogController {

        private final FallacyCatalogService catalogService;

        AdminCatalogController(FallacyCatalogService catalogService) {
                this.catalogService = catalogService;
        }

        @PostMapping
        public ResponseEntity<Map<String, String>> addFallacyKnowledge(@RequestBody FallacyIngestRequest request) {
                FallacyCatalogService.IngestionResult result = catalogService.ingestFallacy(request.name(),
                                request.description(), request.example());

                if (result == FallacyCatalogService.IngestionResult.CREATED) {
                        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                                        "status", "CREATED",
                                        "message",
                                        "A new logical fallacy definition was successfully vectorized and stored."));
                } else {
                        return ResponseEntity.ok(Map.of(
                                        "status", "UPDATED",
                                        "message",
                                        "An existing fallacy definition was matched. Missing properties overwritten cleanly via UPSERT."));
                }
        }

        record FallacyIngestRequest(String name, String description, String example) {
        }
}