package sa.logiceval.validator.internal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sa.logiceval.validator.ArgumentValidatorService;
import sa.logiceval.validator.EvaluationResultDTO;

@RestController
@RequestMapping("/api/validate")
public class ArgumentValidatorRestController {

    private final ArgumentValidatorService validatorService;

    ArgumentValidatorRestController(ArgumentValidatorService validatorService) {
        this.validatorService = validatorService;
    }

    /**
     * API Version 1.0 - Simple Synchronous Text Validation
     * Expects a raw string in the HTTP Request Body.
     */
    @PostMapping(version = "1.0", consumes = "text/plain")
    public ResponseEntity<EvaluationResultDTO> validateArgumentV1(@RequestBody String rawText) {
        EvaluationResultDTO result = validatorService.validateArgument(rawText);
        return ResponseEntity.ok(result);
    }

    // TODO
    /**
     * API Version 2.0 - Rich Parameterized Payload Validation
     * Expects a JSON object in the Request Body.
     */
    @PostMapping(version = "2.0", consumes = "application/json")
    public ResponseEntity<EvaluationResultDTO> validateArgumentV2(@RequestBody ValidationRequestV2 request) {
        // Leverages the exact same core engine, mapping the target property out of the
        // request payload
        EvaluationResultDTO result = validatorService.validateArgument(request.argumentText());
        return ResponseEntity.ok(result);
    }

    /**
     * Private request body representation sealed to this controller's version
     * contract.
     */
    private record ValidationRequestV2(String argumentText) {
    }
}