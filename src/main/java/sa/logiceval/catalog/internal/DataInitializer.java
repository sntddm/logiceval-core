package sa.logiceval.catalog.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
// import org.springframework.ai.vectorstore.Document;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate; // <-- New Import
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
    private final JdbcTemplate jdbcTemplate; // <-- Inject JdbcTemplate

    DataInitializer(FallacyDefinitionRepository definitionRepository, VectorStore vectorStore,
            JdbcTemplate jdbcTemplate) {
        this.definitionRepository = definitionRepository;
        this.vectorStore = vectorStore;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        log.info("Checking vector store indexing state...");

        // GUARD CONDITION: Check if public.fallacy_vector_store already holds records
        Integer existingVectorCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM public.fallacy_vector_store", Integer.class);

        if (existingVectorCount != null && existingVectorCount > 0) {
            log.info("Vector store already contains {} indexed records. Skipping embedding generation step.",
                    existingVectorCount);
            return; // Clean fast exit!
        }

        log.info("Vector store is empty. Fetching relational catalog for seeding pipeline...");
        List<FallacyDefinition> fallacies = definitionRepository.findAll();

        if (fallacies.isEmpty()) {
            log.warn("No fallacies found in relational database. Skipping vector indexing.");
            return;
        }

        log.info("Transforming {} fallacies into vectors...", fallacies.size());
        List<Document> documentsToVectorize = fallacies.stream()
                .map(this::toSpringAiDocument)
                .collect(Collectors.toList());

        try {
            log.info("Invoking nomic-embed-text via Ollama for initial indexing batch...");
            vectorStore.add(documentsToVectorize);
            log.info("Successfully indexed vector targets!");
        } catch (Exception e) {
            log.error("Failed to seed vector store: {}", e.getMessage(), e);
        }
    }

    private Document toSpringAiDocument(FallacyDefinition fallacy) {
        String semanticContent = String.format(
                "Fallacy Name: %s\nLatin Name: %s\nLogical Flaw Description: %s\nTextbook Example: %s",
                fallacy.getName(),
                fallacy.getLatinName() != null ? fallacy.getLatinName() : "N/A",
                fallacy.getLogicalFlawDescription(),
                fallacy.getTextbookExample());

        Map<String, Object> metadata = Map.of(
                "fallacy_id", fallacy.getId(),
                "fallacy_name", fallacy.getName(),
                "category_name", fallacy.getCategory().getName());

        return new Document(semanticContent, metadata);
    }
}