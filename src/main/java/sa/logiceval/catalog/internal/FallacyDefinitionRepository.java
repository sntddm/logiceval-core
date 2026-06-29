package sa.logiceval.catalog.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface FallacyDefinitionRepository extends JpaRepository<FallacyDefinition, Long> {

    // Custom query to pull fallacies belonging to a parent category hierarchy
    @Query("SELECT f FROM FallacyDefinition f WHERE f.category.id = :categoryId OR f.category.parent.id = :categoryId")
    List<FallacyDefinition> findByCategoryIdOrParentId(Long categoryId);
}