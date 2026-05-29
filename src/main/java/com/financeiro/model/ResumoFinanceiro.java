package com.financeiro.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public final class ResumoFinanceiro {

    private final LocalDate  dataInicio;
    private final LocalDate  dataFim;
    private final BigDecimal totalReceitas;
    private final BigDecimal totalDespesas;
    private final long       quantidadeLancamentos;
    private final long       quantidadeDebitados;
    private final long       quantidadeVencidos;

    public ResumoFinanceiro(LocalDate  dataInicio,
                            LocalDate  dataFim,
                            BigDecimal totalReceitas,
                            BigDecimal totalDespesas,
                            long       quantidadeLancamentos,
                            long       quantidadeDebitados,
                            long       quantidadeVencidos) {

        this.dataInicio            = Objects.requireNonNull(dataInicio, "Data início é obrigatória");
        this.dataFim               = Objects.requireNonNull(dataFim,    "Data fim é obrigatória");
        this.totalReceitas         = totalReceitas != null ? totalReceitas : BigDecimal.ZERO;
        this.totalDespesas         = totalDespesas != null ? totalDespesas : BigDecimal.ZERO;
        this.quantidadeLancamentos = quantidadeLancamentos;
        this.quantidadeDebitados   = quantidadeDebitados;
        this.quantidadeVencidos    = quantidadeVencidos;
    }

    public BigDecimal getSaldo() {
        return totalReceitas.subtract(totalDespesas);
    }

    public LocalDate  getDataInicio()            { return dataInicio;            }
    public LocalDate  getDataFim()               { return dataFim;               }
    public BigDecimal getTotalReceitas()         { return totalReceitas;         }
    public BigDecimal getTotalDespesas()         { return totalDespesas;         }
    public long       getQuantidadeLancamentos() { return quantidadeLancamentos; }
    public long       getQuantidadeDebitados()   { return quantidadeDebitados;   }
    public long       getQuantidadeVencidos()    { return quantidadeVencidos;    }
    public long       getLancamentosPendentes()  { return Math.max(0, quantidadeLancamentos - quantidadeDebitados); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResumoFinanceiro that)) return false;
        return quantidadeLancamentos == that.quantidadeLancamentos
                && quantidadeDebitados   == that.quantidadeDebitados
                && quantidadeVencidos    == that.quantidadeVencidos
                && Objects.equals(dataInicio,    that.dataInicio)
                && Objects.equals(dataFim,       that.dataFim)
                && Objects.equals(totalReceitas, that.totalReceitas)
                && Objects.equals(totalDespesas, that.totalDespesas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataInicio, dataFim, totalReceitas, totalDespesas,
                quantidadeLancamentos, quantidadeDebitados, quantidadeVencidos);
    }

    @Override
    public String toString() {
        return String.format(
                "ResumoFinanceiro{%s até %s | Receitas=R$%.2f | Despesas=R$%.2f | Saldo=R$%.2f}",
                dataInicio, dataFim, totalReceitas, totalDespesas, getSaldo());
    }
}
