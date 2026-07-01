-- ==========================================
-- 1. SEED HIERARCHICAL CATEGORIES
-- ==========================================

-- Stage 1: Insert Core Root Categories (No Parents)
INSERT INTO
    fallacy_categories (
        id,
        parent_id,
        name,
        description
    )
VALUES (
        1,
        NULL,
        'Informal Fallacies',
        'Arguments that fail because their premises are irrelevant, misleading, or psychologically manipulative.'
    ),
    (
        2,
        NULL,
        'Formal Fallacies',
        'Arguments with a flawed logical structure or form, rendering them completely invalid regardless of content.'
    )
ON CONFLICT (id) DO NOTHING;

-- Stage 2: Insert Layered Sub-Categories (Referencing Core Roots)
INSERT INTO
    fallacy_categories (
        id,
        parent_id,
        name,
        description
    )
VALUES (
        3,
        1,
        'Fallacies of Relevance',
        'These divert attention away from the actual argument using red herrings.'
    ),
    (
        4,
        1,
        'Fallacies of Presumption',
        'These jump to conclusions based on hidden, unproven, or unjustified assumptions.'
    ),
    (
        5,
        1,
        'Fallacies of Weak Induction',
        'The evidence provided is simply too weak or inappropriate to support the conclusion.'
    ),
    (
        6,
        1,
        'Fallacies of Ambiguity',
        'These rely on language tricks, shifts in word meanings, grammar flaws, or parts of a whole.'
    ),
    (
        7,
        2,
        'Propositional Fallacies',
        'Structurally invalid arguments involving conditional statements.'
    ),
    (
        8,
        2,
        'Syllogistic Fallacies',
        'Flaws in structural categorical arguments using quantifiers.'
    )
ON CONFLICT (id) DO NOTHING;

-- ==========================================
-- 2. SEED CONCRETE FALLACY DEFINITIONS
-- ==========================================

-- Change 'alias_name' to 'latin_name' in the insert target signature
INSERT INTO
    fallacy_definitions (
        category_id,
        name,
        latin_name,
        logical_flaw_description,
        textbook_example
    )
VALUES

-- --- A. Fallacies of Relevance (Category 3) ---
(
    3,
    'Ad Hominem',
    'Argumentum ad hominem',
    'Attacking the person''s character or motives instead of their argument.',
    'Our opponent''s tax proposal cannot be trusted; after all, he didn''t even graduate from an Ivy League university.'
),
(
    3,
    'Tu Quoque',
    'Appeal to Hypocrisy',
    'Dismissing a claim because the speaker does not practice what they preach.',
    'My doctor told me to stop smoking to improve my health, but I saw him smoking outside the clinic yesterday, so his advice is nonsense.'
),
(
    3,
    'Straw Man',
    NULL,
    'Misrepresenting or oversimplifying an opponent’s argument to make it easier to attack.',
    'Senator Smith thinks we should optimize our military budget. It''s shocking that he wants to leave our nation completely defenseless against foreign invaders.'
),
(
    3,
    'Red Herring',
    NULL,
    'Introducing an completely irrelevant topic to distract from the core issue.',
    'Why should we worry about climate change regulations when there are thousands of people out of work right now who need jobs?'
),
(
    3,
    'Genetic Fallacy',
    NULL,
    'Judging a claim based purely on its historical or cultural origin rather than its current merit.',
    'The media platform you are quoting was originally started by an eccentric billionaire as a hobby, so anything published there must be completely biased.'
),

-- --- B. Fallacies of Presumption (Category 4) ---
(
    4,
    'Slippery Slope',
    NULL,
    'Arguing without evidence that one small step will inevitably trigger a catastrophic chain reaction.',
    'If we allow students to use calculators in this math class, next they will want them for every test, and eventually, no one will know how to add numbers manually.'
),
(
    4,
    'False Dilemma',
    'Black-and-White',
    'Forcing a choice between two extreme options when other valid alternatives exist.',
    'Either you support our current economic policy or you want our country to fall into absolute financial ruin.'
),
(
    4,
    'Begging the Question',
    'Circular Reasoning',
    'Disguising the conclusion as one of the premises.',
    'The soul is immortal because it cannot die, and since it can never truly perish, it must live on forever.'
),
(
    4,
    'Loaded Question',
    NULL,
    'Asking a question that contains a presupposition of guilt or an unproven assumption.',
    'Have you finally decided to stop cheating on your weekly exams?'
),
(
    4,
    'Texas Sharpshooter',
    NULL,
    'Cherry-picking a cluster of data to fit a narrative while intentionally ignoring the broader dataset.',
    'The new energy drink company pointed out that three local athletes won championships while drinking their product, ignoring the hundreds of others who lost.'
),

