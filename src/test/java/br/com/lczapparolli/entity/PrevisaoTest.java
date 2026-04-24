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
 * Testa o mapeamento da entidade Previsao
 */
@QuarkusTest
public class PrevisaoTest {

    private static Long idCategoria;
    private static Long idPeriodo;

    @BeforeEach
    @Transactional
    void prepararDados() {
        if (idCategoria == null) {
            var categoria = new Categoria();
            categoria.descricao = "Teste previsão";
            categoria.persistAndFlush();
            idCategoria = categoria.id;
        }

        if (idPeriodo == null) {
            var periodo = new Periodo();
            periodo.dataInicio = LocalDate.now();
            periodo.dataFim = LocalDate.now();
            periodo.persistAndFlush();
            idPeriodo = periodo.id;
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
        var quantidadeInicial = Previsao.count();
        var previsao = new Previsao();
        previsao.categoria = Categoria.findById(idCategoria);
        previsao.periodo = Periodo.findById(idPeriodo);
        previsao.valor = BigDecimal.ONE;
        
        // Verifica se os campos não estão preenchidos
        assertNull(previsao.dataCriacao);
        assertNull(previsao.versao);
        
        // Salva a nova previsão
        previsao.persistAndFlush();

        //Verifica se o registro foi incluído
        var quantidade = Previsao.count();
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
        previsao.categoria = Categoria.findById(idCategoria);
        previsao.periodo = Periodo.findById(idPeriodo);
        previsao.valor = BigDecimal.ONE;
        previsao.persistAndFlush();
        var criacaoAnterior = previsao.dataCriacao;
        var versaoAnterior = previsao.versao;

        // Procura a previsão inserida e atualiza as informações
        Previsao previsaoInserida = Previsao.findById(previsao.id);
        previsaoInserida.valor = BigDecimal.TEN;
        previsaoInserida.persistAndFlush();

        // Verifica se os campos foram atualizados
        assertTrue(criacaoAnterior.isEqual(previsaoInserida.dataCriacao));
        assertTrue(versaoAnterior.isBefore(previsaoInserida.versao));
    }

}
