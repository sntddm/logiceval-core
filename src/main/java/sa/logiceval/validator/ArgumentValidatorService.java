package sa.logiceval.validator;

import sa.logiceval.validator.internal.*;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArgumentValidatorService {

    private final FallacyRagRetriever ragRetriever;
    private final FallacyAiClient aiClient;
    private final AnalysisRepository analysisRepository;
    private final ApplicationEventPublisher eventPublisher; // 1. Added publisher field

    // Package-private constructor optimized for Modulith dependency injection
    ArgumentValidatorService(FallacyRagRetriever ragRetriever,
            FallacyAiClient aiClient,
            AnalysisRepository analysisRepository,
            ApplicationEventPublisher eventPublisher) { // 2. Wire up the bean
        this.ragRetriever = ragRetriever;
        this.aiClient = aiClient;
        this.analysisRepository = analysisRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Executes the complete RAG-to-AI analysis pipeline, persists the
     * transaction, and broadcasts a domain event upon completion.
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

        if (aiResult.flaws() != null) {
            List<DetectedFlaw> internalFlaws = aiResult.flaws().stream()
                    .filter(aiFlaw -> aiFlaw != null && aiFlaw.identifiedFallacyName() != null)
                    .map(aiFlaw -> {
                        DetectedFlaw flaw = new DetectedFlaw();
                        flaw.setIdentifiedFallacyName(aiFlaw.identifiedFallacyName());
                        flaw.setFlawedSnippet(aiFlaw.flawedSnippet());
                        flaw.setAiJustification(aiFlaw.aiJustification());
                        return flaw;
                    }).toList();
            analysis.getDetectedFlaws().addAll(internalFlaws);
        }

        boolean trulyHasFlaws = Boolean.TRUE.equals(aiResult.containsFlaws()) && !analysis.getDetectedFlaws().isEmpty();
        analysis.setContainsFlaws(trulyHasFlaws);

        ArgumentAnalysis savedAnalysis = analysisRepository.save(analysis);

        // 4. Event Publication: Broadcast completion to listening modules
        // asynchronously
        eventPublisher.publishEvent(new ArgumentValidatedEvent(
                savedAnalysis.getId(),
                savedAnalysis.isContainsFlaws(),
                LocalDateTime.now()));

        // 5. Return DTO: Map database entity state to clean public output payload
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