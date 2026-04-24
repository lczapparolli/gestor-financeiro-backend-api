package br.com.lczapparolli.database.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.lczapparolli.database.repository.CartaoCreditoRepository;
import br.com.lczapparolli.database.repository.ContaRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Testa o mapeamento da entidade CartaoCredito
 */
@QuarkusTest
public class CartaoCreditoTest {

    @Inject
    CartaoCreditoRepository cartaoCreditoRepository;

    @Inject
    ContaRepository contaRepository;

    /**
     * Verifica se o mapeamento da entidade permite a inclusão de novos registros
     */
    @Test
    @Transactional
    @DisplayName("Entidade Cartão Crédito - Inclusão")
    void incluirCartaoCreditoTest() {
        // Prepara os dados iniciais
        var quantidadeInicial = cartaoCreditoRepository.count();
        var quantidadeInicialConta = contaRepository.count();
        var cartaoCredito = new CartaoCredito();
        cartaoCredito.descricao = "Cartão Crédito teste";
        cartaoCredito.diaVencimento = 10;
        cartaoCredito.diaFechamento = 31;
        
        // Verifica se os campos não estão preenchidos
        assertNull(cartaoCredito.dataCriacao);
        assertNull(cartaoCredito.versao);
        
        // Salva o novo cartão
        cartaoCreditoRepository.persistAndFlush(cartaoCredito);

        // Verifica se o registro foi incluído
        var quantidade = cartaoCreditoRepository.count();
        var quantidadeConta = contaRepository.count();
        assertEquals(quantidadeInicial + 1, quantidade);
        // Verifica se incluiu uma nova Conta
        assertEquals(quantidadeInicialConta + 1, quantidadeConta);
        assertNotNull(cartaoCredito.dataCriacao);
        assertNotNull(cartaoCredito.versao);
        assertTrue(cartaoCredito.ativo);
    }

    /**
     * Verifica se o mapeamento da entidade permite a atualização de registros
     */
    @Test
    @Transactional
    @DisplayName("Entidade Cartão Crédito - Alteração")
    void atualizarCartaoCreditoTest() {
        // Prepara os dados iniciais
        var cartaoCredito = new CartaoCredito();
        cartaoCredito.descricao = "Cartão Crédito Atualização";
        cartaoCredito.diaVencimento = 10;
        cartaoCredito.diaFechamento = 31;
        cartaoCreditoRepository.persistAndFlush(cartaoCredito);
        var criacaoAnterior = cartaoCredito.dataCriacao;
        var versaoAnterior = cartaoCredito.versao;

        // Procura o cartão inserido e atualiza as informações
        CartaoCredito cartaoCreditoInserido = cartaoCreditoRepository.findById(cartaoCredito.id);
        cartaoCreditoInserido.diaVencimento = 11;
        cartaoCreditoRepository.persistAndFlush(cartaoCreditoInserido);

        // Verifica se os campos foram atualizados
        assertTrue(criacaoAnterior.isEqual(cartaoCreditoInserido.dataCriacao));
        assertTrue(versaoAnterior.isBefore(cartaoCreditoInserido.versao));
    }

}
