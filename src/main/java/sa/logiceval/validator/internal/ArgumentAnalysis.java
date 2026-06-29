package sa.logiceval.validator.internal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "argument_analyses")
@Getter
@Setter
public class ArgumentAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 4000)
    private String rawInputText; // The text submitted by the user or an external AI agent

    private LocalDateTime analyzedAt = LocalDateTime.now();

    private boolean containsFallacies;

    // A single text input might contain multiple structural reasoning flaws
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "analysis_id")
    private List<DetectedFlaw> detectedFlaws = new ArrayList<>();
}