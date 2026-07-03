package sa.logiceval.validator;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import sa.logiceval.validator.internal.AiEvaluationResult;
import sa.logiceval.validator.internal.FallacyAiClient;
import sa.logiceval.validator.internal.FallacyRagRetriever;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationModuleTest
class ValidatorModuleIntegrationTest {

    @Autowired
    ArgumentValidatorService validatorService;

    // Mock the internal infrastructure components so they don't break the execution
    // pipeline
    @MockitoBean
    FallacyRagRetriever ragRetriever;

    @MockitoBean
    FallacyAiClient aiClient;

    @Test
    void shouldPublishEventWhenArgumentIsValidated(Scenario scenario) {
        String testInput = "You are wrong because you work for the government!";

        // 1. Stub the retriever to return a basic mock context string
        Mockito.when(ragRetriever.retrieveRelevantContext(testInput))
                .thenReturn("Seeded fallacy context data");

        // 2. Stub the AI client to return a valid result object instead of null
        AiEvaluationResult mockAiResult = new AiEvaluationResult(
                true, // containsFlaws
                List.of(new AiEvaluationResult.AiDetectedFlaw("Ad Hominem", "work for the government",
                        "Attacking source instead of logic")));
        Mockito.when(aiClient.analyzeArgument(testInput, "Seeded fallacy context data"))
                .thenReturn(mockAiResult);

        // 3. Run the evaluation scenario
        scenario.stimulate(() -> validatorService.validateArgument(testInput))
                .andWaitForEventOfType(ArgumentValidatedEvent.class)
                .matching(event -> event.containsFlaws() == true)
                .toArriveAndVerify(event -> {
                    assertThat(event.analysisId()).isNotNull();
                    assertThat(event.timestamp()).isBeforeOrEqualTo(LocalDateTime.now());
                });
    }
}