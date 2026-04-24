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
            var categoria = Categoria.builder()
                .descricao("Teste movimento")
                .ativo(true)
                .build();
            categoriaRepository.persistAndFlush(categoria);
            idCategoria = categoria.getId();
        }

        if (idConta == null) {
            var conta = Conta.builder()
                .descricao("Teste movimento")
                .ativo(true)
                .build();
            contaRepository.persistAndFlush(conta);
            idConta = conta.getId();
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
        var movimento = Movimento.builder()
            .categoria(categoriaRepository.findById(idCategoria))
            .conta(contaRepository.findById(idConta))
            .periodo(LocalDate.now())
            .data(LocalDate.now())
            .descricao("Teste inclusão")
            .valor(BigDecimal.ONE)
            .ativo(true)
            .build();

        // Verifica se os campos não estão preenchidos
        assertNull(movimento.getDataCriacao());
        assertNull(movimento.getVersao());

        // Salva o novo movimento
        movimentoRepository.persistAndFlush(movimento);

        // Verifica se o registros foi incluído
        var quantidade = movimentoRepository.count();
        assertEquals(quantidadeInicial + 1, quantidade);
        assertNotNull(movimento.getDataCriacao());
        assertNotNull(movimento.getVersao());
    }

    /**
     * Verifica se o mapeamento da entidade permite a atualização de registros
     */
    @Test
    @Transactional
    void atualizacao_sucesso_test() {
        // Prepara os dados iniciais
        var movimento = Movimento.builder()
            .categoria(categoriaRepository.findById(idCategoria))
            .conta(contaRepository.findById(idConta))
            .periodo(LocalDate.now())
            .data(LocalDate.now())
            .descricao("Teste atualização")
            .valor(BigDecimal.ONE)
            .ativo(true)
            .build();
        movimentoRepository.persistAndFlush(movimento);
        var criacaoAnterior = movimento.getDataCriacao();
        var versaoAnterior = movimento.getVersao();

        // Procura o movimento inserido e atualiza as informações
        Movimento movimentoInserido = movimentoRepository.findById(movimento.getId());
        movimentoInserido.setValor(BigDecimal.TEN);
        movimentoRepository.persistAndFlush(movimentoInserido);

        // Verifica se os campos foram atualizados
        assertTrue(criacaoAnterior.isEqual(movimentoInserido.getDataCriacao()));
        assertTrue(versaoAnterior.isBefore(movimentoInserido.getVersao()));
    }

}