package ua.hudyma.Theater2025;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.hudyma.Theater2025.config.JwtTokenProvider;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    public void setup() {
        jwtTokenProvider = new JwtTokenProvider();
        jwtTokenProvider.secret = "mySuperSecretKey1234567890@THEATER"; // напряму для тесту
        jwtTokenProvider.init();
    }

    @Test
    void testGenerateAndValidateToken() {
        String email = "test@mail.com";
        String token = jwtTokenProvider.generateToken(email);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(email, jwtTokenProvider.getEmailFromToken(token));
    }

    @Test
    void testInvalidToken() {
        String invalidToken = "abc.def.ghi";
        assertFalse(jwtTokenProvider.validateToken(invalidToken));
    }
}