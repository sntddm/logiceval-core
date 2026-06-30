package sa.logiceval.validator.internal;

import java.util.List;

public record AiEvaluationResult(
                Boolean containsFlaws,
                List<AiDetectedFlaw> flaws) {
}