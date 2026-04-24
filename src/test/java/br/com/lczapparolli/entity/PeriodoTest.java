package br.com.lczapparolli.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;

/**
 * Testa o mapeamento da entidade Periodo
 */
@QuarkusTest
public class PeriodoTest {

    /**
     * Verifica se o mapeamento da entidade permite a inclusão de novos registros
     */
    @Test
    @Transactional
    @DisplayName("Entidade Período - Inclusão")
    void incluirPeriodoTest() {
        // Prepara os dados iniciais
        var quantidadeInicial = Periodo.count();
        var periodo = new Periodo();
        periodo.dataInicio = LocalDate.now();
        periodo.dataFim = LocalDate.now();

        // Verifica se os campos não estão preenchidos
        assertNull(periodo.dataCriacao);
        assertNull(periodo.versao);

        // Salva o novo período
        periodo.persistAndFlush();

        // Verifica se o registro foi incluído
        var quantidade = Periodo.count();
        assertEquals(quantidadeInicial + 1, quantidade);
        assertNotNull(periodo.dataCriacao);
        assertNotNull(periodo.versao);
        assertTrue(periodo.ativo);
    }

    /**
     * Verifica se o mapeamento da entidade permite a atualização de registros
     */
    @Test
    @Transactional
    @DisplayName("Entidade Período - Alteração")
    void atualizarPeriodoTest() {
        // Prepara os dados iniciais
        var periodo = new Periodo();
        periodo.dataInicio = LocalDate.now();
        periodo.dataFim = LocalDate.now();
        periodo.persistAndFlush();
        var criacaoAnterior = periodo.dataCriacao;
        var versaoAnterior = periodo.versao;

        // Procura a conta inserida e atualiza as informações
        Periodo periodoInserido = Periodo.findById(periodo.id);
        periodoInserido.dataInicio = LocalDate.now().plusDays(1);
        periodoInserido.dataFim = LocalDate.now().plusDays(1);
        periodoInserido.persistAndFlush();

        // Verifica se os campos de atualização
        assertTrue(criacaoAnterior.isEqual(periodoInserido.dataCriacao));
        assertTrue(versaoAnterior.isBefore(periodoInserido.versao));
    }

}
