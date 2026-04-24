package br.com.lczapparolli.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;

/**
 * Testa o mapeamento da entidade Categoria
 */
@QuarkusTest
public class CategoriaTest {

  /**
   * Verifica se o mapeamento da entidade permite a inclusão de novos registros
   */
  @Test
  @Transactional
  @DisplayName("Entidade Categoria - Inclusão")
  void incluirCategoriaTest() {
    // Prepara os dados iniciais
    var quantidadeInicial = Categoria.count();
    var categoria = new Categoria();
    categoria.descricao = "Categoria de teste";

    // Verifica se os campos não estão preenchidos
    assertNull(categoria.dataCriacao);
    assertNull(categoria.versao);

    // Salva a nova conta
    categoria.persistAndFlush();

    // Verifica se o registro foi incluído
    var quantidade = Categoria.count();
    assertEquals(quantidadeInicial + 1, quantidade);
    assertNotNull(categoria.dataCriacao);
    assertNotNull(categoria.versao);
    assertTrue(categoria.ativo);
  }

  /**
   * Verifica se o mapeamento da entidade permite a atualização de registros
   */
  @Test
  @Transactional
  @DisplayName("Entidade Categoria - Alteração")
  void atualizarCategoriaTest() {
    // Prepara os dados iniciais
    var categoria = new Categoria();
    categoria.descricao = "Categoria atualizar";
    categoria.persistAndFlush();
    var criacaoAnterior = categoria.dataCriacao;
    var versaoAnterior = categoria.versao;

    // Procura a categoria inserida e atualiza as informações
    Categoria categoriaInserida = Categoria.findById(categoria.id);
    categoriaInserida.descricao = "Descrição atualizada";
    categoriaInserida.persistAndFlush();

    // Verifica se os campos de atualização 
    assertTrue(criacaoAnterior.isEqual(categoriaInserida.dataCriacao));
    assertTrue(versaoAnterior.isBefore(categoriaInserida.versao));
  }

}
