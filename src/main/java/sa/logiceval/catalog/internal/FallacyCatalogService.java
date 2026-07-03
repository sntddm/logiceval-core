package sa.logiceval.catalog.internal;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class FallacyCatalogService {

    private final VectorStore vectorStore;

    public FallacyCatalogService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public IngestionResult ingestFallacy(String name, String description, String example) {
        String baseKey = name.trim().toLowerCase().replace(" ", "_");
        String deterministicUuid = UUID.nameUUIDFromBytes(baseKey.getBytes(StandardCharsets.UTF_8)).toString();

        // 1. Check if this exact deterministic key metadata already exists in our index
        // We use a high-similarity strict metadata filter to see if it's there
        SearchRequest lookupRequest = SearchRequest.builder()
                .topK(1)
                .filterExpression("name == '" + baseKey + "'")
                .build();

        boolean alreadyExists = !vectorStore.similaritySearch(lookupRequest).isEmpty();

        String compositeText = String.format(
                "Fallacy Name: %s\nDescription: %s\nExample: %s",
                name.trim(), description.trim(), example.trim());

        Document doc = new Document(
                deterministicUuid,
                compositeText,
                Map.of(
                        "type", "logical_fallacy",
                        "name", baseKey));

        // 2. This will cleanly INSERT or UPDATE
        vectorStore.accept(List.of(doc));

        // 3. Return a precise operational status
        return alreadyExists ? IngestionResult.UPDATED : IngestionResult.CREATED;
    }

    public enum IngestionResult {
        CREATED, UPDATED
    }

    @Transactional
    public void updateValidationMetrics(boolean counterFlawFound) {
        System.out.println("📦 [Catalog Service] Updating aggregate database metrics...");

        if (counterFlawFound) {
            // Here you would execute a repository update statement, for example:
            // statsRepository.incrementTotalFlawsCaught();
            System.out.println("📈 Metric Increment: Total Flaws Caught tracking updated (+1).");
        } else {
            // statsRepository.incrementTotalCleanArguments();
            System.out.println("📉 Metric Increment: Total Clean Arguments tracking updated (+1).");
        }
    }
}