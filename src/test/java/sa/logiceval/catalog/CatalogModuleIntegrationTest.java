package sa.logiceval.catalog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import sa.logiceval.catalog.internal.FallacyCatalogService;
import sa.logiceval.validator.ArgumentValidatedEvent;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

@ApplicationModuleTest
class CatalogModuleIntegrationTest {

    @Autowired
    ApplicationEventPublisher eventPublisher;

    // 1. Mock the service layer so we can easily track when the async listener
    // invokes it
    @MockitoBean
    FallacyCatalogService catalogService;

    @Test
    void shouldProcessIncomingValidationEvent(Scenario scenario) {
        // 2. Set up our cross-module test event
        ArgumentValidatedEvent testEvent = new ArgumentValidatedEvent(
                999L,
                true,
                LocalDateTime.now());

        // 3. Fire the event into the application context bus
        scenario.stimulate(() -> eventPublisher.publishEvent(testEvent))
                .andCleanup(() -> {
                    // 4. Use an asynchronous Mockito verify block to wait for the background
                    // listener
                    // thread to invoke our catalog service method safely!
                    verify(catalogService, timeout(2000)).updateValidationMetrics(true);
                });
    }
}
