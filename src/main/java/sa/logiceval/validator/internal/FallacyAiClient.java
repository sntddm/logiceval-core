package sa.logiceval.validator.internal;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Component;

@Component
public class FallacyAiClient {

    private final ChatClient chatClient;
    private final BeanOutputConverter<AiEvaluationResult> outputConverter = new BeanOutputConverter<>(
            AiEvaluationResult.class);

    // Spring AI automatically manages and injects the global ChatClient.Builder
    // bean
    public FallacyAiClient(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public AiEvaluationResult analyzeArgument(String text, String ragContext) {
        String systemPrompt = """
                You are an expert logical analysis system. Analyze the provided user input for logical fallacies using ONLY the provided reference context.

                Reference Context:
                {context}

                CRITICAL OUTPUT RULES:
                1. You MUST respond with raw JSON that perfectly matches the requested schema.
                2. Do NOT wrap your response in markdown blocks like ```json or ```.
                3. Do NOT include any conversational introduction, explanation, or tail-end text.
                4. Ensure all properties use standard JSON formatting with a colon (:) between keys and values. Never use equal signs (=).

                Requested JSON Format Schema:
                {format}
                """;

        return this.chatClient.prompt()
                .system(sp -> sp.text(systemPrompt)
                        .param("context", ragContext)
                        .param("format", outputConverter.getFormat()))
                .user(text)
                .call()
                .entity(outputConverter);
    }
}