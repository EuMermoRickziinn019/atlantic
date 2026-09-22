package com.atlantic.atlanticapi.models.dto.Pessoa;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PessoaResponseDTO(
    Integer idpessoa,
    String nome,
    LocalDate dataNascimento,
    String cpf,
    String cnpj,
    String email,
    String telefoneFixo,
    String telefoneMovel,
    LocalDateTime dataCadastro,
    LocalDateTime dataAlteracao,
    boolean inativo
) {
}
