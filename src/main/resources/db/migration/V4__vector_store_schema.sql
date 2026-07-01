-- 1. Ensure the UUID extension is enabled for unique token identifiers
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. Construct the core table expected natively by Spring AI's PgVectorStore
CREATE TABLE IF NOT EXISTS public.fallacy_vector_store (
    id UUID DEFAULT uuid_generate_v4 () PRIMARY KEY,
    content TEXT NOT NULL,
    metadata JSONB NOT NULL,
    embedding VECTOR (768) NOT NULL -- Match the exact 768-dimension layout of nomic-embed-text
);

-- 3. Create a high-performance HNSW index to accelerate similarity vector lookups
CREATE INDEX IF NOT EXISTS idx_fallacy_vector_store_hnsw ON public.fallacy_vector_store USING hnsw (embedding vector_cosine_ops);