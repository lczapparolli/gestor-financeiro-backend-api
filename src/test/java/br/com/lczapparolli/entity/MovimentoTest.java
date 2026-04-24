package br.com.lczapparolli.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;

/**
 * Testa o mapeamento da entidade Movimento
 */
@QuarkusTest
public class MovimentoTest {

    private static Long idCategoria;
    private static Long idConta;
    private static Long idPeriodo;

    @BeforeEach
    @Transactional
    void prepararDados() {
        if (idCategoria == null) {
            var categoria = new Categoria();
            categoria.descricao  = "Teste movimento";
            categoria.persistAndFlush();
            idCategoria = categoria.id;
        }

        if (idConta == null) {
            var conta = new Conta();
            conta.descricao = "Teste movimento";
            conta.persistAndFlush();
            idConta = conta.id;
        }

        if (idPeriodo == null) {
            var periodo = new Periodo();
            periodo.dataInicio = LocalDate.now();
            periodo.dataFim = LocalDate.now();
            periodo.persistAndFlush();
            idPeriodo = periodo.id;
        }
    }

    /**
     * Verifica se o mapeamento da entidade permite a inclusão de novos registros
     */
    @Test
    @Transactional
    @DisplayName("Entidade Movimento - Inclusão")
    void incluirMovimentoTest() {
        // Prepara os dados iniciais
        var quantidadeInicial = Movimento.count();
        var movimento = new Movimento();
        movimento.categoria = Categoria.findById(idCategoria);
        movimento.conta = Conta.findById(idConta);
        movimento.periodo = Periodo.findById(idPeriodo);
        movimento.data = LocalDate.now();
        movimento.descricao = "Teste inclusão";
        movimento.valor = BigDecimal.ONE;

        // Verifica se os campos não estão preenchidos
        assertNull(movimento.dataCriacao);
        assertNull(movimento.versao);

        // Salva o novo movimento
        movimento.persistAndFlush();

        // Verifica se o registros foi incluído
        var quantidade = Movimento.count();
        assertEquals(quantidadeInicial + 1, quantidade);
        assertNotNull(movimento.dataCriacao);
        assertNotNull(movimento.versao);
        assertTrue(movimento.ativo);
    }

    /**
     * Verifica se o mapeamento da entidade permite a atualização de registros
     */
    @Test
    @Transactional
    @DisplayName("Entidade Movimento - Alteração")
    void atualizacaoMovimentoTest() {
        // Prepara os dados iniciais
        var movimento = new Movimento();
        movimento.categoria = Categoria.findById(idCategoria);
        movimento.conta = Conta.findById(idConta);
        movimento.periodo = Periodo.findById(idPeriodo);
        movimento.data = LocalDate.now();
        movimento.descricao = "Teste atualização";
        movimento.valor = BigDecimal.ONE;
        movimento.persistAndFlush();
        var criacaoAnterior = movimento.dataCriacao;
        var versaoAnterior = movimento.versao;

        // Procura o movimento inserido e atualiza as informações
        Movimento movimentoInserido = Movimento.findById(movimento.id);
        movimentoInserido.valor = BigDecimal.TEN;
        movimentoInserido.persistAndFlush();

        // Verifica se os campos foram atualizados
        assertTrue(criacaoAnterior.isEqual(movimentoInserido.dataCriacao));
        assertTrue(versaoAnterior.isBefore(movimentoInserido.versao));
    }

}