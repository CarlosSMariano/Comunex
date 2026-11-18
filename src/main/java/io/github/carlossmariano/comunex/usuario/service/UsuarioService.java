package io.github.carlossmariano.comunex.usuario.service;

import io.github.carlossmariano.comunex.bairro.service.BairroService;
import io.github.carlossmariano.comunex.usuario.dto.request.CadastroUsuarioRequest;
import io.github.carlossmariano.comunex.usuario.dto.response.UsuarioResponse;
import io.github.carlossmariano.comunex.usuario.entity.Usuario;
import io.github.carlossmariano.comunex.usuario.enums.TipoPerfil;
import io.github.carlossmariano.comunex.usuario.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;


//TODO: ADICIONAR BAIRRO
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BairroService bairroService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, BairroService bairroService, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.bairroService = bairroService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponse cadastrar(CadastroUsuarioRequest request){
        if (usuarioRepository.existsByEmail(request.email())){
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

        if (tipoPerfil == TipoPerfil.ADMINISTRADOR){
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Não é permitido cadastrar-se como administrador."
            );
        }

        if (request.idBairro() == null){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "É necessário informar um bairro."
            );
        }

        if (!bairroService.existePorId(request.idBairro())){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Bairro não encontrado."
            );
        }

        Usuario usuario = new Usuario();

        usuario.setNome(request.nome());
        usuario.setEmail(request.email().trim().toLowerCase());
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.setDescricao(request.descricao());
        usuario.setTipoPerfil(tipoPerfil);
        usuario.setIdBairro(request.idBairro());
        usuario.setDataCadastro(LocalDateTime.now());

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return new UsuarioResponse(
                usuarioSalvo.getIdUsuario(),
                usuarioSalvo.getIdBairro(),
                usuarioSalvo.getNome(),
                usuarioSalvo.getEmail(),
                usuarioSalvo.getDescricao(),
                usuarioSalvo.getTipoPerfil(),
                usuario.getDataCadastro()
        );

    }


}
