package io.github.carlossmariano.comunex.bairro.dto.response;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public record BairroResponse(
    long idBairro,
    String nome,
    String municipio,
    String estado
) {
}
