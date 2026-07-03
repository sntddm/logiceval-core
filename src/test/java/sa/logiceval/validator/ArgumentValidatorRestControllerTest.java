package sa.logiceval.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import sa.logiceval.validator.internal.ArgumentValidatorRestController;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArgumentValidatorRestController.class)
class ArgumentValidatorRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArgumentValidatorService validatorService;

    @Test
    @WithMockUser(username = "analyst", roles = { "USER" })
    @DisplayName("POST /api/validate V1.0 - Should process raw text/plain payloads successfully")
    void shouldProcessV1PlainTextPayload() throws Exception {
        // Arrange
        EvaluationResultDTO mockResponse = createMockResponse();
        when(validatorService.validateArgument("The target raw text argument")).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/validate")
                .with(csrf())
                .contentType(MediaType.TEXT_PLAIN) // 👈 Matches consumes = "text/plain"
                .header("X-API-Version", "1.0") // 👈 Custom version header if required by your framework setup
                .accept(MediaType.APPLICATION_JSON)
                .content("The target raw text argument"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.analysisId").value(42))
                .andExpect(jsonPath("$.containsFlaws").value(true));
    }

    @Test
    @WithMockUser(username = "analyst", roles = { "USER" })
    @DisplayName("POST /api/validate V2.0 - Should process structured application/json payloads successfully")
    void shouldProcessV2JsonPayload() throws Exception {
        // Arrange
        EvaluationResultDTO mockResponse = createMockResponse();
        when(validatorService.validateArgument("The target structured argument")).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/validate")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON) // 👈 Matches consumes = "application/json"
                .header("X-API-Version", "2.0") // 👈 Points to Version 2.0 mapping
                .content("{\"argumentText\": \"The target structured argument\"}")) // 👈 Matches ValidationRequestV2
                                                                                    // record structure
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.analysisId").value(42))
                .andExpect(jsonPath("$.flaws[0].fallacyName").value("Straw Man"));
    }

    @Test
    @DisplayName("POST /api/validate - Should deny anonymous requests with 401 Unauthorized status")
    void shouldRejectUnauthenticatedRequests() throws Exception {
        mockMvc.perform(post("/api/validate")
                .with(csrf())
                .contentType(MediaType.TEXT_PLAIN)
                .content("Anonymous post test"))
                .andExpect(status().isUnauthorized());
    }

    private EvaluationResultDTO createMockResponse() {
        return new EvaluationResultDTO(
                42L,
                "The target argument",
                LocalDateTime.now(),
                true,
                List.of(new EvaluationResultDTO.FlawDetail("Straw Man", "Target text", "Justification details")));
    }
}