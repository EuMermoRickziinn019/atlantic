package com.atlantic.atlanticapi.core.mapper;

import com.atlantic.models.adm.Pessoa;
import com.atlantic.models.dto.adm.Pessoa.PessoaRequestDTO;
import com.atlantic.models.dto.adm.Pessoa.PessoaResponseDTO;

public class PessoaMapper {
    public static Pessoa toEntity(PessoaRequestDTO dto) {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(dto.nome());
        pessoa.setDataNascimento(dto.dataNascimento());
        pessoa.setCpf(dto.cpf());
        pessoa.setCnpj(dto.cnpj());
        pessoa.setEmail(dto.email());
        pessoa.setTelefoneFixo(dto.telefoneFixo());
        pessoa.setTelefoneModel(dto.telefoneModel());
        pessoa.setInativo(dto.inativo());
        return pessoa;
    }

    public static PessoaResponseDTO toDto(Pessoa pessoa) {
        return new PessoaResponseDTO(
            pessoa.getIdpessoa(),
            pessoa.getNome(),
            pessoa.getDataNascimento(),
            pessoa.getCpf(),
            pessoa.getCnpj(),
            pessoa.getEmail(),
            pessoa.getTelefoneFixo(),
            pessoa.getTelefoneModel(),
            pessoa.getDataCadastro(),
            pessoa.getDataAlteracao(),
            pessoa.isInativo()
        );
    }
}
