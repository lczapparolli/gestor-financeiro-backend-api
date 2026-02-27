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
 * Testa o mapeamento da entidade Conta
 */
@QuarkusTest
public class ContaTest {

  /**
   * Verifica se o mapeamento da entidade permite a inclusão de novos registros
   */
  @Test
  @Transactional
  @DisplayName("Entidade Conta - Inclusão")
  void incluirContaTest() {
    // Prepara os dados iniciais
    var quantidadeInicial = Conta.count();
    var conta = new Conta();
    conta.descricao = "Conta de teste";

    // Verifica se os campos não estão preenchidos
    assertNull(conta.dataCriacao);
    assertNull(conta.versao);

    // Salva a nova conta
    conta.persistAndFlush();

    // Verifica se o registro foi incluído
    var quantidade = Conta.count();
    assertEquals(quantidadeInicial + 1, quantidade);
    assertNotNull(conta.dataCriacao);
    assertNotNull(conta.versao);
    assertTrue(conta.ativo);
  }

  /**
   * Verifica se o mapeamento da entidade permite a atualização de registros
   */
  @Test
  @Transactional
  @DisplayName("Entidade Conta - Alteração")
  void atualizarContaTest() {
    // Prepara os dados iniciais
    var conta = new Conta();
    conta.descricao = "Conta atualizar";
    conta.persistAndFlush();
    var criacaoAnterior = conta.dataCriacao;
    var versaoAnterior = conta.versao;

    // Procura a conta inserida e atualiza as informações
    Conta contaInserida = Conta.findById(conta.id);
    contaInserida.descricao = "Descrição atualizada";
    contaInserida.persistAndFlush();

    // Verifica se os campos de atualização 
    assertTrue(criacaoAnterior.isEqual(contaInserida.dataCriacao));
    assertTrue(versaoAnterior.isBefore(contaInserida.versao));
  }

}
