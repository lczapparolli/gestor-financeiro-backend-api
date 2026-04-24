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
import br.com.lczapparolli.database.entity.ContaPagar;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Testa o mapeamento da entidade ContaPagar
 */
@QuarkusTest
public class ContaPagarRepositoryTest {

    @Inject
    CategoriaRepository categoriaRepository;
    
    @Inject
    ContaPagarRepository contaPagarRepository;

    private static Long idCategoria;

    @BeforeEach
    @Transactional
    void prepararDados() {
        if (idCategoria == null) {
            var categoria = Categoria.builder()
                .descricao("Teste Conta a Pagar")
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
        var quantidadeInicial = contaPagarRepository.count();
        var contaPagar = ContaPagar.builder()
            .periodo(LocalDate.now())
            .categoria(categoriaRepository.findById(idCategoria))
            .vencimento(LocalDate.now())
            .valor(BigDecimal.ONE)
            .ativo(true)
            .build();
        
        // Verifica se os campos não estão preenchidos
        assertNull(contaPagar.getDataCriacao());
        assertNull(contaPagar.getVersao());
        
        // Salva a nova conta a pagar
        contaPagarRepository.persistAndFlush(contaPagar);

        //Verifica se o registro foi incluído
        var quantidade = contaPagarRepository.count();
        assertEquals(quantidadeInicial + 1, quantidade);
        assertNotNull(contaPagar.getDataCriacao());
        assertNotNull(contaPagar.getVersao());
    }

    /**
     * Verifica se o mapeamento da entidade permite a atualização de registros
     */
    @Test
    @Transactional
    void atualizar_sucesso_test() {
        // Prepara os dados iniciais
        var contaPagar = ContaPagar.builder()
            .periodo(LocalDate.now())
            .categoria(categoriaRepository.findById(idCategoria))
            .vencimento(LocalDate.now())
            .valor(BigDecimal.ONE)
            .ativo(true)
            .build();
        contaPagarRepository.persistAndFlush(contaPagar);
        var criacaoAnterior = contaPagar.getDataCriacao();
        var versaoAnterior = contaPagar.getVersao();

        // Procura a conta a pagar inserida e atualiza as informações
        ContaPagar contaPagarInserida = contaPagarRepository.findById(contaPagar.getId());
        contaPagarInserida.setValor(BigDecimal.TEN);
        contaPagarRepository.persistAndFlush(contaPagarInserida);

        // Verifica se os campos foram atualizados
        assertTrue(criacaoAnterior.isEqual(contaPagarInserida.getDataCriacao()));
        assertTrue(versaoAnterior.isBefore(contaPagarInserida.getVersao()));
    }

}