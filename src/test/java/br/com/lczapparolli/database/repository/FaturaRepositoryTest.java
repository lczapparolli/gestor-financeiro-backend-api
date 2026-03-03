package br.com.lczapparolli.database.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.lczapparolli.database.entity.CartaoCredito;
import br.com.lczapparolli.database.entity.Categoria;
import br.com.lczapparolli.database.entity.Fatura;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Testa o mapeamento da entidade Fatura
 */
@QuarkusTest
public class FaturaRepositoryTest {

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
            var cartao = CartaoCredito.cartaoCreditoBuilder()
                .descricao("Teste fatura")
                .diaVencimento(1)
                .diaFechamento(10)
                .ativo(true)
                .build();
            cartaoCreditoRepository.persistAndFlush(cartao);
            idCartao = cartao.getId();
        }

        if (idCategoria == null) {
            var categoria = Categoria.builder()
                .descricao("Teste Fatura")
                .ativo(true)
                .build();
            categoriaRepository.persistAndFlush(categoria);
            idCategoria = categoria.getId();
        }
    }

    /**
     * Verifica se o mapeamento da entidade permite a inclusão de novos registros
     */
    @Test
    @Transactional
    void incluir_sucesso_test() {
        // Prepara os dados iniciais
        var quantidadeInicial = faturaRepository.count();
        var quantidadeInicialPagar = contaPagarRepository.count();
        var fatura = Fatura.faturaBuilder()
            .cartaoCredito(cartaoCreditoRepository.findById(idCartao))
            .categoria(categoriaRepository.findById(idCategoria))
            .periodo(LocalDate.now())
            .vencimento(LocalDate.now())
            .valor(BigDecimal.ONE)
            .ativo(true)
            .build();
        
        // Verifica se os campos não estão preenchidos
        assertNull(fatura.getDataCriacao());
        assertNull(fatura.getVersao());
        
        // Salva a nova fatura
        faturaRepository.persistAndFlush(fatura);

        //Verifica se o registro foi incluído
        var quantidade = faturaRepository.count();
        var quantidadePagar = contaPagarRepository.count();
        assertEquals(quantidadeInicial + 1, quantidade);
        assertEquals(quantidadeInicialPagar + 1, quantidadePagar);
        assertNotNull(fatura.getDataCriacao());
        assertNotNull(fatura.getVersao());
    }

    /**
     * Verifica se o mapeamento da entidade permite a atualização de registros
     */
    @Test
    @Transactional
    void atualizar_sucesso_test() {
        // Prepara os dados iniciais
        var fatura = Fatura.faturaBuilder()
            .cartaoCredito(cartaoCreditoRepository.findById(idCartao))
            .categoria(categoriaRepository.findById(idCategoria))
            .periodo(LocalDate.now())
            .vencimento(LocalDate.now())
            .valor(BigDecimal.ONE)
            .ativo(true)
            .build();
        faturaRepository.persistAndFlush(fatura);
        var criacaoAnterior = fatura.getDataCriacao();
        var versaoAnterior = fatura.getVersao();

        // Procura a previsão inserida e atualiza as informações
        Fatura faturaInserida = faturaRepository.findById(fatura.getId());
        faturaInserida.setValor(BigDecimal.TEN);
        faturaRepository.persistAndFlush(faturaInserida);

        // Verifica se os campos foram atualizados
        assertTrue(criacaoAnterior.isEqual(faturaInserida.getDataCriacao()));
        assertTrue(versaoAnterior.isBefore(faturaInserida.getVersao()));
    }

}
