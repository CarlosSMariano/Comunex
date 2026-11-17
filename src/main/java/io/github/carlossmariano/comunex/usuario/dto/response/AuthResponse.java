package io.github.carlossmariano.comunex.usuario.dto.response;

public record AuthResponse(
        String token,
        String tipo,
        Long idUsuario
) {
}
