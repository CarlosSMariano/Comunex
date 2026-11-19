
package io.github.carlossmariano.comunex.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@Profile("!test")
public class JwtConfig {

    @Value("${app.jwt.private-key}")
    private String privateKeyPem;

    @Value("${app.jwt.public-key}")
    private String publicKeyPem;

    @Bean
    public RSAPrivateKey rsaPrivateKey() {
        try {
            String pem = readPem(privateKeyPem);

            byte[] decoded = Base64.getDecoder().decode(pem);

            KeyFactory factory = KeyFactory.getInstance("RSA");

            return (RSAPrivateKey) factory.generatePrivate(
                    new PKCS8EncodedKeySpec(decoded)
            );

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Não foi possível carregar a chave privada RSA", e
            );
        }
    }

    @Bean
    public RSAPublicKey rsaPublicKey() {
        try {
            String pem = readPem(publicKeyPem);

            byte[] decoded = Base64.getDecoder().decode(pem);

            KeyFactory factory = KeyFactory.getInstance("RSA");

            return (RSAPublicKey) factory.generatePublic(
                    new X509EncodedKeySpec(decoded)
            );

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Não foi possível carregar a chave pública RSA", e
            );
        }
    }

    @Bean
    public JwtEncoder jwtEncoder(
            RSAPublicKey publicKey,
            RSAPrivateKey privateKey) {

        JWK jwk = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .build();

        ImmutableJWKSet<SecurityContext> jwkSource =
                new ImmutableJWKSet<>(new JWKSet(jwk));

        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAPublicKey publicKey) {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    private String readPem(String pem) {
        return pem
                .replace("\\n", "\n")
                .replaceAll("-----BEGIN [^-]+-----", "")
                .replaceAll("-----END [^-]+-----", "")
                .replaceAll("\\s", "");
    }
}
