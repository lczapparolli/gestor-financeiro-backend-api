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
import br.com.lczapparolli.database.repository.PrevisaoRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Testa o mapeamento da entidade Previsao
 */
@QuarkusTest
public class PrevisaoTest {

    @Inject
    CategoriaRepository categoriaRepository;

    @Inject
    PrevisaoRepository previsaoRepository;

    private static Long idCategoria;

    @BeforeEach
    @Transactional
    void prepararDados() {
        if (idCategoria == null) {
            var categoria = new Categoria();
            categoria.descricao = "Teste previsão";
            categoriaRepository.persistAndFlush(categoria);
            idCategoria = categoria.id;
        }
    }

    /**
     * Verifica se o mapeamento da entidade permite a inclusão de novos registros
     */
    @Test
    @Transactional
    @DisplayName("Entidade Previsão - Inclusão")
    void incluirPrevisaoTest() {
        // Prepara os dados iniciais
        var quantidadeInicial = previsaoRepository.count();
        var previsao = new Previsao();
        previsao.categoria = categoriaRepository.findById(idCategoria);
        previsao.periodo = LocalDate.now();
        previsao.valor = BigDecimal.ONE;
        
        // Verifica se os campos não estão preenchidos
        assertNull(previsao.dataCriacao);
        assertNull(previsao.versao);
        
        // Salva a nova previsão
        previsaoRepository.persistAndFlush(previsao);

        //Verifica se o registro foi incluído
        var quantidade = previsaoRepository.count();
        assertEquals(quantidadeInicial + 1, quantidade);
        assertNotNull(previsao.dataCriacao);
        assertNotNull(previsao.versao);
        assertTrue(previsao.ativo);
    }

    /**
     * Verifica se o mapeamento da entidade permite a atualização de registros
     */
    @Test
    @Transactional
    @DisplayName("Entidade Previsão - Alteração")
    void atualizarPrevisaoTest() {
        // Prepara os dados iniciais
        var previsao = new Previsao();
        previsao.categoria = categoriaRepository.findById(idCategoria);
        previsao.periodo = LocalDate.now();
        previsao.valor = BigDecimal.ONE;
        previsaoRepository.persistAndFlush(previsao);
        var criacaoAnterior = previsao.dataCriacao;
        var versaoAnterior = previsao.versao;

        // Procura a previsão inserida e atualiza as informações
        Previsao previsaoInserida = previsaoRepository.findById(previsao.id);
        previsaoInserida.valor = BigDecimal.TEN;
        previsaoRepository.persistAndFlush(previsaoInserida);

        // Verifica se os campos foram atualizados
        assertTrue(criacaoAnterior.isEqual(previsaoInserida.dataCriacao));
        assertTrue(versaoAnterior.isBefore(previsaoInserida.versao));
    }

}
