package sa.logiceval.validator.internal;

public record AiDetectedFlaw(
        String identifiedFallacyName,
        String flawedSnippet,
        String aiJustification) {
}
