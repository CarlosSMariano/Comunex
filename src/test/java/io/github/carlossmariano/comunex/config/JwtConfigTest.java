package io.github.carlossmariano.comunex.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@TestConfiguration
@Profile("test")
public class JwtConfigTest {

    @Bean
    @Primary
    JwtEncoder jwtEncoder() {
        return mock(JwtEncoder.class);
    }

    @Bean
    @Primary
    JwtDecoder jwtDecoder() {
        return mock(JwtDecoder.class);
    }
}