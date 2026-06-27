package sa.logiceval;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ArchitectureStructureTest {

    // Analyzes our package layout strategy
    ApplicationModules modules = ApplicationModules.of(FallacyEngineApplication.class);

    @Test
    void verifyModularArchitecture() {
        // This line checks for circular dependencies and package encapsulation
        // violations
        modules.verify();
    }

    @Test
    void createDocumentation() {
        // Automatically writes Asciidoc/PlantUML design diagrams inside
        // build/spring-modulith/
        new Documenter(modules).writeModulesAsPlantUml();
    }
}