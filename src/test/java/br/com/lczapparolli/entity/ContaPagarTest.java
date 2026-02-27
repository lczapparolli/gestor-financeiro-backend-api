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
 * Testa o mapeamento da entidade ContaPagar
 */
@QuarkusTest
public class ContaPagarTest {

    private static Long idPeriodo;
    private static Long idCategoria;

    @BeforeEach
    @Transactional
    void prepararDados() {
        if (idPeriodo == null) {
            var periodo = new Periodo();
            periodo.dataInicio = LocalDate.now();
            periodo.dataFim = LocalDate.now();
            periodo.persistAndFlush();
            idPeriodo = periodo.id;
        }

        if (idCategoria == null) {
            var categoria = new Categoria();
            categoria.descricao = "Teste Conta a Pagar";
            categoria.persistAndFlush();
            idCategoria = categoria.id;
        }
    }

    /**
     * Verifica se o mapeamento da entidade permite a inclusão de novos registros
     */
    @Test
    @Transactional
    @DisplayName("Entidade ContaPagar - Inclusão")
    void incluirContaPagarTest() {
        // Prepara os dados iniciais
        var quantidadeInicial = ContaPagar.count();
        var contaPagar = new ContaPagar();
        contaPagar.periodo = Periodo.findById(idPeriodo);
        contaPagar.categoria = Categoria.findById(idCategoria);
        contaPagar.vencimento = LocalDate.now();
        contaPagar.valor = BigDecimal.ONE;
        
        // Verifica se os campos não estão preenchidos
        assertNull(contaPagar.dataCriacao);
        assertNull(contaPagar.versao);
        
        // Salva a nova conta a pagar
        contaPagar.persistAndFlush();

        //Verifica se o registro foi incluído
        var quantidade = ContaPagar.count();
        assertEquals(quantidadeInicial + 1, quantidade);
        assertNotNull(contaPagar.dataCriacao);
        assertNotNull(contaPagar.versao);
        assertTrue(contaPagar.ativo);
    }

    /**
     * Verifica se o mapeamento da entidade permite a atualização de registros
     */
    @Test
    @Transactional
    @DisplayName("Entidade ContaPagar - Alteração")
    void atualizarContaPagarTest() {
        // Prepara os dados iniciais
        var contaPagar = new ContaPagar();
        contaPagar.periodo = Periodo.findById(idPeriodo);
        contaPagar.categoria = Categoria.findById(idCategoria);
        contaPagar.vencimento = LocalDate.now();
        contaPagar.valor = BigDecimal.ONE;
        contaPagar.persistAndFlush();
        var criacaoAnterior = contaPagar.dataCriacao;
        var versaoAnterior = contaPagar.versao;

        // Procura a conta a pagar inserida e atualiza as informações
        ContaPagar contaPagarInserida = ContaPagar.findById(contaPagar.id);
        contaPagarInserida.valor = BigDecimal.TEN;
        contaPagarInserida.persistAndFlush();

        // Verifica se os campos foram atualizados
        assertTrue(criacaoAnterior.isEqual(contaPagarInserida.dataCriacao));
        assertTrue(versaoAnterior.isBefore(contaPagarInserida.versao));
    }

}