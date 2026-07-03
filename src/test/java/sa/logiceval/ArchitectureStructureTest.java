package sa.logiceval;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;
import org.springframework.modulith.docs.Documenter.CanvasOptions;
import org.springframework.modulith.docs.Documenter.DiagramOptions;

class ArchitectureStructureTest {

    // Analyzes our package layout strategy
    ApplicationModules modules = ApplicationModules.of(FallacyEngineApplication.class);

    @Test
    void verifyModularArchitecture() {
        modules.verify();
    }

    @Test
    void createDocumentation() {
        // 1. Setup the diagram options (UML style instead of C4)
        DiagramOptions diagramOptions = DiagramOptions.defaults()
                .withStyle(DiagramOptions.DiagramStyle.UML);

        // 2. Setup canvas options (exposes components inside internal packages)
        CanvasOptions canvasOptions = CanvasOptions.defaults()
                .revealInternals();

        // 3. Generate everything together cleanly
        new Documenter(modules).writeDocumentation(diagramOptions, canvasOptions);
    }
}