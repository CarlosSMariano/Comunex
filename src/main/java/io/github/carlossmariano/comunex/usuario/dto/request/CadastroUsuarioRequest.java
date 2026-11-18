package io.github.carlossmariano.comunex.usuario.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroUsuarioRequest(

        @NotBlank
        @Size(max = 120)
        String nome,

        @NotBlank
        @Email
        @Size(max = 150)
        String email,

        @NotBlank
        @Size(min = 8, max = 72)
        String senha,

        @Size(max = 500)
        String descricao,

        @NotBlank
        String tipoPerfil,

        Long idBairro

) {
}
