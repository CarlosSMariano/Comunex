
package io.github.carlossmariano.comunex.usuario.service;

import io.github.carlossmariano.comunex.bairro.entity.Bairro;
import io.github.carlossmariano.comunex.bairro.service.BairroService;
import io.github.carlossmariano.comunex.usuario.dto.request.AtualizarUsuarioRequest;
import io.github.carlossmariano.comunex.usuario.dto.request.CadastroUsuarioRequest;
import io.github.carlossmariano.comunex.usuario.dto.response.UsuarioResponse;
import io.github.carlossmariano.comunex.usuario.entity.Usuario;
import io.github.carlossmariano.comunex.usuario.enums.TipoPerfil;
import io.github.carlossmariano.comunex.usuario.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BairroService bairroService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            BairroService bairroService,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.bairroService = bairroService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponse cadastrar(CadastroUsuarioRequest request) {

        String email = request.email().trim().toLowerCase();

        if (usuarioRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email já cadastrado."
            );
        }

        TipoPerfil tipoPerfil;

        try {
            tipoPerfil = TipoPerfil.valueOf(
                    request.tipoPerfil().toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tipo de perfil inválido."
            );
        }

        if (tipoPerfil == TipoPerfil.ADMINISTRADOR) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Não é permitido cadastrar-se como administrador."
            );
        }

        Long idBairro;
        TipoPerfil perfilFinal = tipoPerfil;

        if (request.idBairro() != null) {

            if (!bairroService.existePorId(request.idBairro())) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Bairro não encontrado."
                );
            }

            idBairro = request.idBairro();

        } else {

            if (tipoPerfil != TipoPerfil.MORADOR) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Somente moradores podem criar um novo bairro."
                );
            }

            if (request.nomeBairro() == null
                    || request.nomeBairro().isBlank()
                    || request.municipio() == null
                    || request.municipio().isBlank()
                    || request.estado() == null
                    || request.estado().isBlank()) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Informe nome, município e estado do novo bairro."
                );
            }

            Bairro bairro = bairroService.criar(
                    request.nomeBairro(),
                    request.municipio(),
                    request.estado()
            );

            idBairro = bairro.getIdBairro();
            perfilFinal = TipoPerfil.ADMINISTRADOR;
        }

        Usuario usuario = new Usuario();

        usuario.setNome(request.nome());
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.setDescricao(request.descricao());
        usuario.setTipoPerfil(perfilFinal);
        usuario.setIdBairro(idBairro);

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return new UsuarioResponse(
                usuarioSalvo.getIdUsuario(),
                usuarioSalvo.getIdBairro(),
                usuarioSalvo.getNome(),
                usuarioSalvo.getEmail(),
                usuarioSalvo.getDescricao(),
                usuarioSalvo.getTipoPerfil(),
                usuarioSalvo.getDataCadastro()
        );
    }

    public UsuarioResponse buscarPorEmail(String email){
       Usuario usuario = usuarioRepository.findByEmail(email)
               .orElseThrow(() -> new ResponseStatusException(
                       HttpStatus.NOT_FOUND, "Usuário não encontrado"));

       return new UsuarioResponse(
               usuario.getIdUsuario(),
               usuario.getIdBairro(),
               usuario.getNome(),
               usuario.getEmail(),
               usuario.getDescricao(),
               usuario.getTipoPerfil(),
               usuario.getDataCadastro()
       );
    }

    @Transactional
    public UsuarioResponse atualizarPerfil(String email, AtualizarUsuarioRequest request){
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if(request.nome() != null && !request.nome().isBlank()){
            usuario.setNome(request.nome());
        }

        if(request.descricao() != null && !request.descricao().isBlank()){
            usuario.setDescricao(request.descricao());
        }

        return new UsuarioResponse(
                usuario.getIdUsuario(),
                usuario.getIdBairro(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getDescricao(),
                usuario.getTipoPerfil(),
                usuario.getDataCadastro()
        );

    }

    @Transactional
    public void deletarConta(String email){
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        usuarioRepository.delete(usuario);
    }
}
