package com.atlantic.atlanticapi.controller;
import com.atlantic.ISBServices.Facade.SessionFacadeADM;
import com.atlantic.models.adm.Pessoa;
import com.atlantic.models.dto.adm.Pessoa.PessoaRequestDTO;
import org.junit.jupiter.api.*; import org.junit.jupiter.api.extension.ExtendWith; import org.mockito.*; import org.mockito.junit.jupiter.MockitoExtension; import org.springframework.http.HttpStatus; import java.time.LocalDate; import java.util.List;
import static org.junit.jupiter.api.Assertions.*; import static org.mockito.ArgumentMatchers.any; import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class) class PessoaControllerTest {
 @Mock SessionFacadeADM facade; PessoaController c; @BeforeEach void setup(){c=new PessoaController(facade);}
 @Test void cria(){when(facade.criarPessoa(any())).thenReturn(p(1,"Maria"));var r=c.criar(d("Maria"));assertEquals(HttpStatus.OK,r.getStatusCode());assertEquals(1,r.getBody().idpessoa());}
 @Test void lista(){when(facade.getTodasPessoas()).thenReturn(List.of(p(1,"Maria"),p(2,"Pedro")));assertEquals(2,c.todasPessoas().getBody().size());}
 @Test void consultaInexistente(){when(facade.consultarPessoa(404)).thenReturn(null);assertEquals(HttpStatus.NOT_FOUND,c.consultarPessoa(404).getStatusCode());}
 @Test void atualizaIdDaRota(){when(facade.atualizarPessoa(any())).thenReturn(p(7,"Ana"));assertEquals(HttpStatus.OK,c.atualizarPessoa(7,d("Ana")).getStatusCode());}
 @Test void excluiOu404(){when(facade.removerPessoa(8)).thenReturn(true);when(facade.removerPessoa(9)).thenReturn(false);assertEquals(HttpStatus.OK,c.deletarPessoa(8).getStatusCode());assertEquals(HttpStatus.NOT_FOUND,c.deletarPessoa(9).getStatusCode());}
 com.atlantic.models.dto.adm.Pessoa.PessoaRequestDTO d(String n){return new PessoaRequestDTO(null,n,LocalDate.now(),"12345678901",null,n+"@e.com",null,null,null,null,false);} Pessoa p(int id, String n){Pessoa p=new Pessoa();p.setIdpessoa(id);p.setNome(n);return p;}
}
