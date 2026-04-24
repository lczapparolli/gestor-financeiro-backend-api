package br.com.lczapparolli.database.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import br.com.lczapparolli.database.entity.Conta;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Testa o mapeamento da entidade Conta
 */
@QuarkusTest
public class ContaRepositoryTest {

  @Inject
  ContaRepository contaRepository;

  /**
   * Verifica se o mapeamento da entidade permite a inclusão de novos registros
   */
  @Test
  @Transactional
  void incluir_sucesso_test() {
    // Prepara os dados iniciais
    var quantidadeInicial = contaRepository.count();
    var conta = new Conta();
    conta.descricao = "Conta de teste";

    // Verifica se os campos não estão preenchidos
    assertNull(conta.dataCriacao);
    assertNull(conta.versao);

    // Salva a nova conta
    contaRepository.persistAndFlush(conta);

    // Verifica se o registro foi incluído
    var quantidade = contaRepository.count();
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
  void atualizar_sucesso_test() {
    // Prepara os dados iniciais
    var conta = new Conta();
    conta.descricao = "Conta atualizar";
    contaRepository.persistAndFlush(conta);
    var criacaoAnterior = conta.dataCriacao;
    var versaoAnterior = conta.versao;

    // Procura a conta inserida e atualiza as informações
    Conta contaInserida = contaRepository.findById(conta.id);
    contaInserida.descricao = "Descrição atualizada";
    contaRepository.persistAndFlush(contaInserida);

    // Verifica se os campos de atualização 
    assertTrue(criacaoAnterior.isEqual(contaInserida.dataCriacao));
    assertTrue(versaoAnterior.isBefore(contaInserida.versao));
  }

}
