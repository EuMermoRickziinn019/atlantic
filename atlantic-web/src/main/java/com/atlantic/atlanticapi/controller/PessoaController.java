package com.atlantic.atlanticapi.controller;

import com.atlantic.ISBServices.Facade.SessionFacadeADM;
import com.atlantic.ISBServices.ISBServicesAdm;
import com.atlantic.atlanticapi.core.mapper.PessoaMapper;
import com.atlantic.models.adm.Pessoa;
import com.atlantic.models.dto.adm.Pessoa.PessoaRequestDTO;
import com.atlantic.models.dto.adm.Pessoa.PessoaResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pessoa")
public class PessoaController {
    private final ISBServicesAdm negociosADM;
    public PessoaController(SessionFacadeADM facadeADM) {
        this.negociosADM = facadeADM;
    }

    @PostMapping("/criarPessoa")
    @PreAuthorize("hasAuthority('CRIAR')")
    public ResponseEntity<PessoaResponseDTO> criar(@RequestBody PessoaRequestDTO requestDTO) {
        Pessoa pessoa = PessoaMapper.toEntity(requestDTO);
        Pessoa respDB = negociosADM.criarPessoa(pessoa);
        return ResponseEntity.status(HttpStatus.OK).body(PessoaMapper.toDto(respDB));
    }

    @GetMapping("/getTodasPessoas")
    @PreAuthorize("hasAuthority('VISUALIZAR')")
    public ResponseEntity<List<PessoaResponseDTO>> todasPessoas() {
        List<PessoaResponseDTO> list = new ArrayList<>();
        List<Pessoa> pessoas = negociosADM.getTodasPessoas();
        for(Pessoa pessoa : pessoas) {
            list.add(PessoaMapper.toDto(pessoa));
        }
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VISUALIZAR')")
    public ResponseEntity<PessoaResponseDTO> consultarPessoa(@PathVariable Integer id) {
        Pessoa pessoa = negociosADM.consultarPessoa(id);
        if(pessoa != null) {
            return ResponseEntity.status(HttpStatus.OK).body(PessoaMapper.toDto(pessoa));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('EDITAR')")
    public ResponseEntity<PessoaResponseDTO> atualizarPessoa(@PathVariable Integer id, @RequestBody PessoaRequestDTO requestDTO) {
        Pessoa pessoa = PessoaMapper.toEntity(requestDTO);
        pessoa.setIdpessoa(id);
        Pessoa regisAtual = negociosADM.atualizarPessoa(pessoa);
        if(regisAtual != null) {
            return ResponseEntity.status(HttpStatus.OK).body(PessoaMapper.toDto(regisAtual));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('EXCLUIR')")
    public ResponseEntity<Void> deletarPessoa(@PathVariable Integer id) {
        if(negociosADM.removerPessoa(id)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
