package com.financeiro.repository;

import com.financeiro.model.TipoMovimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipoMovimentoRepository extends JpaRepository<TipoMovimento, Long> {

    List<TipoMovimento> findAllByOrderByNomeAsc();

    boolean existsByNomeIgnoreCase(String nome);
}
