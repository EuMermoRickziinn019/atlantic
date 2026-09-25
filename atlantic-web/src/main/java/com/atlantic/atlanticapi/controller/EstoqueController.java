package com.atlantic.atlanticapi.controller;

import com.atlantic.ISBServices.Facade.SessionFacadeADM;
import com.atlantic.ISBServices.ISBServicesAdm;
import com.atlantic.ISBServices.ISBServicesCmc;
import com.atlantic.atlanticapi.core.mapper.EstoqueMapper;
import com.atlantic.atlanticapi.core.mapper.PessoaMapper;
import com.atlantic.models.adm.Pessoa;
import com.atlantic.models.cmc.Estoque;
import com.atlantic.models.dto.adm.Pessoa.PessoaResponseDTO;
import com.atlantic.models.dto.cmc.EstoqueResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/estoque")
public class EstoqueController {
    private final ISBServicesCmc negociosCmc;
    public EstoqueController(ISBServicesCmc negociosCmc) {
        this.negociosCmc = negociosCmc;
    }

    @GetMapping("/getTodasEstoque")
    @PreAuthorize("hasAuthority('VISUALIZAR')")
    public ResponseEntity<List<EstoqueResponseDTO>> todasEstoque() {
        List<EstoqueResponseDTO> list = new ArrayList<>();
        List<Estoque> estoque = negociosCmc.getTodosEstoque();
        for(Estoque e : estoque) {
            list.add(EstoqueMapper.toDto(e));
        }
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }
}
