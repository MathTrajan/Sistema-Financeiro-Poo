package com.financeiro.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum FormaPagamento {

    DINHEIRO("Dinheiro", false),
    CARTAO  ("Cartão",   true),
    PIX     ("PIX",      true);

    private final String  descricao;
    private final boolean requererBanco;

    FormaPagamento(String descricao, boolean requererBanco) {
        this.descricao    = descricao;
        this.requererBanco = requererBanco;
    }

    public String  getDescricao()    { return descricao;    }
    public boolean isRequererBanco() { return requererBanco; }

    public static List<FormaPagamento> getFormasComBanco() {
        return Arrays.stream(values())
                .filter(FormaPagamento::isRequererBanco)
                .collect(Collectors.toList());
    }
}
