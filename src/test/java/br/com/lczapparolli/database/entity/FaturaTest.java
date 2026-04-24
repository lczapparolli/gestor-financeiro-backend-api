package br.com.lczapparolli.database.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.lczapparolli.database.repository.CartaoCreditoRepository;
import br.com.lczapparolli.database.repository.CategoriaRepository;
import br.com.lczapparolli.database.repository.ContaPagarRepository;
import br.com.lczapparolli.database.repository.FaturaRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Testa o mapeamento da entidade Fatura
 */
@QuarkusTest
public class FaturaTest {

    @Inject
    CartaoCreditoRepository cartaoCreditoRepository;
    
    @Inject
    CategoriaRepository categoriaRepository;

    @Inject
    FaturaRepository faturaRepository;

    @Inject
    ContaPagarRepository contaPagarRepository;

    private static Long idCartao;
    private static Long idCategoria;

    @BeforeEach
    @Transactional
    void prepararDados() {
        if (idCartao == null) {
            var cartao = new CartaoCredito();
            cartao.descricao = "Teste fatura";
            cartao.diaVencimento = 1;
            cartao.diaFechamento = 10;
            cartaoCreditoRepository.persistAndFlush(cartao);
            idCartao = cartao.id;
        }

        if (idCategoria == null) {
            var categoria = new Categoria();
            categoria.descricao = "Teste Fatura";
            categoriaRepository.persistAndFlush(categoria);
            idCategoria = categoria.id;
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
        var quantidadeInicial = faturaRepository.count();
        var quantidadeInicialPagar = contaPagarRepository.count();
        var fatura = new Fatura();
        fatura.cartaoCredito = cartaoCreditoRepository.findById(idCartao);
        fatura.categoria = categoriaRepository.findById(idCategoria);
        fatura.periodo = LocalDate.now();
        fatura.vencimento = LocalDate.now();
        fatura.valor = BigDecimal.ONE;
        
        // Verifica se os campos não estão preenchidos
        assertNull(fatura.dataCriacao);
        assertNull(fatura.versao);
        
        // Salva a nova fatura
        faturaRepository.persistAndFlush(fatura);

        //Verifica se o registro foi incluído
        var quantidade = faturaRepository.count();
        var quantidadePagar = contaPagarRepository.count();
        assertEquals(quantidadeInicial + 1, quantidade);
        assertEquals(quantidadeInicialPagar + 1, quantidadePagar);
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
        fatura.cartaoCredito = cartaoCreditoRepository.findById(idCartao);
        fatura.categoria = categoriaRepository.findById(idCategoria);
        fatura.periodo = LocalDate.now();
        fatura.vencimento = LocalDate.now();
        fatura.valor = BigDecimal.ONE;
        faturaRepository.persistAndFlush(fatura);
        var criacaoAnterior = fatura.dataCriacao;
        var versaoAnterior = fatura.versao;

        // Procura a previsão inserida e atualiza as informações
        Fatura faturaInserida = faturaRepository.findById(fatura.id);
        faturaInserida.valor = BigDecimal.TEN;
        faturaRepository.persistAndFlush(faturaInserida);

        // Verifica se os campos foram atualizados
        assertTrue(criacaoAnterior.isEqual(faturaInserida.dataCriacao));
        assertTrue(versaoAnterior.isBefore(faturaInserida.versao));
    }

}
