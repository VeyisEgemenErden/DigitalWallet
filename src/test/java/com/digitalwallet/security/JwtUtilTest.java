package com.digitalwallet.security;

import com.digitalwallet.entity.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();


        String secret = Base64.getEncoder().encodeToString("MyVerySecretKeyForJWTMustBeLongEnough1234".getBytes());
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", 3600000L);

        jwtUtil.init();
    }

    @Test
    void shouldGenerateAndValidateTokenCorrectly() {
        AppUser user = new AppUser();
        user.setId(42L);
        user.setUsername("Egemen");
        user.setRole("CUSTOMER");

        String token = jwtUtil.generateToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtUtil.isTokenValid(token)).isTrue();
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("Egemen");
        assertThat(jwtUtil.extractUserId(token)).isEqualTo(42L);
    }

    @Test
    void shouldReturnFalseForInvalidToken() {
        String invalidToken = "this.is.not.a.valid.token";

        assertThat(jwtUtil.isTokenValid(invalidToken)).isFalse();
    }

    @Test
    void shouldThrowException_whenTokenIsTampered() {
        AppUser user = new AppUser();
        user.setId(99L);
        user.setUsername("Egemen");
        user.setRole("EMPLOYEE");

        String token = jwtUtil.generateToken(user);
        String tamperedToken = token.substring(0, token.length() - 2) + "XX";

        assertThat(jwtUtil.isTokenValid(tamperedToken)).isFalse();
        assertThrows(Exception.class, () -> jwtUtil.extractUsername(tamperedToken));
    }
}
