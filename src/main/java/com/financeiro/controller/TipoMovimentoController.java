package com.financeiro.controller;

import com.financeiro.model.TipoMovimento;
import com.financeiro.service.TipoMovimentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-movimento")
@RequiredArgsConstructor
public class TipoMovimentoController {

    private final TipoMovimentoService service;

    @GetMapping
    public ResponseEntity<List<TipoMovimento>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }
}
