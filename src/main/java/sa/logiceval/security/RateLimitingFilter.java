package sa.logiceval.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter implements Filter {

    // Cache to hold distinct traffic buckets for each unique user
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    // Define the capacity constraints: 5 requests per minute, refilling
    // continuously
    private Bucket createNewBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1))))
                .build();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Target rate limiting only on our heavy LLM execution route
        if (httpRequest.getRequestURI().startsWith("/api/validate")) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = (authentication != null) ? authentication.getName() : "anonymous";

            // Get or create the unique bucket for this user
            Bucket bucket = cache.computeIfAbsent(username, k -> createNewBucket());

            if (!bucket.tryConsume(1)) {
                httpResponse.setStatus(429); // Too Many Requests
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write(
                        "{\"error\": \"Too Many Requests\", \"message\": \"You have exceeded your LLM analysis quota. Please wait a minute.\"}");
                return; // Stop the filter chain execution here
            }
        }

        chain.doFilter(request, response);
    }
}