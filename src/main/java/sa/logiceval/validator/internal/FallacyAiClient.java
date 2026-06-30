package sa.logiceval.validator.internal;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class FallacyAiClient {

    private final ChatClient chatClient;

    // We build the client using a pre-configured ChatClient.Builder
    FallacyAiClient(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Dispatches the composite RAG context and user argument text to the LLM
     * and guarantees a structurally valid Java response object matching our record.
     */
    public AiEvaluationResult analyzeArgument(String userArgumentText, String ragContext) {
        String systemPrompt = """
                You are a strict, objective Logical Fallacy Validator Engine.
                Your task is to analyze the provided human statement and identify if it commits any logical fallacies.

                Use the following micro-catalog matching references as your absolute ground truth:
                {ragContext}

                Rules:
                1. ALWAYS include the 'containsFlaws' field as true or false. Never omit it.
                2. If a fallacy name is identified, you MUST copy its corresponding 'Core Flaw' description
                   directly from the reference context and use it as part of your 'aiJustification'. Do not mix definitions.
                2. If a flaw matches one of the provided reference fallacies, extract it.
                3. Exact matching string values are required for 'identifiedFallacyName' fields.
                4. Provide clear analytical justifications.
                """;

        return this.chatClient.prompt()
                .system(sp -> sp.text(systemPrompt).param("ragContext", ragContext))
                .user(userArgumentText)
                // Spring AI 2.0 Feature: Natively forces model compliance to this target schema
                .call()
                .entity(AiEvaluationResult.class);
    }
}