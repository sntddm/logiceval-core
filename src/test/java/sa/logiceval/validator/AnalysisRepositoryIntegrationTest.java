package sa.logiceval.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import sa.logiceval.validator.internal.AnalysisRepository;
import sa.logiceval.validator.internal.ArgumentAnalysis;
import sa.logiceval.validator.internal.DetectedFlaw;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers // 👈 Activates automatic container lifecycle management
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 👈 Prevents H2 replacement
class AnalysisRepositoryIntegrationTest {

    // 👈 Spins up your exact DB image. If using vector embeddings, use a
    // pgvector-enabled image!
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("pgvector/pgvector:0.8.2-pg18-trixie")
            .withDatabaseName("logiceval_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private AnalysisRepository analysisRepository;

    @Test
    @DisplayName("Should successfully persist ArgumentAnalysis cascading down to Unidirectional DetectedFlaws using Testcontainers")
    void shouldPersistAnalysisAndFlawsCascadingly() {
        // Arrange
        ArgumentAnalysis analysis = new ArgumentAnalysis();
        analysis.setRawInputText("Testcontainers isolation verification.");
        analysis.setContainsFlaws(true);

        DetectedFlaw flaw = new DetectedFlaw();
        flaw.setIdentifiedFallacyName("Straw Man");
        flaw.setFlawedSnippet("Misrepresented statement");
        flaw.setAiJustification("Distorted premise structure.");

        analysis.getDetectedFlaws().add(flaw);

        // Act
        ArgumentAnalysis savedEntity = analysisRepository.saveAndFlush(analysis);

        // Assert
        assertThat(savedEntity.getId()).isNotNull();
        ArgumentAnalysis retrievedEntity = analysisRepository.findById(savedEntity.getId()).orElseThrow();

        assertThat(retrievedEntity.isContainsFlaws()).isTrue();
        assertThat(retrievedEntity.getDetectedFlaws()).hasSize(1);
        assertThat(retrievedEntity.getDetectedFlaws().get(0).getIdentifiedFallacyName()).isEqualTo("Straw Man");
    }
}