package hr.lektie.webapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Reads the JWT from the auth cookie (Lektie Studio) or, failing that, from "Authorization: Bearer ..."
 * (curl, Postman, other services). A missing or invalid token leaves the request anonymous; SecurityConfig
 * then decides whether that is allowed.
 *
 * Deliberately not a @Component: Spring Boot would also register it as a plain servlet filter and run it twice.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final AuthCookies authCookies;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, AuthCookies authCookies) {
        this.jwtUtil = jwtUtil;
        this.authCookies = authCookies;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        authCookies.read(request).flatMap(jwtUtil::parse)
                .or(() -> bearerToken(request).flatMap(jwtUtil::parse))
                .ifPresent(user -> {
                    var auth = UsernamePasswordAuthenticationToken.authenticated(
                            user, null, List.of(new SimpleGrantedAuthority(user.role().authority())));
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                });
        chain.doFilter(request, response);
    }

    private Optional<String> bearerToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return Optional.of(header.substring(BEARER_PREFIX.length()).trim());
        }
        return Optional.empty();
    }
}
