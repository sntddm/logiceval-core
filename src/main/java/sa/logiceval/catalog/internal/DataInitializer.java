package sa.logiceval.catalog.internal;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class DataInitializer implements ApplicationRunner {

    private final FallacyCategoryRepository categoryRepository;
    private final FallacyDefinitionRepository definitionRepository;

    DataInitializer(FallacyCategoryRepository categoryRepository, FallacyDefinitionRepository definitionRepository) {
        this.categoryRepository = categoryRepository;
        this.definitionRepository = definitionRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (categoryRepository.count() > 0) {
            return; // Already initialized
        }

        // ==========================================
        // 1. TOP-LEVEL MACRO CATEGORIES
        // ==========================================
        FallacyCategory informal = createCategory("Informal Fallacies",
                "Arguments that fail because their premises are irrelevant, misleading, or psychologically manipulative.",
                null);
        FallacyCategory formal = createCategory("Formal Fallacies",
                "Arguments with a flawed logical structure or form, rendering them completely invalid regardless of content.",
                null);
        // TODO: Cognitive Biases: psychological patterns

        // ==========================================
        // 2. SUB-CATEGORIES
        // ==========================================
        FallacyCategory relevance = createCategory("Fallacies of Relevance",
                "These divert attention away from the actual argument using red herrings.", informal);
        FallacyCategory presumption = createCategory("Fallacies of Presumption",
                "These jump to conclusions based on hidden, unproven, or unjustified assumptions.", informal);
        FallacyCategory weakInduction = createCategory("Fallacies of Weak Induction",
                "The evidence provided is simply too weak or inappropriate to support the conclusion.", informal);
        FallacyCategory ambiguity = createCategory("Fallacies of Ambiguity",
                "These rely on language tricks, shifts in word meanings, grammar flaws, or parts of a whole.",
                informal);

        FallacyCategory propositional = createCategory("Propositional Fallacies",
                "Structurally invalid arguments involving conditional statements.", formal);
        FallacyCategory syllogistic = createCategory("Syllogistic Fallacies",
                "Flaws in structural categorical arguments using quantifiers.", formal);

        // ==========================================
        // 3. SEEDING CONCRETE FALLACY DEFINITIONS
        // ==========================================

        // --- A. Fallacies of Relevance ---
        createFallacy("Ad Hominem", "Argumentum ad hominem",
                "Attacking the person's character or motives instead of their argument.",
                "Our opponent's tax proposal cannot be trusted; after all, he didn't even graduate from an Ivy League university.",
                relevance);

        createFallacy("Tu Quoque", "Appeal to Hypocrisy",
                "Dismissing a claim because the speaker does not practice what they preach.",
                "My doctor told me to stop smoking to improve my health, but I saw him smoking outside the clinic yesterday, so his advice is nonsense.",
                relevance);

        createFallacy("Straw Man", null,
                "Misrepresenting or oversimplifying an opponent’s argument to make it easier to attack.",
                "Senator Smith thinks we should optimize our military budget. It's shocking that he wants to leave our nation completely defenseless against foreign invaders.",
                relevance);

        createFallacy("Red Herring", null,
                "Introducing an completely irrelevant topic to distract from the core issue.",
                "Why should we worry about climate change regulations when there are thousands of people out of work right now who need jobs?",
                relevance);

        createFallacy("Genetic Fallacy", null,
                "Judging a claim based purely on its historical or cultural origin rather than its current merit.",
                "The media platform you are quoting was originally started by an eccentric billionaire as a hobby, so anything published there must be completely biased.",
                relevance);

        // --- B. Fallacies of Presumption ---
        createFallacy("Slippery Slope", null,
                "Arguing without evidence that one small step will inevitably trigger a catastrophic chain reaction.",
                "If we allow students to use calculators in this math class, next they will want them for every test, and eventually, no one will know how to add numbers manually.",
                presumption);

        createFallacy("False Dilemma", "Black-and-White",
                "Forcing a choice between two extreme options when other valid alternatives exist.",
                "Either you support our current economic policy or you want our country to fall into absolute financial ruin.",
                presumption);

        createFallacy("Begging the Question", "Circular Reasoning",
                "Disguising the conclusion as one of the premises.",
                "The soul is immortal because it cannot die, and since it can never truly perish, it must live on forever.",
                presumption);

        createFallacy("Loaded Question", null,
                "Asking a question that contains a presupposition of guilt or an unproven assumption.",
                "Have you finally decided to stop cheating on your weekly exams?", presumption);

        createFallacy("Texas Sharpshooter", null,
                "Cherry-picking a cluster of data to fit a narrative while intentionally ignoring the broader dataset.",
                "The new energy drink company pointed out that three local athletes won championships while drinking their product, ignoring the hundreds of others who lost.",
                presumption);

        // --- C. Fallacies of Weak Induction ---
        createFallacy("Hasty Generalization", null,
                "Drawing a universal conclusion from a tiny, unrepresentative sample size.",
                "I met two rude drivers during my visit to that city, so everyone living there must be completely terrible at driving.",
                weakInduction);

        createFallacy("False Cause", "Post Hoc Ergo Propter Hoc",
                "Claiming that because Event B followed Event A, Event A must have directly caused it.",
                "I wore my lucky blue socks today and scored the winning point. Therefore, those socks caused our team to win the game.",
                weakInduction);

        createFallacy("Appeal to Ignorance", "Ad Ignorantiam",
                "Arguing a claim is true simply because it has not been proven false yet (or vice versa).",
                "No one has ever conclusively proven that extraterrestrials haven't visited Earth, so they must be watching us.",
                weakInduction);

        createFallacy("Appeal to Authority", "Ad Verecundiam",
                "Citing an unqualified expert or using an expert's opinion outside their legitimate field.",
                "The famous actor stated in his latest interview that this specific herbal tea cures cellular aging, so I am going to buy a case.",
                weakInduction);

        createFallacy("Weak Analogy", null,
                "Comparing two things that share minor similarities but are fundamentally different.",
                "Guns are made of metal and can kill people, and cars are also made of metal and kill people in accidents. If we regulate guns, we should require a license to look at cars.",
                weakInduction);

        // --- D. Fallacies of Ambiguity ---
        createFallacy("Equivocation", null,
                "Shifting the meaning of a single word halfway through an argument to create a false conclusion.",
                "Giving money to charities is a right thing to do. So, charities have a right to take our money whenever they want.",
                ambiguity);

        createFallacy("Amphiboly", null,
                "Using loose, vague, or faulty grammar to create a double meaning that misleads the listener.",
                "The sign outside said: 'Watch repairs done here while you wait.' So if I don't wait, they won't fix my watch.",
                ambiguity);

        createFallacy("Fallacy of Composition", null,
                "Assuming what is true for an individual part must be true for the whole entity.",
                "Every single component used to build this engine is extremely lightweight. Therefore, the entire engine itself must weigh almost nothing.",
                ambiguity);

        createFallacy("Fallacy of Division", null,
                "Assuming what is true for the whole must be true for every individual part.",
                "Our company is incredibly profitable and successful, so every single employee working here must be making an excellent salary.",
                ambiguity);

        // --- E. Propositional Fallacies (Formal) ---
        createFallacy("Affirming the Consequent", "Conversio pura",
                "Structure: If P, then Q. Q is true. Therefore, P is true.",
                "If it rains, the grass gets wet. The grass is wet. Therefore, it rained. (Flaw: Someone could have turned on a lawn sprinkler).",
                propositional);

        createFallacy("Denying the Antecedent", null,
                "Structure: If P, then Q. P is false. Therefore, Q is false.",
                "If you are a lawyer, you know the law. You are not a lawyer. Therefore, you do not know the law.",
                propositional);

        // --- F. Syllogistic Fallacies (Formal) ---
        createFallacy("Fallacy of Undistributed Middle", null,
                "Structure: All X are Y. Z is a Y. Therefore, Z is an X.",
                "All dogs are mammals. A whale is a mammal. Therefore, a whale is a dog.", syllogistic);
    }

    private FallacyCategory createCategory(String name, String description, FallacyCategory parent) {
        FallacyCategory cat = new FallacyCategory();
        cat.setName(name);
        cat.setDescription(description);
        cat.setParent(parent);
        return categoryRepository.save(cat);
    }

    private void createFallacy(String name, String latinName, String description, String example,
            FallacyCategory category) {
        FallacyDefinition fallback = new FallacyDefinition();
        fallback.setName(name);
        fallback.setLatinName(latinName);
        fallback.setLogicalFlawDescription(description);
        fallback.setTextbookExample(example);
        fallback.setCategory(category);
        definitionRepository.save(fallback);
    }
}