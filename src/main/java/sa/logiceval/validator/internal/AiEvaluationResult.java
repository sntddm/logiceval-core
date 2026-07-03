package sa.logiceval.validator.internal;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiEvaluationResult(
                Boolean containsFlaws,
                List<AiDetectedFlaw> flaws) {
        public record AiDetectedFlaw(
                        @JsonProperty("identified_fallacy_name") String identifiedFallacyName,
                        @JsonProperty("flawed_snippet") String flawedSnippet,
                        @JsonProperty("ai_justification") String aiJustification) {
        }

}