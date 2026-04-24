package br.com.lczapparolli.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.util.Optional;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import br.com.lczapparolli.database.entity.Conta;
import br.com.lczapparolli.database.repository.ContaRepository;
import br.com.lczapparolli.dto.ContaDTO;
import br.com.lczapparolli.exception.GerenciadorException;
import br.com.lczapparolli.exception.ValidacaoException;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/**
 * Valida as regras de negócio para manipulação de Contas
 */
@QuarkusTest
public class ContaServiceTest {

    @Inject
    ContaService contaService;

    @InjectMock
    ContaRepository contaRepository;

    @Test
    void incluirConta_sucesso_test() throws GerenciadorException {
        // Preparando ferramentas de mock
        ArgumentCaptor<@NonNull Conta> contaCaptor = ArgumentCaptor.forClass(Conta.class);
        Mockito.doAnswer(invocation -> {
            Conta conta = invocation.getArgument(0, Conta.class);
            conta.setId(1L);
            return null;
        }).when(contaRepository).persist(any(Conta.class));

        // Executando método
        var contaDTO = ContaDTO.builder()
            .descricao("Teste service")
            .build();
        var resultado = contaService.inserirConta(contaDTO);
        
        // Valida o resultado
        assertNotNull(resultado);
        assertNotNull(resultado.getId());

        // Verificando a chamada ao repositório
        Mockito.verify(contaRepository, times(1)).persist(contaCaptor.capture());
        assertNotNull(contaCaptor.getValue());
        assertTrue(contaCaptor.getValue().isAtivo());
        assertEquals(contaDTO.getDescricao(), contaCaptor.getValue().getDescricao());
    }

    @Test
    void incluirConta_parametroNulo_deveGerarErro_test() {
        var excecao = assertThrows(ValidacaoException.class, () -> contaService.inserirConta(null));
        assertEquals("Os dados estão vazios", excecao.getMessage());

        // Verificando a chamada ao repositório
        Mockito.verify(contaRepository, never()).persist(any(Conta.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @NullSource
    void incluirConta_descricaoVazia_deveGerarErro_test(String descricao) {
        var contaDTO = ContaDTO.builder().descricao(descricao).build();
        var excecao = assertThrows(ValidacaoException.class, () -> contaService.inserirConta(contaDTO));
        assertEquals("A descrição precisa ser preenchida", excecao.getMessage());

        // Verificando a chamada ao repositório
        Mockito.verify(contaRepository, never()).persist(any(Conta.class));
    }

    @Test
    void incluirConta_descricaoDuplicada_deveGerarErro_test() {
        String descricao = "Teste duplicado";
        Conta contaMock = Conta.builder()
            .descricao(descricao)
            .id(1L)
            .ativo(true)
            .build();
        Mockito.doReturn(Optional.of(contaMock)).when(contaRepository).findByDescricao(descricao);

        var contaDTO = ContaDTO.builder().descricao(descricao).build();
        var excecao = assertThrows(ValidacaoException.class, () -> contaService.inserirConta(contaDTO));
        assertEquals("Já existe uma conta com a mesma descrição", excecao.getMessage());

        // Verificando a chamada ao repositório
        Mockito.verify(contaRepository, never()).persist(any(Conta.class));
    }

    @Test
    void incluirConta_descricaoDuplicadaDesativada_deveGerarErro_test() {
        String descricao = "Teste duplicado";
        Conta contaMock = Conta.builder()
            .descricao(descricao)
            .id(1L)
            .ativo(false)
            .build();
        Mockito.doReturn(Optional.of(contaMock)).when(contaRepository).findByDescricao(descricao);

        var contaDTO = ContaDTO.builder().descricao(descricao).build();
        var excecao = assertThrows(ValidacaoException.class, () -> contaService.inserirConta(contaDTO));
        assertEquals("Já existe uma conta desativada com a mesma descrição", excecao.getMessage());

        // Verificando a chamada ao repositório
        Mockito.verify(contaRepository, never()).persist(any(Conta.class));
    }

    @Test
    void atualizarConta_sucesso_test() {
        fail("Não implementado");
    }

    @Test
    void atualizarConta_parametroNulo_deveGerarErro_test() {
        fail("Não implementado");
    }

    @Test
    void atualizarConta_descricaoVazia_deveGerarErro_test() {
        fail("Não implementado");
    }

    @Test
    void atualizarConta_descricaoDuplicada_deveGerarErro_test() {
        fail("Não implementado");
    }

    @Test
    void atualizarConta_descricaoDuplicadaDesativada_deveGerarErro_test() {
        fail("Não implementado");
    }

    @Test
    void atualizarConta_contaInexistente_deveGerarErro_test() {
        fail("Não implementado");
    }

    @Test
    void desativarConta_sucesso_test() {
        fail("Não implementado");
    }

    @Test
    void desativarConta_contaDesativada_deveGerarErro_test() {
        fail("Não implementado");
    }

    @Test
    void desativarConta_contaInexistente_deveGerarErro_test() {
        fail("Não implementado");
    }

    @Test
    void reativarConta_sucesso_test() {
        fail("Não implementado");
    }

    @Test
    void reativarConta_contaAtiva_deveGerarErro_test() {
        fail("Não implementado");
    }

    @Test
    void reativarConta_contaInexistente_deveGerarErro_test() {
        fail("Não implementado");
    }

}
