package sa.logiceval.validator.internal;

import org.springframework.data.jpa.repository.JpaRepository;

interface AnalysisRepository extends JpaRepository<ArgumentAnalysis, Long> {
}