package com.financeiro.repository;

import com.financeiro.model.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    @Query("SELECT l FROM Lancamento l JOIN FETCH l.tipoMovimento " +
           "ORDER BY l.dataMovimento DESC")
    List<Lancamento> findAllComTipo();

    @Query("SELECT l FROM Lancamento l JOIN FETCH l.tipoMovimento " +
           "WHERE l.dataMovimento BETWEEN :inicio AND :fim " +
           "ORDER BY l.dataMovimento DESC")
    List<Lancamento> findByPeriodo(
            @Param("inicio") LocalDate inicio,
            @Param("fim")    LocalDate fim);

    @Modifying
    @Transactional
    @Query("UPDATE Lancamento l SET l.debitado = true WHERE l.id = :id")
    int marcarComoDebitado(@Param("id") Long id);
}
