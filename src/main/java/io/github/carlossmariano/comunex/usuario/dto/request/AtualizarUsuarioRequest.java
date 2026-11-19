package io.github.carlossmariano.comunex.usuario.dto.request;

import jakarta.validation.constraints.Size;

public record AtualizarUsuarioRequest(
        @Size(max = 120)
        String nome,

        @Size(max = 150)
        String descricao
) {
}
