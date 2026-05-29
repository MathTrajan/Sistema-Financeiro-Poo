package com.financeiro.service;

import com.financeiro.dto.LancamentoDTO;
import com.financeiro.model.Lancamento;
import com.financeiro.model.ResumoFinanceiro;
import com.financeiro.model.TipoMovimento;
import com.financeiro.repository.LancamentoRepository;
import com.financeiro.repository.TipoMovimentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LancamentoService {

    private final LancamentoRepository lancamentoRepo;
    private final TipoMovimentoRepository tipoRepo;

    private static final List<String> CATEGORIAS_RECEITA = List.of("Salario", "Freelance");

    public ArrayList<LancamentoDTO> listarTodos() {
        ArrayList<LancamentoDTO> resultado = new ArrayList<>();
        lancamentoRepo.findAllComTipo().forEach(l -> resultado.add(toDTO(l)));
        return resultado;
    }

    public ArrayList<LancamentoDTO> buscarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return lancamentoRepo.findByPeriodo(inicio, fim).stream()
                .map(this::toDTO)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Optional<LancamentoDTO> buscarPorId(Long id) {
        return lancamentoRepo.findById(id).map(this::toDTO);
    }

    @Transactional
    public LancamentoDTO salvar(LancamentoDTO dto) {
        normalizarCampos(dto);

        if (dto.getFormaPagamento() != null && dto.getFormaPagamento().isRequererBanco()
                && (dto.getBanco() == null || dto.getBanco().isBlank())) {
            throw new IllegalArgumentException("Informe o banco para: " + dto.getFormaPagamento().getDescricao());
        }
        return toDTO(lancamentoRepo.save(toEntity(dto)));
    }

    @Transactional
    public void excluir(Long id) {
        lancamentoRepo.deleteById(id);
    }

    @Transactional
    public void marcarComoDebitado(Long id) {
        lancamentoRepo.marcarComoDebitado(id);
    }

    public ResumoFinanceiro calcularResumo(LocalDate inicio, LocalDate fim) {
        List<Lancamento> lancamentos = lancamentoRepo.findByPeriodo(inicio, fim);

        BigDecimal totalReceitas = lancamentos.stream()
                .filter(this::isReceita)
                .map(Lancamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDespesas = lancamentos.stream()
                .filter(l -> !isReceita(l))
                .map(Lancamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long debitados = lancamentos.stream()
                .filter(l -> Boolean.TRUE.equals(l.getDebitado()))
                .count();
        long vencidos = lancamentos.stream()
                .filter(Lancamento::isVencido)
                .count();

        return new ResumoFinanceiro(inicio, fim, totalReceitas, totalDespesas,
                lancamentos.size(), debitados, vencidos);
    }

    private boolean isReceita(Lancamento lancamento) {
        String categoria = normalizarTexto(lancamento.getTipoMovimento().getNome());
        return CATEGORIAS_RECEITA.stream().anyMatch(categoria::equalsIgnoreCase);
    }

    private void normalizarCampos(LancamentoDTO dto) {
        if (dto.getDescricao() != null) {
            dto.setDescricao(dto.getDescricao().trim());
        }
        if (dto.getBanco() != null) {
            String banco = dto.getBanco().trim();
            dto.setBanco(banco.isEmpty() ? null : banco);
        }
        if (dto.getQuantidadeParcelas() == null || dto.getQuantidadeParcelas() < 1) {
            dto.setQuantidadeParcelas(1);
        }
        if (dto.getDebitado() == null) {
            dto.setDebitado(false);
        }
    }

    private String normalizarTexto(String valor) {
        if (valor == null) {
            return "";
        }
        return java.text.Normalizer.normalize(valor, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    private LancamentoDTO toDTO(Lancamento l) {
        LancamentoDTO dto = new LancamentoDTO();
        dto.setId(l.getId());
        dto.setDataMovimento(l.getDataMovimento());
        dto.setTipoMovimentoId(l.getTipoMovimento().getId());
        dto.setTipoMovimentoNome(l.getTipoMovimento().getNome());
        dto.setDescricao(l.getDescricao());
        dto.setValor(l.getValor());
        dto.setFormaPagamento(l.getFormaPagamento());
        dto.setBanco(l.getBanco());
        dto.setQuantidadeParcelas(l.getQuantidadeParcelas());
        dto.setDataVencimento(l.getDataVencimento());
        dto.setDebitado(l.getDebitado());
        dto.setVencido(l.isVencido());
        return dto;
    }

    private Lancamento toEntity(LancamentoDTO dto) {
        Lancamento l = (dto.getId() != null)
                ? lancamentoRepo.findById(dto.getId()).orElse(new Lancamento())
                : new Lancamento();

        TipoMovimento tipo = tipoRepo.findById(dto.getTipoMovimentoId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria nao encontrada"));

        l.setDataMovimento(dto.getDataMovimento());
        l.setTipoMovimento(tipo);
        l.setDescricao(dto.getDescricao());
        l.setValor(dto.getValor());
        l.setFormaPagamento(dto.getFormaPagamento());
        l.setBanco(dto.getBanco());
        l.setQuantidadeParcelas(dto.getQuantidadeParcelas() != null ? dto.getQuantidadeParcelas() : 1);
        l.setDataVencimento(dto.getDataVencimento());
        l.setDebitado(dto.getDebitado() != null ? dto.getDebitado() : false);
        return l;
    }
}
