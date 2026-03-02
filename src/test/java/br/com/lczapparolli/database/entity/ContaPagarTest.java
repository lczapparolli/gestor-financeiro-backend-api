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

import br.com.lczapparolli.database.repository.CategoriaRepository;
import br.com.lczapparolli.database.repository.ContaPagarRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Testa o mapeamento da entidade ContaPagar
 */
@QuarkusTest
public class ContaPagarTest {

    @Inject
    CategoriaRepository categoriaRepository;
    
    @Inject
    ContaPagarRepository contaPagarRepository;

    private static Long idCategoria;

    @BeforeEach
    @Transactional
    void prepararDados() {
        if (idCategoria == null) {
            var categoria = new Categoria();
            categoria.descricao = "Teste Conta a Pagar";
            categoriaRepository.persistAndFlush(categoria);
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
        var quantidadeInicial = contaPagarRepository.count();
        var contaPagar = new ContaPagar();
        contaPagar.periodo = LocalDate.now();
        contaPagar.categoria = categoriaRepository.findById(idCategoria);
        contaPagar.vencimento = LocalDate.now();
        contaPagar.valor = BigDecimal.ONE;
        
        // Verifica se os campos não estão preenchidos
        assertNull(contaPagar.dataCriacao);
        assertNull(contaPagar.versao);
        
        // Salva a nova conta a pagar
        contaPagarRepository.persistAndFlush(contaPagar);

        //Verifica se o registro foi incluído
        var quantidade = contaPagarRepository.count();
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
        contaPagar.periodo = LocalDate.now();
        contaPagar.categoria = categoriaRepository.findById(idCategoria);
        contaPagar.vencimento = LocalDate.now();
        contaPagar.valor = BigDecimal.ONE;
        contaPagarRepository.persistAndFlush(contaPagar);
        var criacaoAnterior = contaPagar.dataCriacao;
        var versaoAnterior = contaPagar.versao;

        // Procura a conta a pagar inserida e atualiza as informações
        ContaPagar contaPagarInserida = contaPagarRepository.findById(contaPagar.id);
        contaPagarInserida.valor = BigDecimal.TEN;
        contaPagarRepository.persistAndFlush(contaPagarInserida);

        // Verifica se os campos foram atualizados
        assertTrue(criacaoAnterior.isEqual(contaPagarInserida.dataCriacao));
        assertTrue(versaoAnterior.isBefore(contaPagarInserida.versao));
    }

}