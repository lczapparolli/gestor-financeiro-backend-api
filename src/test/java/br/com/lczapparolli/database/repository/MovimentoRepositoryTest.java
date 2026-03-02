package br.com.lczapparolli.database.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.lczapparolli.database.entity.Categoria;
import br.com.lczapparolli.database.entity.Conta;
import br.com.lczapparolli.database.entity.Movimento;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Testa o mapeamento da entidade Movimento
 */
@QuarkusTest
public class MovimentoRepositoryTest {

    @Inject
    ContaRepository contaRepository;

    @Inject
    CategoriaRepository categoriaRepository;

    @Inject
    MovimentoRepository movimentoRepository;

    private static Long idCategoria;
    private static Long idConta;

    @BeforeEach
    @Transactional
    void prepararDados() {
        if (idCategoria == null) {
            var categoria = new Categoria();
            categoria.descricao  = "Teste movimento";
            categoriaRepository.persistAndFlush(categoria);
            idCategoria = categoria.id;
        }

        if (idConta == null) {
            var conta = new Conta();
            conta.descricao = "Teste movimento";
            contaRepository.persistAndFlush(conta);
            idConta = conta.id;
        }
    }

    /**
     * Verifica se o mapeamento da entidade permite a inclusão de novos registros
     */
    @Test
    @Transactional
    void incluir_sucesso_test() {
        // Prepara os dados iniciais
        var quantidadeInicial = movimentoRepository.count();
        var movimento = new Movimento();
        movimento.categoria = categoriaRepository.findById(idCategoria);
        movimento.conta = contaRepository.findById(idConta);
        movimento.periodo = LocalDate.now();
        movimento.data = LocalDate.now();
        movimento.descricao = "Teste inclusão";
        movimento.valor = BigDecimal.ONE;

        // Verifica se os campos não estão preenchidos
        assertNull(movimento.dataCriacao);
        assertNull(movimento.versao);

        // Salva o novo movimento
        movimentoRepository.persistAndFlush(movimento);

        // Verifica se o registros foi incluído
        var quantidade = movimentoRepository.count();
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
    void atualizacao_sucesso_test() {
        // Prepara os dados iniciais
        var movimento = new Movimento();
        movimento.categoria = categoriaRepository.findById(idCategoria);
        movimento.conta = contaRepository.findById(idConta);
        movimento.periodo = LocalDate.now();
        movimento.data = LocalDate.now();
        movimento.descricao = "Teste atualização";
        movimento.valor = BigDecimal.ONE;
        movimentoRepository.persistAndFlush(movimento);
        var criacaoAnterior = movimento.dataCriacao;
        var versaoAnterior = movimento.versao;

        // Procura o movimento inserido e atualiza as informações
        Movimento movimentoInserido = movimentoRepository.findById(movimento.id);
        movimentoInserido.valor = BigDecimal.TEN;
        movimentoRepository.persistAndFlush(movimentoInserido);

        // Verifica se os campos foram atualizados
        assertTrue(criacaoAnterior.isEqual(movimentoInserido.dataCriacao));
        assertTrue(versaoAnterior.isBefore(movimentoInserido.versao));
    }

}