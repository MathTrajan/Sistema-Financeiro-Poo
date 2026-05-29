package com.financeiro.controller;

import com.financeiro.dto.LancamentoDTO;
import com.financeiro.model.FormaPagamento;
import com.financeiro.model.ResumoFinanceiro;
import com.financeiro.service.LancamentoService;
import com.financeiro.service.TipoMovimentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LancamentoController {

    private final LancamentoService service;
    private final TipoMovimentoService tipoService;

    @GetMapping("/lancamentos")
    public ResponseEntity<ArrayList<LancamentoDTO>> listar(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim) {
        if (dataInicio != null && dataFim != null) {
            return ResponseEntity.ok(service.buscarPorPeriodo(
                    LocalDate.parse(dataInicio), LocalDate.parse(dataFim)));
        }
        return ResponseEntity.ok(service.listarTodos());
    }

    @PostMapping("/lancamentos")
    public ResponseEntity<?> criar(@RequestBody LancamentoDTO dto) {
        try {
            dto.setId(null);
            return ResponseEntity.ok(service.salvar(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @DeleteMapping("/lancamentos/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/lancamentos/{id}/debitado")
    public ResponseEntity<Void> marcarDebitado(@PathVariable Long id) {
        service.marcarComoDebitado(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/formas-pagamento")
    public ResponseEntity<List<Map<String, Object>>> formasPagamento() {
        return ResponseEntity.ok(Arrays.stream(FormaPagamento.values())
                .map(fp -> Map.<String, Object>of(
                        "codigo", fp.name(),
                        "descricao", fp.getDescricao(),
                        "requererBanco", fp.isRequererBanco()))
                .toList());
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ResumoFinanceiro> dashboard() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicio = hoje.withDayOfMonth(1);
        LocalDate fim = hoje.withDayOfMonth(hoje.lengthOfMonth());
        return ResponseEntity.ok(service.calcularResumo(inicio, fim));
    }

    @GetMapping("/relatorio")
    public ResponseEntity<?> relatorio(
            @RequestParam String dataInicio,
            @RequestParam String dataFim) {
        LocalDate inicio = LocalDate.parse(dataInicio);
        LocalDate fim = LocalDate.parse(dataFim);

        ArrayList<LancamentoDTO> lancamentos = service.buscarPorPeriodo(inicio, fim);
        ResumoFinanceiro resumo = service.calcularResumo(inicio, fim);

        Map<String, Object> resp = new HashMap<>();
        resp.put("lancamentos", lancamentos);
        resp.put("resumo", resumo);
        return ResponseEntity.ok(resp);
    }
}
