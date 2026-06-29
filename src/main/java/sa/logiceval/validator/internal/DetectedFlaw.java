package sa.logiceval.validator.internal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "detected_flaws")
@Getter
@Setter
public class DetectedFlaw {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String identifiedFallacyName; // Links back to our Catalog naming convention

    @Column(nullable = false, length = 1000)
    private String flawedSnippet; // The specific sentence inside the text that triggered the flaw

    @Column(nullable = false, length = 2000)
    private String aiJustification; // The rationalized explanation detailing why it fails logic checks
}