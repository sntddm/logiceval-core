package sa.logiceval.validator.internal;

import java.util.List;

public record AiEvaluationResult(
        boolean containsFlaws,
        List<AiDetectedFlaw> flaws) {
}