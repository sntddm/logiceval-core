package sa.logiceval.validator;

import java.time.LocalDateTime;

public record ArgumentValidatedEvent(
                Long analysisId,
                boolean containsFlaws,
                LocalDateTime timestamp) {
}