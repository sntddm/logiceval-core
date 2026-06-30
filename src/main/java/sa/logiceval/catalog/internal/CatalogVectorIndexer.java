package sa.logiceval.catalog.internal;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
class CatalogVectorIndexer {

    private final VectorStore vectorStore;

    CatalogVectorIndexer(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void indexFallacies(List<FallacyDefinition> fallacies) {
        List<Document> documents = fallacies.stream()
                .map(fallacy -> {
                    // Combine the flaw description and textbook example for rich semantic lookup
                    String searchableText = String.format(
                            "Fallacy Name: %s. Description: %s. Example: %s",
                            fallacy.getName(),
                            fallacy.getLogicalFlawDescription(),
                            fallacy.getTextbookExample());

                    // Embed relational database keys into metadata for clean trace tracking
                    Map<String, Object> metadata = Map.of(
                            "fallacyId", fallacy.getId(),
                            "fallacyName", fallacy.getName(),
                            "categoryName", fallacy.getCategory().getName());

                    System.out.println("Embed: " + metadata);

                    return new Document(searchableText, metadata);
                })
                .toList();

        // Pass documents to the embedding model and store them in memory
        vectorStore.add(documents);
    }
}
