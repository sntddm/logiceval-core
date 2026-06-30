package sa.logiceval.validator;

public record ValidationRequestV2(
        String argumentText,
        boolean strictMode, // If true, could enforce lower similarity thresholds or higher penalties
        String contextOrigin // Tracks whether the text came from a political debate, social media, etc.
) {
}