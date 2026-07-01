package sa.logiceval.catalog.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
// import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
// import org.springframework.ai.vectorstore.Document;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final FallacyDefinitionRepository definitionRepository;
    private final VectorStore vectorStore;

    // Direct injection of Spring AI's auto-configured PgVectorStore abstraction
    DataInitializer(FallacyDefinitionRepository definitionRepository, VectorStore vectorStore) {
        this.definitionRepository = definitionRepository;
        this.vectorStore = vectorStore;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        log.info("Checking relational database catalog for fallacies to index...");

        // 1. Fetch the definitions pre-seeded by Flyway V2
        List<FallacyDefinition> fallacies = definitionRepository.findAll();

        if (fallacies.isEmpty()) {
            log.warn("No fallacies found in relational database. Skipping vector indexing.");
            return;
        }

        log.info("Found {} fallacies. Transforming into Spring AI documents...", fallacies.size());

        // 2. Map Fallacy JPA Entities into Spring AI Document structures
        List<Document> documentsToVectorize = fallacies.stream()
                .map(this::toSpringAiDocument)
                .collect(Collectors.toList());

        // 3. Pushing to PgVectorStore (This automatically invokes Ollama to compute
        // embeddings)
        try {
            log.info("Sending batch to Ollama for embedding generation and pgvector injection...");
            vectorStore.add(documentsToVectorize);
            log.info("Successfully indexed {} logical fallacies into the vector store pipeline!",
                    documentsToVectorize.size());
        } catch (Exception e) {
            log.error("CRITICAL: Failed to push embeddings into vector store. Error: {}", e.getMessage(), e);
        }
    }

    /**
     * Converts a domain database entity into a high-utility semantic text block
     * accompanied by isolated filtering metadata payload properties.
     */
    private Document toSpringAiDocument(FallacyDefinition fallacy) {
        // Construct a clean, highly descriptive text anchor for the embedding model to
        // read
        String semanticContent = String.format(
                "Fallacy Name: %s\nLatin Name: %s\nLogical Flaw Description: %s\nTextbook Example: %s",
                fallacy.getName(),
                fallacy.getLatinName() != null ? fallacy.getLatinName() : "N/A",
                fallacy.getLogicalFlawDescription(),
                fallacy.getTextbookExample());

        // Store explicit categorical dimensions as structured metadata for exact-match
        // runtime filtering expressions
        Map<String, Object> metadata = Map.of(
                "fallacy_id", fallacy.getId(),
                "fallacy_name", fallacy.getName(),
                "category_id", fallacy.getCategory().getId(),
                "category_name", fallacy.getCategory().getName());

        // Spring AI Document holds the semantic payload text and meta identifiers
        // together
        return new Document(semanticContent, metadata);
    }
}