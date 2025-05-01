package ua.hudyma.Theater2025.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ua.hudyma.Theater2025.service.AuthService;
import ua.hudyma.Theater2025.service.CustomUserDetailsService;
import io.jsonwebtoken.Claims;

import java.io.IOException;
import java.util.Date;

@Component
@Log4j2
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION = "Authorization";
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = authService.getTokenFromCookie(request, AUTHORIZATION);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String email = jwtTokenProvider.getEmailFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            // --- Sliding expiration
            Claims claims = jwtTokenProvider.getAllClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            if (shouldRefreshToken(expiration)) {
                String newToken = jwtTokenProvider.generateToken(email);
                Cookie refreshedCookie = new Cookie(AUTHORIZATION, newToken);
                refreshedCookie.setHttpOnly(true);
                refreshedCookie.setPath("/");
                refreshedCookie.setMaxAge((int) (jwtTokenProvider.getValidityInMs() / 1000));
                response.addCookie(refreshedCookie);
                log.info(".......... Refreshed JWT for {}", email);
            }
            log.info("......✅ Authenticated user: {}", email);
        }
        filterChain.doFilter(request, response);
    }

    /*private String getJwtFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }*/

    private boolean shouldRefreshToken(Date expiration) {
        long timeLeftMs = expiration.getTime() - System.currentTimeMillis();
        long thresholdMs = 15 * 60 * 1000; // 15 хв до закінчення
        return timeLeftMs < thresholdMs;
    }
}
