package sa.logiceval.validator;

import java.time.LocalDateTime;
import java.util.List;

public record EvaluationResultDTO(
        Long analysisId,
        String rawInputText,
        LocalDateTime analyzedAt,
        boolean containsFlaws,
        List<FlawDetail> flaws) {
    public record FlawDetail(
            String fallacyName,
            String flawedSnippet,
            String justification) {
    }
}