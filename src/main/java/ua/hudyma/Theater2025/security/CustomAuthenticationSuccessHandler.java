package ua.hudyma.Theater2025.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ua.hudyma.Theater2025.constants.UserAccessLevel;
import ua.hudyma.Theater2025.repository.UserRepository;

import java.io.IOException;

@Log4j2
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String email = authentication.getName();
        String accessToken = jwtTokenProvider.generateToken(email);
        String refreshToken = jwtTokenProvider.generateRefreshToken(email);

        Cookie accessCookie = new Cookie("Authorization", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge((int) (jwtTokenProvider.getValidityInMs() / 1000));

        Cookie refreshCookie = new Cookie("RefreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7 днів

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        var user = userRepository.findByEmail(email).orElseThrow();
        var userStatus = user.getAccessLevel();
        if (userStatus == UserAccessLevel.ADMIN
                || userStatus == UserAccessLevel.MANAGER){
            response.sendRedirect("/admin");
        } else if (userStatus == UserAccessLevel.BLOCKED) {
            response.sendRedirect("/error");
        } else {
            response.sendRedirect("/user");
        }
    }
}
