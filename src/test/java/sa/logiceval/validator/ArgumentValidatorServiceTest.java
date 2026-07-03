package sa.logiceval.validator;

import sa.logiceval.validator.internal.*;
import sa.logiceval.validator.internal.AiEvaluationResult.AiDetectedFlaw;

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
        validatorService = new ArgumentValidatorService(ragRetriever, aiClient, analysisRepository);
    }

    @Test
    @DisplayName("Should successfully analyze and persist an argument containing logical flaws")
    void shouldProcessFlawedArgumentSuccessfully() {
        // Arrange
        String input = "You're wrong because you're lazy.";
        String mockContext = "Ad Hominem definition context...";

        AiEvaluationResult mockAiResult = new AiEvaluationResult(
                true,
                List.of(new AiDetectedFlaw("Ad Hominem", "you're lazy", "Attacking person instead of point")));

        when(ragRetriever.retrieveRelevantContext(input)).thenReturn(mockContext);
        when(aiClient.analyzeArgument(input, mockContext)).thenReturn(mockAiResult);

        when(analysisRepository.save(any(ArgumentAnalysis.class))).thenAnswer(invocation -> {
            ArgumentAnalysis analysis = invocation.getArgument(0);
            analysis.setId(1L);
            return analysis;
        });

        // Act
        EvaluationResultDTO result = validatorService.validateArgument(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.analysisId()).isEqualTo(1L); // 👈 Fixed from id()
        assertThat(result.containsFlaws()).isTrue();
        assertThat(result.flaws()).hasSize(1);
        assertThat(result.flaws().get(0).fallacyName()).isEqualTo("Ad Hominem"); // 👈 Fixed from
                                                                                 // identifiedFallacyName()

        verify(analysisRepository, times(1)).save(any(ArgumentAnalysis.class));
    }

    @Test
    @DisplayName("Should filter out null or corrupt AI flaws and prevent database NOT NULL crashes")
    void shouldFilterCorruptAiFlawsDefensively() {
        // Arrange
        String input = "Faulty payload text";
        String mockContext = "Context...";

        AiEvaluationResult corruptAiResult = new AiEvaluationResult(
                true,
                List.of(
                        new AiDetectedFlaw(null, null, null),
                        new AiDetectedFlaw("Straw Man", "Misrepresented view", "Distorting the statement")));

        when(ragRetriever.retrieveRelevantContext(input)).thenReturn(mockContext);
        when(aiClient.analyzeArgument(input, mockContext)).thenReturn(corruptAiResult);
        when(analysisRepository.save(any(ArgumentAnalysis.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        EvaluationResultDTO result = validatorService.validateArgument(input);

        // Assert
        assertThat(result.flaws()).hasSize(1);
        assertThat(result.flaws().get(0).fallacyName()).isEqualTo("Straw Man"); // 👈 Fixed from identifiedFallacyName()

        verify(analysisRepository).save(argThat(analysis -> analysis.getDetectedFlaws().size() == 1 &&
                analysis.getDetectedFlaws().get(0).getIdentifiedFallacyName().equals("Straw Man")));
    }

    @Test
    @DisplayName("Should handle clean arguments without flaws gracefully")
    void shouldHandleFlawlessArgument() {
        // Arrange
        String input = "All humans are mortal. Socrates is human. Therefore, Socrates is mortal.";
        String mockContext = "Syllogism rule context...";

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