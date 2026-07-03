package sa.logiceval.validator;

import sa.logiceval.validator.internal.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArgumentValidatorServiceTest {

    @Mock
    private FallacyRagRetriever ragRetriever;

    @Mock
    private FallacyAiClient aiClient;

    @Mock
    private AnalysisRepository analysisRepository;

    private ArgumentValidatorService validatorService;

    @BeforeEach
    void setUp() {
        // Using the package-private constructor optimized for Spring Modulith
        // validatorService = new ArgumentValidatorService(ragRetriever, aiClient,
        // analysisRepository);

    }

    @Test
    @DisplayName("Should successfully analyze and save an argument when the AI identifies valid flaws")
    void shouldProcessFlawedArgumentSuccessfully() {
        // Arrange
        String input = "You're wrong because you're lazy.";
        String mockContext = "Ad Hominem definition context...";

        AiEvaluationResult mockAiResult = new AiEvaluationResult(
                true,
                List.of(new AiEvaluationResult.AiDetectedFlaw("Ad Hominem", "you're lazy",
                        "Attacking person instead of point")));

        when(ragRetriever.retrieveRelevantContext(input)).thenReturn(mockContext);
        when(aiClient.analyzeArgument(input, mockContext)).thenReturn(mockAiResult);

        // Mock save to mirror the object state and append a generated ID
        when(analysisRepository.save(any(ArgumentAnalysis.class))).thenAnswer(invocation -> {
            ArgumentAnalysis analysis = invocation.getArgument(0);
            analysis.setId(100L);
            return analysis;
        });

        // Act
        EvaluationResultDTO result = validatorService.validateArgument(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.analysisId()).isEqualTo(100L);
        assertThat(result.containsFlaws()).isTrue();
        assertThat(result.flaws()).hasSize(1);
        assertThat(result.flaws().get(0).fallacyName()).isEqualTo("Ad Hominem");

        verify(analysisRepository, times(1)).save(any(ArgumentAnalysis.class));
    }

    @Test
    @DisplayName("Should override containsFlaws to FALSE if the AI list has rows but they are entirely null/corrupt")
    void shouldForceContainsFlawsToFalseWhenAllFlawsAreCorrupt() {
        // Arrange
        String input = "Text that causes JSON key mismatch issues";
        String mockContext = "Context data...";

        // Scenario: AI claims it found a flaw, but everything gets filtered out due to
        // null names
        AiEvaluationResult corruptAiResult = new AiEvaluationResult(
                true,
                List.of(new AiEvaluationResult.AiDetectedFlaw(null, null, null)));

        when(ragRetriever.retrieveRelevantContext(input)).thenReturn(mockContext);
        when(aiClient.analyzeArgument(input, mockContext)).thenReturn(corruptAiResult);
        when(analysisRepository.save(any(ArgumentAnalysis.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        EvaluationResultDTO result = validatorService.validateArgument(input);

        // Assert
        // The service logic must catch this and clean the state!
        assertThat(result.containsFlaws()).isFalse();
        assertThat(result.flaws()).isEmpty();

        verify(analysisRepository)
                .save(argThat(analysis -> !analysis.isContainsFlaws() && analysis.getDetectedFlaws().isEmpty()));
    }

    @Test
    @DisplayName("Should override containsFlaws to FALSE if the AI claims flaws exist but returns an empty list")
    void shouldForceContainsFlawsToFalseWhenFlawsListIsEmpty() {
        // Arrange
        String input = "Text triggering AI mismatch logic";
        String mockContext = "Context data...";

        // Scenario: AI claims 'true' but hands back a completely empty collection array
        AiEvaluationResult contradictionAiResult = new AiEvaluationResult(true, Collections.emptyList());

        when(ragRetriever.retrieveRelevantContext(input)).thenReturn(mockContext);
        when(aiClient.analyzeArgument(input, mockContext)).thenReturn(contradictionAiResult);
        when(analysisRepository.save(any(ArgumentAnalysis.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        EvaluationResultDTO result = validatorService.validateArgument(input);

        // Assert
        assertThat(result.containsFlaws()).isFalse();
        assertThat(result.flaws()).isEmpty();

        verify(analysisRepository).save(argThat(analysis -> !analysis.isContainsFlaws()));
    }

    @Test
    @DisplayName("Should handle clean arguments without flaws gracefully")
    void shouldHandleFlawlessArgument() {
        // Arrange
        String input = "All humans are mortal. Socrates is human. Therefore, Socrates is mortal.";
        String mockContext = "Syllogism rules...";

        AiEvaluationResult cleanAiResult = new AiEvaluationResult(false, Collections.emptyList());

        when(ragRetriever.retrieveRelevantContext(input)).thenReturn(mockContext);
        when(aiClient.analyzeArgument(input, mockContext)).thenReturn(cleanAiResult);
        when(analysisRepository.save(any(ArgumentAnalysis.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        EvaluationResultDTO result = validatorService.validateArgument(input);

        // Assert
        assertThat(result.containsFlaws()).isFalse();
        assertThat(result.flaws()).isEmpty();
    }

}