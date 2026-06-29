package sa.logiceval.catalog;

import sa.logiceval.catalog.internal.FallacyDefinition;
import sa.logiceval.catalog.internal.FallacyDefinitionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FallacyService {

    private final FallacyDefinitionRepository fallacyDefinitionRepository;

    // Package-private constructor constructor injection
    FallacyService(FallacyDefinitionRepository fallacyRepository) {
        this.fallacyDefinitionRepository = fallacyRepository;
    }

    @Transactional(readOnly = true)
    public List<FallacyDTO> getAllFallacies() {
        return fallacyDefinitionRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<FallacyDTO> getFallacyById(Long id) {
        return fallacyDefinitionRepository.findById(id).map(this::mapToDTO);
    }

    // Helper mapper mapping internal entity to public record
    private FallacyDTO mapToDTO(FallacyDefinition entity) {
        return new FallacyDTO(
                entity.getId(),
                entity.getName(),
                entity.getLatinName(),
                entity.getLogicalFlawDescription(),
                entity.getCategory().getName(),
                entity.getTextbookExample());
    }
}