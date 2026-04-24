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
 * Testa o mapeamento da entidade Fatura
 */
@QuarkusTest
public class FaturaTest {

    private static Long idCartao;
    private static Long idPeriodo;
    private static Long idContaPagar;

    @BeforeEach
    @Transactional
    void prepararDados() {
        if (idCartao == null) {
            var cartao = new CartaoCredito();
            cartao.descricao = "Teste fatura";
            cartao.diaVencimento = 1;
            cartao.diaFechamento = 10;
            cartao.persistAndFlush();
            idCartao = cartao.id;
        }

        if (idPeriodo == null) {
            var periodo = new Periodo();
            periodo.dataInicio = LocalDate.now();
            periodo.dataFim = LocalDate.now();
            periodo.persistAndFlush();
            idPeriodo = periodo.id;
        }

        if (idContaPagar == null) {
            var categoria = new Categoria();
            categoria.descricao = "Teste Fatura";
            categoria.persistAndFlush();

            var contaPagar = new ContaPagar();
            contaPagar.periodo = Periodo.findById(idPeriodo);
            contaPagar.categoria = categoria;
            contaPagar.vencimento = LocalDate.now();
            contaPagar.valor = BigDecimal.ONE;
            contaPagar.persistAndFlush();
            idContaPagar = contaPagar.id;
        }
    }

    /**
     * Verifica se o mapeamento da entidade permite a inclusão de novos registros
     */
    @Test
    @Transactional
    @DisplayName("Entidade Fatura - Inclusão")
    void incluirFaturaTest() {
        // Prepara os dados iniciais
        var quantidadeInicial = Fatura.count();
        var fatura = new Fatura();
        fatura.cartaoCredito = CartaoCredito.findById(idCartao);
        fatura.periodo = Periodo.findById(idPeriodo);
        fatura.contaPagar = ContaPagar.findById(idContaPagar);
        
        // Verifica se os campos não estão preenchidos
        assertNull(fatura.dataCriacao);
        assertNull(fatura.versao);
        
        // Salva a nova fatura
        fatura.persistAndFlush();

        //Verifica se o registro foi incluído
        var quantidade = Fatura.count();
        assertEquals(quantidadeInicial + 1, quantidade);
        assertNotNull(fatura.dataCriacao);
        assertNotNull(fatura.versao);
        assertTrue(fatura.ativo);
    }

    /**
     * Verifica se o mapeamento da entidade permite a atualização de registros
     */
    @Test
    @Transactional
    @DisplayName("Entidade Fatura - Alteração")
    void atualizarFaturaTest() {
        // Prepara os dados iniciais
        var fatura = new Fatura();
        fatura.cartaoCredito = CartaoCredito.findById(idCartao);
        fatura.periodo = Periodo.findById(idPeriodo);
        fatura.contaPagar = ContaPagar.findById(idContaPagar);
        fatura.persistAndFlush();
        var criacaoAnterior = fatura.dataCriacao;
        var versaoAnterior = fatura.versao;

        // Procura a previsão inserida e atualiza as informações
        Fatura faturaInserida = Fatura.findById(fatura.id);
        faturaInserida.ativo = false;
        faturaInserida.persistAndFlush();

        // Verifica se os campos foram atualizados
        assertTrue(criacaoAnterior.isEqual(faturaInserida.dataCriacao));
        assertTrue(versaoAnterior.isBefore(faturaInserida.versao));
    }

}
