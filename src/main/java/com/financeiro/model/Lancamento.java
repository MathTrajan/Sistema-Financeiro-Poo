package com.financeiro.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lancamentos")
@Data
@NoArgsConstructor

@SQLDelete(sql = "UPDATE lancamentos SET data_exclusao = NOW() WHERE id = ?")

@SQLRestriction("data_exclusao IS NULL")
public class Lancamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dataMovimento;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_movimento_id", nullable = false)
    private TipoMovimento tipoMovimento;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormaPagamento formaPagamento;

    @Column
    private String banco;

    @Column
    private Integer quantidadeParcelas = 1;

    @Column
    private LocalDate dataVencimento;

    @Column
    private Boolean debitado = false;

    @Column(name = "data_exclusao")
    private LocalDateTime dataExclusao;

    public boolean isVencido() {
        return dataVencimento != null
                && dataVencimento.isBefore(LocalDate.now())
                && !Boolean.TRUE.equals(debitado);
    }
}
