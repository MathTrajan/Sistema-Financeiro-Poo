package com.financeiro;

import com.financeiro.model.FormaPagamento;
import com.financeiro.model.ResumoFinanceiro;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class FinanceiroApplicationTests {

    @Test
    void enum_formasPagamentoExistem() {
        assertNotNull(FormaPagamento.DINHEIRO);
        assertNotNull(FormaPagamento.CARTAO);
        assertNotNull(FormaPagamento.PIX);
    }

    @Test
    void enum_formasQueExigemBanco() {
        assertTrue(FormaPagamento.CARTAO.isRequererBanco());
        assertTrue(FormaPagamento.PIX.isRequererBanco());
        assertFalse(FormaPagamento.DINHEIRO.isRequererBanco());
    }

    @Test
    void enum_descricoes() {
        assertEquals("Cartão",   FormaPagamento.CARTAO.getDescricao());
        assertEquals("PIX",      FormaPagamento.PIX.getDescricao());
        assertEquals("Dinheiro", FormaPagamento.DINHEIRO.getDescricao());
    }

    @Test
    void arrayList_adicionarEContar() {
        ArrayList<String> categorias = new ArrayList<>();
        categorias.add("Salário");
        categorias.add("Energia Elétrica");
        categorias.add("Plano de Saúde");

        assertEquals(3, categorias.size());
        assertTrue(categorias.contains("Salário"));
    }

    @Test
    void arrayList_removerItem() {
        ArrayList<String> lista = new ArrayList<>(Arrays.asList("A", "B", "C"));
        lista.remove("B");

        assertEquals(2, lista.size());
        assertFalse(lista.contains("B"));
    }

    @Test
    void lambda_filtrarFormasComBanco() {
        List<FormaPagamento> comBanco = FormaPagamento.getFormasComBanco();

        assertFalse(comBanco.isEmpty());
        assertTrue(comBanco.stream().allMatch(FormaPagamento::isRequererBanco));
        assertEquals(2, comBanco.size());
    }

    @Test
    void lambda_filtrarDespesas() {
        ArrayList<String> categorias = new ArrayList<>(
                Arrays.asList("Energia Elétrica", "Salário", "Plano de Saúde", "Freelance"));

        ArrayList<String> despesas = categorias.stream()
                .filter(c -> !c.equals("Salário") && !c.equals("Freelance"))
                .collect(Collectors.toCollection(ArrayList::new));

        assertEquals(2, despesas.size());
        assertTrue(despesas.contains("Energia Elétrica"));
        assertFalse(despesas.contains("Salário"));
    }

    @Test
    void lambda_somarValoresComReduce() {
        ArrayList<BigDecimal> valores = new ArrayList<>(Arrays.asList(
                new BigDecimal("100.00"),
                new BigDecimal("250.50"),
                new BigDecimal("75.25")));

        BigDecimal total = valores.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertEquals(new BigDecimal("425.75"), total);
    }

    @Test
    void valueObject_criacao() {
        ResumoFinanceiro resumo = new ResumoFinanceiro(
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 30),
                new BigDecimal("5000.00"),
                new BigDecimal("1500.00"),
                10L, 7L, 1L);

        assertEquals(new BigDecimal("5000.00"), resumo.getTotalReceitas());
        assertEquals(new BigDecimal("1500.00"), resumo.getTotalDespesas());
    }

    @Test
    void valueObject_calculaSaldo() {
        ResumoFinanceiro resumo = new ResumoFinanceiro(
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 30),
                new BigDecimal("5000.00"),
                new BigDecimal("1500.00"),
                10L, 7L, 0L);

        assertEquals(new BigDecimal("3500.00"), resumo.getSaldo());
    }

    @Test
    void valueObject_calculaLancamentosPendentes() {
        ResumoFinanceiro resumo = new ResumoFinanceiro(
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 30),
                new BigDecimal("5000.00"),
                new BigDecimal("1500.00"),
                10L, 7L, 0L);

        assertEquals(3L, resumo.getLancamentosPendentes());
    }

    @Test
    void valueObject_saldoNegativo() {
        ResumoFinanceiro resumo = new ResumoFinanceiro(
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 30),
                new BigDecimal("1000.00"),
                new BigDecimal("3000.00"),
                5L, 3L, 1L);

        assertEquals(new BigDecimal("-2000.00"), resumo.getSaldo());
    }

    @Test
    void valueObject_igualdadePorValor() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fim    = LocalDate.of(2026, 1, 31);

        ResumoFinanceiro r1 = new ResumoFinanceiro(inicio, fim,
                BigDecimal.TEN, BigDecimal.TEN, 2L, 1L, 0L);
        ResumoFinanceiro r2 = new ResumoFinanceiro(inicio, fim,
                BigDecimal.TEN, BigDecimal.TEN, 2L, 1L, 0L);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

}
