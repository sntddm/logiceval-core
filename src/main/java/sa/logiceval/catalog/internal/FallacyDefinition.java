package sa.logiceval.catalog.internal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "fallacy_definitions")
@Getter
@Setter
public class FallacyDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "latin_name")
    private String latinName;

    @Column(nullable = false, length = 2000)
    private String logicalFlawDescription;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id")
    private FallacyCategory category;

    // Concrete text snippet showcasing an example of this argument structure
    @Column(length = 2000)
    private String textbookExample;
}