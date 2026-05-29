package com.financeiro.service;

import com.financeiro.model.TipoMovimento;
import com.financeiro.repository.TipoMovimentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoMovimentoService {

    private final TipoMovimentoRepository repo;

    public List<TipoMovimento> listarTodos() {
        return repo.findAllByOrderByNomeAsc();
    }
}
