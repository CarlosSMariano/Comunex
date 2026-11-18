
package io.github.carlossmariano.comunex.usuario.service;

import io.github.carlossmariano.comunex.security.JwtService;
import io.github.carlossmariano.comunex.usuario.dto.request.LoginRequest;
import io.github.carlossmariano.comunex.usuario.dto.response.AuthResponse;
import io.github.carlossmariano.comunex.usuario.entity.Usuario;
import io.github.carlossmariano.comunex.usuario.enums.TipoPerfil;
import io.github.carlossmariano.comunex.usuario.repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private LoginRequest request;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        request = new LoginRequest("teste@email.com", "senha123");

        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setEmail("teste@email.com");
        usuario.setTipoPerfil(TipoPerfil.MORADOR);
    }

    @Test
    void deveRealizarLoginComSucesso() {
        when(usuarioRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(usuario));

        when(jwtService.gerarToken(any(), any(), any()))
                .thenReturn("token-falso");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("token-falso", response.token());
        assertEquals("Bearer", response.tipo());

        verify(authenticationManager).authenticate(any());
        verify(jwtService).gerarToken(any(), any(), any());
    }

    @Test
    void deveFalharQuandoSenhaEstiverIncorreta() {
        doThrow(new BadCredentialsException("Credenciais inválidas"))
                .when(authenticationManager)
                .authenticate(any());

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verify(usuarioRepository, never()).findByEmail(any());
        verify(jwtService, never()).gerarToken(any(), any(), any());
    }

    @Test
    void deveFalharQuandoEmailNaoExistir() {
        when(usuarioRepository.findByEmail(request.email()))
                .thenReturn(Optional.empty());

        assertThrows(
                java.util.NoSuchElementException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never()).gerarToken(any(), any(), any());
    }
}