-- --- C. Fallacies of Weak Induction (Category 5) ---
(
    5,
    'Hasty Generalization',
    NULL,
    'Drawing a universal conclusion from a tiny, unrepresentative sample size.',
    'I met two rude drivers during my visit to that city, so everyone living there must be completely terrible at driving.'
),
(
    5,
    'False Cause',
    'Post Hoc Ergo Propter Hoc',
    'Claiming that because Event B followed Event A, Event A must have directly caused it.',
    'I wore my lucky blue socks today and scored the winning point. Therefore, those socks caused our team to win the game.'
),
(
    5,
    'Appeal to Ignorance',
    'Ad Ignorantiam',
    'Arguing a claim is true simply because it has not been proven false yet (or vice versa).',
    'No one has ever conclusively proven that extraterrestrials haven''t visited Earth, so they must be watching us.'
),
(
    5,
    'Appeal to Authority',
    'Ad Verecundiam',
    'Citing an unqualified expert or using an expert''s opinion outside their legitimate field.',
    'The famous actor stated in his latest interview that this specific herbal tea cures cellular aging, so I am going to buy a case.'
),
(
    5,
    'Weak Analogy',
    NULL,
    'Comparing two things that share minor similarities but are fundamentally different.',
    'Guns are made of metal and can kill people, and cars are also made of metal and kill people in accidents. If we regulate guns, we should require a license to look at cars.'
),

-- --- D. Fallacies of Ambiguity (Category 6) ---
(
    6,
    'Equivocation',
    NULL,
    'Shifting the meaning of a single word halfway through an argument to create a false conclusion.',
    'Giving money to charities is a right thing to do. So, charities have a right to take our money whenever they want.'
),
(
    6,
    'Amphiboly',
    NULL,
    'Using loose, vague, or faulty grammar to create a double meaning that misleads the listener.',
    'The sign outside said: ''Watch repairs done here while you wait.'' So if I don''t wait, they won''t fix my watch.'
),
(
    6,
    'Fallacy of Composition',
    NULL,
    'Assuming what is true for an individual part must be true for the whole entity.',
    'Every single component used to build this engine is extremely lightweight. Therefore, the entire engine itself must weigh almost nothing.'
),
(
    6,
    'Fallacy of Division',
    NULL,
    'Assuming what is true for the whole must be true for every individual part.',
    'Our company is incredibly profitable and successful, so every single employee working here must be making an excellent salary.'
),

-- --- E. Propositional Fallacies (Category 7) ---
(
    7,
    'Affirming the Consequent',
    'Conversio pura',
    'Structure: If P, then Q. Q is true. Therefore, P is true.',
    'If it rains, the grass gets wet. The grass is wet. Therefore, it rained. (Flaw: Someone could have turned on a lawn sprinkler).'
),
(
    7,
    'Denying the Antecedent',
    NULL,
    'Structure: If P, then Q. P is false. Therefore, Q is false.',
    'If you are a lawyer, you know the law. You are not a lawyer. Therefore, you do not know the law.'
),

-- --- F. Syllogistic Fallacies (Category 8) ---
(
    8,
    'Fallacy of Undistributed Middle',
    NULL,
    'Structure: All X are Y. Z is a Y. Therefore, Z is an X.',
    'All dogs are mammals. A whale is a mammal. Therefore, a whale is a dog.'
)
ON CONFLICT (name) DO NOTHING;

-- ==========================================
-- 3. ALIGN PRIMARY KEY SEQUENCES
-- ==========================================
SELECT setval(
        'fallacy_categories_id_seq', (
            SELECT MAX(id)
            FROM fallacy_categories
        )
    );

SELECT setval(
        'fallacy_definitions_id_seq', (
            SELECT MAX(id)
            FROM fallacy_definitions
        )
    );