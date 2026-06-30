package sa.logiceval.validator.internal;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FallacyRagRetriever {

    private final VectorStore vectorStore;

    // Spring Boot auto-wires our configured SimpleVectorStore bean here
    FallacyRagRetriever(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * Extracts semantically similar logical fallacies from the vector space
     * to assemble the contextual background block for our prompt template.
     */
    public String retrieveRelevantContext(String rawInputText) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(rawInputText)
                .topK(3) // Extract only the top 3 closest structural examples
                .similarityThreshold(0.6) // Filter out low-confidence, irrelevant noise
                .build();

        List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);

        if (similarDocuments.isEmpty()) {
            return "No directly related classical fallacy matches found in the local database.";
        }

        // Format retrieved documents into a structured string block for prompt
        // injection
        return similarDocuments.stream()
                .map(doc -> String.format(
                        "- Fallacy: %s\n  Core Flaw: %s\n  Reference Example: %s",
                        doc.getMetadata().get("fallacyName"),
                        doc.getText(),
                        doc.getMetadata().get("categoryName")))
                .collect(Collectors.joining("\n\n"));
    }
}