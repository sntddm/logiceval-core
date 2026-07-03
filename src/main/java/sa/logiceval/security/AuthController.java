package sa.logiceval.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sa.logiceval.security.internal.AuthService;

@RestController
@RequestMapping("/api/auth")
class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        String token = authService.authenticate(loginRequest.username(), loginRequest.password());
        return new LoginResponse(token);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authService.registerUser(request.username(), request.password());
        return ResponseEntity.ok("User registered successfully! You can now authenticate via /login.");
    }

    record RegisterRequest(String username, String password) {
    }

    record LoginRequest(String username, String password) {
    }

    record LoginResponse(String token) {
    }
}
