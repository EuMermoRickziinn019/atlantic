package com.atlantic.atlanticapi.core.mapper;
import com.atlantic.models.adm.Pessoa;
import com.atlantic.models.dto.adm.Pessoa.PessoaRequestDTO;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
class PessoaMapperTest {
 @Test void deveMapearRequest(){var d=new com.atlantic.models.dto.adm.Pessoa.PessoaRequestDTO(99,"Ana",LocalDate.of(1990,5,12),"12345678901",null,"ana@example.com",null,"11999998888",null,null,false); Pessoa p=PessoaMapper.toEntity(d); assertEquals("Ana",p.getNome()); assertEquals("11999998888",p.getTelefoneModel()); assertNull(p.getIdpessoa());}
 @Test void deveMapearResposta(){Pessoa p=new Pessoa();p.setIdpessoa(10);p.setNome("João");p.setCpf("98765432100");var r=PessoaMapper.toDto(p);assertEquals(10,r.idpessoa());assertEquals("João",r.nome());assertEquals("98765432100",r.cpf());}
}
