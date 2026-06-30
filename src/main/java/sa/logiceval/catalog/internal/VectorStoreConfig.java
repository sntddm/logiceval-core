package sa.logiceval.catalog.internal;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
// import org.springframework.ai.embedding.EmbeddingModel;
// import org.springframework.ai.openai.OpenAiEmbeddingModel;
// import org.springframework.ai.openai.api.OpenAiApi;
// import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
// import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
class VectorStoreConfig {

    // For local development, an in-memory SimpleVectorStore works perfectly
    // @Bean
    // public VectorStore vectorStore(EmbeddingModel embeddingModel) {
    // // return new SimpleVectorStore(embeddingModel);
    // return SimpleVectorStore.builder(embeddingModel).build();
    // }

    @Bean
    public VectorStore ollamaVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        // Creates a real connection utilizing Postgres's native vector operations
        return PgVectorStore.builder(jdbcTemplate, embeddingModel).build();
    }
}