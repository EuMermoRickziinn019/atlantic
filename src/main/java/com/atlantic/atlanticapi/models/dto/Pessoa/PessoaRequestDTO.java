package com.atlantic.atlanticapi.models.dto.Pessoa;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PessoaRequestDTO(
    Integer idpessoa,
    String nome,
    LocalDate dataNascimento,
    String cpf,
    String cnpj,
    String email,
    String telefoneFixo,
    String telefoneModel,
    LocalDateTime dataCadastro,
    LocalDateTime dataAlteracao,
    boolean inativo
) {
}
