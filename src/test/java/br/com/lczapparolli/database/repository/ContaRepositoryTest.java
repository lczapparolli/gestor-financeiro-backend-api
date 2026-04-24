package br.com.lczapparolli.database.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
    var conta = Conta.builder()
      .descricao("Conta de teste")
      .ativo(true)
      .build();

    // Verifica se os campos não estão preenchidos
    assertNull(conta.getDataCriacao());
    assertNull(conta.getVersao());

    // Salva a nova conta
    contaRepository.persistAndFlush(conta);

    // Verifica se o registro foi incluído
    var quantidade = contaRepository.count();
    assertEquals(quantidadeInicial + 1, quantidade);
    assertNotNull(conta.getDataCriacao());
    assertNotNull(conta.getVersao());
  }

  /**
   * Verifica se o mapeamento da entidade permite a atualização de registros
   */
  @Test
  @Transactional
  void atualizar_sucesso_test() {
    // Prepara os dados iniciais
    var conta = Conta.builder()
      .descricao("Conta atualizar")
      .ativo(true)
      .build();
    contaRepository.persistAndFlush(conta);
    var criacaoAnterior = conta.getDataCriacao();
    var versaoAnterior = conta.getVersao();

    // Procura a conta inserida e atualiza as informações
    Conta contaInserida = contaRepository.findById(conta.getId());
    contaInserida.setDescricao("Descrição atualizada");
    contaRepository.persistAndFlush(contaInserida);

    // Verifica se os campos de atualização 
    assertTrue(criacaoAnterior.isEqual(contaInserida.getDataCriacao()));
    assertTrue(versaoAnterior.isBefore(contaInserida.getVersao()));
  }

  @Test
  void findByDescricao_sucesso_test() {
    fail("Não implementado");
  }

  @Test
  void findByDescricao_ignoraCaixaAlta_sucesso_test() {
    fail("Não implementado");
  }

}
