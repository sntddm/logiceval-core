package sa.logiceval.validator;

import sa.logiceval.validator.internal.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArgumentValidatorService {

    private final FallacyRagRetriever ragRetriever;
    // private final FallacyAiClient_openai aiClient;
    private final FallacyAiClient_ollama aiClient;
    private final AnalysisRepository analysisRepository;

    // Package-private constructor for Modulith optimization
    ArgumentValidatorService(FallacyRagRetriever ragRetriever,
            FallacyAiClient_ollama aiClient,
            AnalysisRepository analysisRepository) {
        this.ragRetriever = ragRetriever;
        this.aiClient = aiClient;
        this.analysisRepository = analysisRepository;
    }

    /**
     * Executes the complete RAG-to-AI analysis pipeline and persists the
     * transaction.
     */
    @Transactional
    public EvaluationResultDTO validateArgument(String rawInputText) {
        // 1. Semantic Retrieval: Match text against our 22 seeded catalog entries
        String matchingContext = ragRetriever.retrieveRelevantContext(rawInputText);

        // 2. AI Execution: Request structured data conforming to our target schema
        AiEvaluationResult aiResult = aiClient.analyzeArgument(rawInputText, matchingContext);

        // 3. Persistence: Map the immutable AI record into our internal auditable
        // entity structure
        ArgumentAnalysis analysis = new ArgumentAnalysis();
        analysis.setRawInputText(rawInputText);
        // analysis.setContainsFlaws(aiResult.containsFlaws());
        analysis.setContainsFlaws(Boolean.TRUE.equals(aiResult.containsFlaws()));

        if (aiResult.flaws() != null) {
            List<DetectedFlaw> internalFlaws = aiResult.flaws().stream()
                    .map(aiFlaw -> {
                        DetectedFlaw flaw = new DetectedFlaw();
                        flaw.setIdentifiedFallacyName(aiFlaw.identifiedFallacyName());
                        flaw.setFlawedSnippet(aiFlaw.flawedSnippet());
                        flaw.setAiJustification(aiFlaw.aiJustification());
                        return flaw;
                    }).toList();
            analysis.getDetectedFlaws().addAll(internalFlaws);
        }

        ArgumentAnalysis savedAnalysis = analysisRepository.save(analysis);

        // 4. Return DTO: Map database entity state to clean public output payload
        return mapToDTO(savedAnalysis);
    }

    private EvaluationResultDTO mapToDTO(ArgumentAnalysis entity) {
        List<EvaluationResultDTO.FlawDetail> publicFlaws = entity.getDetectedFlaws().stream()
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
                publicFlaws);
    }
}