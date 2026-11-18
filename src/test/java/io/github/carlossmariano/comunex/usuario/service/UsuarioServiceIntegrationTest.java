
package io.github.carlossmariano.comunex.usuario.service;

import io.github.carlossmariano.comunex.bairro.entity.Bairro;
import io.github.carlossmariano.comunex.bairro.repository.BairroRepository;
import io.github.carlossmariano.comunex.config.JwtConfigTest;
import io.github.carlossmariano.comunex.usuario.dto.request.CadastroUsuarioRequest;
import io.github.carlossmariano.comunex.usuario.dto.response.UsuarioResponse;
import io.github.carlossmariano.comunex.usuario.entity.Usuario;
import io.github.carlossmariano.comunex.usuario.enums.TipoPerfil;
import io.github.carlossmariano.comunex.usuario.repository.UsuarioRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(JwtConfigTest.class)
@ActiveProfiles("test")
@Transactional
public class UsuarioServiceIntegrationTest {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BairroRepository bairroRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void deveCadastrarMoradorEmBairroExistente() {

        Bairro bairro = criarBairro();

        CadastroUsuarioRequest request = new CadastroUsuarioRequest(
                "João",
                "joao@email.com",
                "Senha123!",
                "Morador",
                "MORADOR",
                bairro.getIdBairro(),
                null,
                null,
                null
        );

        UsuarioResponse response = usuarioService.cadastrar(request);

        assertNotNull(response.id());
        assertEquals(bairro.getIdBairro(), response.idBairro());
        assertEquals(TipoPerfil.MORADOR, response.tipoPerfil());

        Usuario usuario = usuarioRepository
                .findById(response.id())
                .orElseThrow();

        assertTrue(
                passwordEncoder.matches(
                        "Senha123!",
                        usuario.getSenha()
                )
        );
    }

    @Test
    void deveCriarBairroECadastrarAdministrador() {

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

        UsuarioResponse response = usuarioService.cadastrar(request);

        assertNotNull(response.id());
        assertNotNull(response.idBairro());
        assertEquals(TipoPerfil.ADMINISTRADOR, response.tipoPerfil());

        Bairro bairro = bairroRepository
                .findById(response.idBairro())
                .orElseThrow();

        assertEquals("Vila Nova", bairro.getNome());
        assertEquals("São Paulo", bairro.getMunicipio());
        assertEquals("SP", bairro.getEstado());

        Usuario usuario = usuarioRepository
                .findById(response.id())
                .orElseThrow();

        assertEquals(TipoPerfil.ADMINISTRADOR, usuario.getTipoPerfil());
    }

    private Bairro criarBairro() {

        Bairro bairro = new Bairro();

        bairro.setNome("Centro");
        bairro.setMunicipio("São Paulo");
        bairro.setEstado("SP");

        return bairroRepository.save(bairro);
    }
}
