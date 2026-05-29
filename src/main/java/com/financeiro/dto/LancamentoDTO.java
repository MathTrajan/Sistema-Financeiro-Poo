package com.financeiro.dto;

import com.financeiro.model.FormaPagamento;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class LancamentoDTO {

    private Long           id;
    private LocalDate      dataMovimento;
    private Long           tipoMovimentoId;
    private String         tipoMovimentoNome;
    private String         descricao;
    private BigDecimal     valor;
    private FormaPagamento formaPagamento;
    private String         banco;
    private Integer        quantidadeParcelas;
    private LocalDate      dataVencimento;
    private Boolean        debitado;
    private boolean        vencido;
}
