package sa.logiceval.catalog.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

interface FallacyCategoryRepository extends JpaRepository<FallacyCategory, Long> {
    Optional<FallacyCategory> findByName(String name);
}
