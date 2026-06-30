package sa.logiceval.validator.internal;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisRepository extends JpaRepository<ArgumentAnalysis, Long> {
}