package br.com.lczapparolli.database.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import br.com.lczapparolli.database.entity.CartaoCredito;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Testa o mapeamento da entidade CartaoCredito
 */
@QuarkusTest
public class CartaoCreditoRepositoryTest {

    @Inject
    CartaoCreditoRepository cartaoCreditoRepository;

    @Inject
    ContaRepository contaRepository;

    /**
     * Verifica se o mapeamento da entidade permite a inclusão de novos registros
     */
    @Test
    @Transactional
    void incluir_sucesso_test() {
        // Prepara os dados iniciais
        var quantidadeInicial = cartaoCreditoRepository.count();
        var quantidadeInicialConta = contaRepository.count();
        var cartaoCredito = CartaoCredito.cartaoCreditoBuilder()
            .descricao("Cartão Crédito teste")
            .diaVencimento(10)
            .diaFechamento(31)
            .ativo(true)
            .build();
        
        // Verifica se os campos não estão preenchidos
        assertNull(cartaoCredito.getDataCriacao());
        assertNull(cartaoCredito.getVersao());
        
        // Salva o novo cartão
        cartaoCreditoRepository.persistAndFlush(cartaoCredito);

        // Verifica se o registro foi incluído
        var quantidade = cartaoCreditoRepository.count();
        var quantidadeConta = contaRepository.count();
        assertEquals(quantidadeInicial + 1, quantidade);
        // Verifica se incluiu uma nova Conta
        assertEquals(quantidadeInicialConta + 1, quantidadeConta);
        assertNotNull(cartaoCredito.getDataCriacao());
        assertNotNull(cartaoCredito.getVersao());
    }

    /**
     * Verifica se o mapeamento da entidade permite a atualização de registros
     */
    @Test
    @Transactional
    void atualizar_sucesso_test() {
        // Prepara os dados iniciais
        var cartaoCredito = CartaoCredito.cartaoCreditoBuilder()
            .descricao("Cartão Crédito Atualização")
            .diaVencimento(10)
            .diaFechamento(31)
            .ativo(true)
            .build();
        cartaoCreditoRepository.persistAndFlush(cartaoCredito);
        var criacaoAnterior = cartaoCredito.getDataCriacao();
        var versaoAnterior = cartaoCredito.getVersao();

        // Procura o cartão inserido e atualiza as informações
        CartaoCredito cartaoCreditoInserido = cartaoCreditoRepository.findById(cartaoCredito.getId());
        cartaoCreditoInserido.setDiaVencimento(11);
        cartaoCreditoRepository.persistAndFlush(cartaoCreditoInserido);

        // Verifica se os campos foram atualizados
        assertTrue(criacaoAnterior.isEqual(cartaoCreditoInserido.getDataCriacao()));
        assertTrue(versaoAnterior.isBefore(cartaoCreditoInserido.getVersao()));
    }

}
