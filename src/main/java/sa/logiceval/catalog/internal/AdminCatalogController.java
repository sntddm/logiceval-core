package sa.logiceval.catalog.internal;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/fallacies")
// 1. Enforce that only users with ROLE_ADMIN can interact with any endpoint in
// this class
// @PreAuthorize("hasRole('ADMIN')")
@PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
class AdminCatalogController {

    private final VectorStore vectorStore;

    // Spring AI automatically manages and configures the VectorStore bean connected
    // to PostgreSQL (PGVector)
    AdminCatalogController(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> addFallacyKnowledge(@RequestBody FallacyIngestRequest request) {

        // 1. Create a standardized, URL-safe uniform deterministic key string
        String deterministicId = request.name().trim().toLowerCase()
                .replaceAll("[^a-z0-9]", "_") // Replace symbols/spaces with underscores
                .replaceAll("_+", "_"); // Deduplicate sequential underscores

        // 2. Format a comprehensive text context string for the RAG pipeline to search
        // against later
        String compositeText = String.format(
                "Fallacy Name: %s\nDescription: %s\nExample: %s",
                request.name(), request.description(), request.example());

        // 3. Create a Spring AI Document wrapper, attaching structural metadata tags
        // Document doc = new Document(compositeText, Map.of(
        // "type", "logical_fallacy",
        // "name", request.name().toLowerCase().replace(" ", "_")));
        Document doc = new Document(
                deterministicId, // <-- The Magic Hook: Prevents duplicate rows!
                compositeText,
                Map.of("type", "logical_fallacy"));

        // 4. Pass the document to the vector store.
        // This implicitly triggers your 'nomic-embed-text' model to generate vectors
        // and runs an INSERT statement.
        vectorStore.accept(List.of(doc));

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Fallacy knowledge successfully vectorized and stored in PGVector database."));
    }

    record FallacyIngestRequest(String name, String description, String example) {
    }
}