package sa.logiceval.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import sa.logiceval.validator.internal.AnalysisRepository;
import sa.logiceval.validator.internal.ArgumentAnalysis;
import sa.logiceval.validator.internal.ArgumentValidatorGraphQLController;
import sa.logiceval.validator.internal.DetectedFlaw;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@GraphQlTest(ArgumentValidatorGraphQLController.class)
class ArgumentValidatorGraphQLControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private AnalysisRepository analysisRepository;

    @Test
    @WithMockUser(username = "analyst", roles = { "USER" })
    @DisplayName("GraphQL Query: argumentAnalysisHistory - Should fetch and cleanly map historical analyses")
    void shouldFetchAnalysisHistory() {
        // Arrange
        ArgumentAnalysis mockEntity = createMockEntity(101L, "History text payload");
        when(analysisRepository.findAll()).thenReturn(List.of(mockEntity));

        String query = """
                query GetHistory {
                  argumentAnalysisHistory {
                    id
                    rawInputText
                    containsFlaws
                  }
                }
                """;

        // Act & Assert
        graphQlTester.document(query)
                .execute()
                .errors()
                .verify()
                .path("argumentAnalysisHistory[0].id").entity(Long.class).isEqualTo(101L)
                .path("argumentAnalysisHistory[0].rawInputText").entity(String.class).isEqualTo("History text payload");
    }

    @Test
    @WithMockUser(username = "analyst", roles = { "USER" })
    @DisplayName("GraphQL Query: analysisById - Should retrieve a specific record when given a valid ID")
    void shouldFetchAnalysisById() {
        // Arrange
        ArgumentAnalysis mockEntity = createMockEntity(202L, "Target item search text");
        when(analysisRepository.findById(202L)).thenReturn(Optional.of(mockEntity));

        String query = """
                query GetById($id: ID!) {
                  analysisById(id: $id) {
                    id
                    rawInputText
                  }
                }
                """;

        // Act & Assert
        graphQlTester.document(query)
                .variable("id", 202L)
                .execute()
                .errors()
                .verify()
                .path("analysisById.id").entity(Long.class).isEqualTo(202L)
                .path("analysisById.rawInputText").entity(String.class).isEqualTo("Target item search text");
    }

    private ArgumentAnalysis createMockEntity(Long id, String text) {
        ArgumentAnalysis entity = new ArgumentAnalysis();
        entity.setId(id);
        entity.setRawInputText(text);
        entity.setAnalyzedAt(LocalDateTime.now());
        entity.setContainsFlaws(true);

        DetectedFlaw flaw = new DetectedFlaw();
        flaw.setIdentifiedFallacyName("Slippery Slope");
        flaw.setFlawedSnippet("If A happens, Z will follow.");
        flaw.setAiJustification("Classic chain reaction assumption flaw.");

        List<DetectedFlaw> flaws = new ArrayList<>();
        flaws.add(flaw);
        entity.setDetectedFlaws(flaws);

        return entity;
    }
}