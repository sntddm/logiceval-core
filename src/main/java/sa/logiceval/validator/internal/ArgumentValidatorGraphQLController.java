package sa.logiceval.validator.internal;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import sa.logiceval.validator.EvaluationResultDTO;

import java.util.List;
import java.util.Optional;

@Controller
public class ArgumentValidatorGraphQLController {

    private final AnalysisRepository analysisRepository;

    ArgumentValidatorGraphQLController(AnalysisRepository analysisRepository) {
        this.analysisRepository = analysisRepository;
    }

    @QueryMapping
    public List<EvaluationResultDTO> argumentAnalysisHistory() {
        return analysisRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @QueryMapping
    public Optional<EvaluationResultDTO> analysisById(@Argument Long id) {
        return analysisRepository.findById(id).map(this::mapToDTO);
    }

    private EvaluationResultDTO mapToDTO(ArgumentAnalysis entity) {
        List<EvaluationResultDTO.FlawDetail> flaws = entity.getDetectedFlaws().stream()
                .map(f -> new EvaluationResultDTO.FlawDetail(
                        f.getIdentifiedFallacyName(),
                        f.getFlawedSnippet(),
                        f.getAiJustification()))
                .toList();

        return new EvaluationResultDTO(
                entity.getId(),
                entity.getRawInputText(),
                entity.getAnalyzedAt(),
                entity.isContainsFlaws(),
                flaws);
    }

    // Add this inside your ArgumentValidatorGraphQLController.java
    @SchemaMapping(typeName = "ArgumentAnalysisAudit", field = "id")
    public Long getId(EvaluationResultDTO dto) {
        return dto.analysisId();
    }
}