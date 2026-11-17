package io.github.carlossmariano.comunex.usuario.dto.response;

import io.github.carlossmariano.comunex.usuario.enums.TipoPerfil;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        Long idBairro,
        String nome,
        String email,
        String descricao,
        TipoPerfil tipoPerfil,
        LocalDateTime dataCadastro
) {
}
