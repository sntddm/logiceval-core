package sa.logiceval.validator;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/validate")
class ArgumentValidatorRestController {

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

    /**
     * API Version 2.0 - Rich Parameterized Payload Validation
     * Expects a JSON object in the Request Body.
     */
    @PostMapping(version = "2.0", consumes = "application/json")
    public ResponseEntity<EvaluationResultDTO> validateArgumentV2(@RequestBody ValidationRequestV2 request) {
        // We leverage the exact same core engine, while capturing extended request
        // parameters
        // e.g., mapping request.argumentText() out of the structured object
        EvaluationResultDTO result = validatorService.validateArgument(request.argumentText());
        return ResponseEntity.ok(result);
    }
}