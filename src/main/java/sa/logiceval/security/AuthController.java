package sa.logiceval.security;

import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.bind.annotation.*;
import org.stringtemplate.v4.compiler.CodeGenerator.primary_return;

@RestController
@RequestMapping("/api/auth")
class AuthController {

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final SystemUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // We will register the AuthenticationManager inside SecurityConfig next

    @PostMapping("/login")
    public LoginResponse token(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
            String token = tokenService.generateToken(authentication);
            return new LoginResponse(token);
        } catch (org.springframework.security.core.AuthenticationException ex) {
            // This will print the precise error to your console log (e.g., Bad Credentials,
            // User Disabled)
            System.err.println("AUTHENTICATION ATTEMPT FAILED: " + ex.getMessage());
            throw ex;
        }
    }

    public AuthController(TokenService tokenService, AuthenticationManager authenticationManager,
            SystemUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        // 1. Check if the account name already exists
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        // 2. Map the structural user domain data
        SystemUser newUser = new SystemUser();
        newUser.setUsername(request.username());
        // CRITICAL: Always use the passwordEncoder to generate safe BCrypt salts before
        // persistence!
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setRoles(Set.of("ROLE_USER"));

        userRepository.save(newUser);
        return "User registered successfully! You can now authenticate via /login.";
    }

    record RegisterRequest(String username, String password) {
    }

    record LoginRequest(String username, String password) {
    }

    record LoginResponse(String token) {
    }

}
