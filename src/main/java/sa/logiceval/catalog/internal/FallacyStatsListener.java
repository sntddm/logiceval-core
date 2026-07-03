package sa.logiceval.catalog.internal;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import sa.logiceval.validator.ArgumentValidatedEvent;

@Component
class FallacyStatsListener {

    private final FallacyCatalogService catalogService;

    // Package-private constructor for Spring Modulith injection optimization
    FallacyStatsListener(FallacyCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    /**
     * intercepts ArgumentValidatedEvents published across module boundaries.
     * This execution is isolated, asynchronous, and transactionally resilient.
     */
    @ApplicationModuleListener
    void onArgumentValidated(ArgumentValidatedEvent event) {
        System.out.println("\n📬 [Catalog Module] Cross-Module Event Intercepted successfully!");
        System.out.println("Processing event payload correlation ID: " + event.analysisId());

        // Delegate to the catalog service layer to process the counter metrics safely
        catalogService.updateValidationMetrics(event.containsFlaws());
    }
}