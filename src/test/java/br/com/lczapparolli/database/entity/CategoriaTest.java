package br.com.lczapparolli.database.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.lczapparolli.database.repository.CategoriaRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Testa o mapeamento da entidade Categoria
 */
@QuarkusTest
public class CategoriaTest {

  @Inject
  CategoriaRepository categoriaRepository;

  /**
   * Verifica se o mapeamento da entidade permite a inclusão de novos registros
   */
  @Test
  @Transactional
  @DisplayName("Entidade Categoria - Inclusão")
  void incluirCategoriaTest() {
    // Prepara os dados iniciais
    var quantidadeInicial = categoriaRepository.count();
    var categoria = new Categoria();
    categoria.descricao = "Categoria de teste";

    // Verifica se os campos não estão preenchidos
    assertNull(categoria.dataCriacao);
    assertNull(categoria.versao);

    // Salva a nova conta
    categoriaRepository.persistAndFlush(categoria);

    // Verifica se o registro foi incluído
    var quantidade = categoriaRepository.count();
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
    categoriaRepository.persistAndFlush(categoria);
    var criacaoAnterior = categoria.dataCriacao;
    var versaoAnterior = categoria.versao;

    // Procura a categoria inserida e atualiza as informações
    Categoria categoriaInserida = categoriaRepository.findById(categoria.id);
    categoriaInserida.descricao = "Descrição atualizada";
    categoriaRepository.persistAndFlush(categoriaInserida);

    // Verifica se os campos de atualização 
    assertTrue(criacaoAnterior.isEqual(categoriaInserida.dataCriacao));
    assertTrue(versaoAnterior.isBefore(categoriaInserida.versao));
  }

}
