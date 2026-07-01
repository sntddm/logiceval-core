-- 1. Ensure the PostgreSQL vector extension is loaded inside our instance
CREATE EXTENSION IF NOT EXISTS vector;

-- 2. Construct the Core Hierarchical Catalog Layout (UPDATED)
CREATE TABLE fallacy_categories (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT, -- Missing self-referential structural link
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(1000), -- Missing descriptive anchor
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES fallacy_categories (id) ON DELETE CASCADE
);

CREATE TABLE fallacy_definitions (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL UNIQUE,
    latin_name VARCHAR(255), -- Changed from alias_name to latin_name
    logical_flaw_description VARCHAR(2000) NOT NULL,
    textbook_example VARCHAR(2000) NOT NULL,
    CONSTRAINT fk_fallacy_category FOREIGN KEY (category_id) REFERENCES fallacy_categories (id)
);

-- 3. Construct the Operational Audit Logging Frame
CREATE TABLE argument_analyses (
    id BIGSERIAL PRIMARY KEY,
    raw_input_text VARCHAR(4000) NOT NULL,
    analyzed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    contains_flaws BOOLEAN NOT NULL
);

CREATE TABLE detected_flaws (
    id BIGSERIAL PRIMARY KEY,
    analysis_id BIGINT,
    identified_fallacy_name VARCHAR(255) NOT NULL,
    flawed_snippet VARCHAR(1000) NOT NULL,
    ai_justification VARCHAR(2000) NOT NULL,
    CONSTRAINT fk_flaw_analysis FOREIGN KEY (analysis_id) REFERENCES argument_analyses (id) ON DELETE CASCADE
);