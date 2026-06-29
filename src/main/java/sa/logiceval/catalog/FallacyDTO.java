package sa.logiceval.catalog;

public record FallacyDTO(
        Long id,
        String name,
        String latinName,
        String description,
        String categoryName,
        String example) {
}