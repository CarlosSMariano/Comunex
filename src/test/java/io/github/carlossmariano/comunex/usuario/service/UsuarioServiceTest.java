
package io.github.carlossmariano.comunex.usuario.service;

import io.github.carlossmariano.comunex.bairro.entity.Bairro;
import io.github.carlossmariano.comunex.bairro.service.BairroService;
import io.github.carlossmariano.comunex.usuario.dto.request.CadastroUsuarioRequest;
import io.github.carlossmariano.comunex.usuario.dto.response.UsuarioResponse;
import io.github.carlossmariano.comunex.usuario.entity.Usuario;
import io.github.carlossmariano.comunex.usuario.enums.TipoPerfil;
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
    void deveCadastrarMoradorEmBairroExistente() {

        CadastroUsuarioRequest request = new CadastroUsuarioRequest(
                "João",
                "joao@email.com",
                "Senha123!",
                "Morador",
                "MORADOR",
                1L,
                null,
                null,
                null
        );

        when(usuarioRepository.existsByEmail("joao@email.com"))
                .thenReturn(false);

        when(bairroService.existePorId(1L))
                .thenReturn(true);

        when(passwordEncoder.encode("Senha123!"))
                .thenReturn("senha-criptografada");

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> {
                    Usuario usuario = invocation.getArgument(0);
                    usuario.setIdUsuario(10L);
                    return usuario;
                });

        UsuarioResponse response = usuarioService.cadastrar(request);

        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals(1L, response.idBairro());
        assertEquals(TipoPerfil.MORADOR, response.tipoPerfil());

        ArgumentCaptor<Usuario> captor =
                ArgumentCaptor.forClass(Usuario.class);

        verify(usuarioRepository).save(captor.capture());

        Usuario usuarioSalvo = captor.getValue();

        assertEquals("senha-criptografada", usuarioSalvo.getSenha());
        assertEquals(TipoPerfil.MORADOR, usuarioSalvo.getTipoPerfil());

        verify(bairroService).existePorId(1L);
        verify(passwordEncoder).encode("Senha123!");
    }

    @Test
    void deveCriarBairroECadastrarUsuarioComoAdministrador() {

        CadastroUsuarioRequest request = new CadastroUsuarioRequest(
                "Maria",
                "maria@email.com",
                "Senha123!",
                "Administradora",
                "MORADOR",
                null,
                "Vila Nova",
                "São Paulo",
                "SP"
        );

        Bairro bairro = new Bairro();
        bairro.setIdBairro(5L);
        bairro.setNome("Vila Nova");
        bairro.setMunicipio("São Paulo");
        bairro.setEstado("SP");

        when(usuarioRepository.existsByEmail("maria@email.com"))
                .thenReturn(false);

        when(bairroService.criar(
                "Vila Nova",
                "São Paulo",
                "SP"
        )).thenReturn(bairro);

        when(passwordEncoder.encode("Senha123!"))
                .thenReturn("senha-criptografada");

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> {
                    Usuario usuario = invocation.getArgument(0);
                    usuario.setIdUsuario(20L);
                    return usuario;
                });

        UsuarioResponse response = usuarioService.cadastrar(request);

        assertNotNull(response);
        assertEquals(20L, response.id());
        assertEquals(5L, response.idBairro());
        assertEquals(TipoPerfil.ADMINISTRADOR, response.tipoPerfil());

        verify(bairroService).criar(
                "Vila Nova",
                "São Paulo",
                "SP"
        );

        ArgumentCaptor<Usuario> captor =
                ArgumentCaptor.forClass(Usuario.class);

        verify(usuarioRepository).save(captor.capture());

        assertEquals(
                TipoPerfil.ADMINISTRADOR,
                captor.getValue().getTipoPerfil()
        );
    }


    @Test
    void naoDeveCadastrarUsuarioComEmailDuplicado() {

        CadastroUsuarioRequest request = new CadastroUsuarioRequest(
                "João",
                "joao@email.com",
                "Senha123!",
                null,
                "MORADOR",
                1L,
                null,
                null,
                null
        );

        when(usuarioRepository.existsByEmail("joao@email.com"))
                .thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> usuarioService.cadastrar(request)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());

        verify(usuarioRepository, never()).save(any());
        verifyNoInteractions(bairroService, passwordEncoder);
    }


    @Test
    void naoDeveCadastrarEmBairroInexistenteSemDadosDeCriacao() {

        CadastroUsuarioRequest request = new CadastroUsuarioRequest(
                "João",
                "joao@email.com",
                "Senha123!",
                null,
                "MORADOR",
                99L,
                null,
                null,
                null
        );

        when(usuarioRepository.existsByEmail("joao@email.com"))
                .thenReturn(false);

        when(bairroService.existePorId(99L))
                .thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> usuarioService.cadastrar(request)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(usuarioRepository, never()).save(any());
        verify(bairroService, never()).criar(any(), any(), any());
    }

    @Test
    void naoDevePermitirOrganizacaoCriarBairro() {

        CadastroUsuarioRequest request = new CadastroUsuarioRequest(
                "ONG Exemplo",
                "ong@email.com",
                "Senha123!",
                null,
                "ORGANIZACAO",
                null,
                "Vila Nova",
                "São Paulo",
                "SP"
        );

        when(usuarioRepository.existsByEmail("ong@email.com"))
                .thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> usuarioService.cadastrar(request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

        verify(bairroService, never()).criar(any(), any(), any());
        verify(usuarioRepository, never()).save(any());
    }


    @Test
    void naoDevePermitirCadastroManualComoAdministrador() {

        CadastroUsuarioRequest request = new CadastroUsuarioRequest(
                "Carlos",
                "carlos@email.com",
                "Senha123!",
                null,
                "ADMINISTRADOR",
                1L,
                null,
                null,
                null
        );

        when(usuarioRepository.existsByEmail("carlos@email.com"))
                .thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> usuarioService.cadastrar(request)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());

        verify(usuarioRepository, never()).save(any());
        verifyNoInteractions(bairroService, passwordEncoder);
    }
}
