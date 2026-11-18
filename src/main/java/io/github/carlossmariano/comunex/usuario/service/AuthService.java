package io.github.carlossmariano.comunex.usuario.service;

import io.github.carlossmariano.comunex.security.JwtService;
import io.github.carlossmariano.comunex.usuario.dto.request.LoginRequest;
import io.github.carlossmariano.comunex.usuario.dto.response.AuthResponse;
import io.github.carlossmariano.comunex.usuario.entity.Usuario;
import io.github.carlossmariano.comunex.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    @Value("${jwt.expiration-seconds}")
    private long expirationSeconds;

    public AuthService(AuthenticationManager authenticationManager, UsuarioRepository usuarioRepository, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    public AuthResponse login(LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.senha()
                )
        );

        Usuario usuario = usuarioRepository.findByEmail(request.email()).orElseThrow();

        String token = jwtService.gerarToken(
                usuario.getIdUsuario(),
                usuario.getEmail(),
                usuario.getTipoPerfil().name()
        );

        return new AuthResponse(
                token,
                "Bearer",
                expirationSeconds
        );
    }
}
