package io.github.carlossmariano.comunex.usuario.service;

import io.github.carlossmariano.comunex.bairro.service.BairroService;
import io.github.carlossmariano.comunex.usuario.dto.request.CadastroUsuarioRequest;
import io.github.carlossmariano.comunex.usuario.dto.response.UsuarioResponse;
import io.github.carlossmariano.comunex.usuario.entity.Usuario;
import io.github.carlossmariano.comunex.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private BairroService bairroService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveCadastrarUsuarioComSucesso() {

        CadastroUsuarioRequest request = new CadastroUsuarioRequest(
                "Teste",
                "teste@comunex.com",
                "Senha123!",
                "Descrição",
                "MORADOR",
                1L
        );

        when(usuarioRepository.existsByEmail(request.email()))
                .thenReturn(false);

        when(bairroService.existePorId(1L))
                .thenReturn(false);

        when(passwordEncoder.encode(request.senha()))
                .thenReturn("Senha-criptografada");

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> {
                    Usuario usuario = invocation.getArgument(0);
                    usuario.setIdUsuario(1L);
                    return usuario;
                });

        UsuarioResponse response = usuarioService.cadastrar(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Teste", response.nome());
        assertEquals("teste@comunex.com", response.email());
        assertEquals("MORADOR", response.tipoPerfil());

        ArgumentCaptor<Usuario> captor =
                ArgumentCaptor.forClass(Usuario.class);

        verify(usuarioRepository).save(captor.capture());

        assertEquals(
                "senha-criptografada",
                captor.getValue().getSenha()
        );

        verify(passwordEncoder).encode(request.senha());
    }

    @Test
    void naoDeveCadastrarUsuarioComEmailDuplicado(){
        CadastroUsuarioRequest request = new CadastroUsuarioRequest(
                "Teste",
                "teste@comunex.com",
                "Senha123!",
                null,
                "MORADOR",
                1L
        );

        when(usuarioRepository.existsByEmail(request.email()))
                .thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> usuarioService.cadastrar(request)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());

        verify(usuarioRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void naoDeveCadastrarUsuarioComBairroInexistente(){
        CadastroUsuarioRequest request = new CadastroUsuarioRequest(
                "Teste",
                "teste@comunex.com",
                "Senha123!",
                null,
                "MORADOR",
                99L
        );

        when(usuarioRepository.existsByEmail(request.email()))
                .thenReturn(false);

        when(bairroService.existePorId(99L))
                .thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> usuarioService.cadastrar(request)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void naoDevePermitirCadastroComoAdministrador() {

        CadastroUsuarioRequest request = new CadastroUsuarioRequest(
                "Teste",
                "teste@comunex.com",
                "Senha123!",
                null,
                "ADMINISTRADOR",
                1L
        );

        when(usuarioRepository.existsByEmail(request.email()))
                .thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> usuarioService.cadastrar(request)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());

        verify(usuarioRepository, never()).save(any());
    }
}