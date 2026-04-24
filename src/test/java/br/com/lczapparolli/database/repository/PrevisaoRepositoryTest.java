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
import br.com.lczapparolli.database.entity.Previsao;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Testa o mapeamento da entidade Previsao
 */
@QuarkusTest
public class PrevisaoRepositoryTest {

    @Inject
    CategoriaRepository categoriaRepository;

    @Inject
    PrevisaoRepository previsaoRepository;

    private static Long idCategoria;

    @BeforeEach
    @Transactional
    void prepararDados() {
        if (idCategoria == null) {
            var categoria = Categoria.builder()
                .descricao("Teste previsão")
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
        var quantidadeInicial = previsaoRepository.count();
        var previsao = Previsao.builder()
            .categoria(categoriaRepository.findById(idCategoria))
            .periodo(LocalDate.now())
            .valor(BigDecimal.ONE)
            .ativo(true)
            .build();
        
        // Verifica se os campos não estão preenchidos
        assertNull(previsao.getDataCriacao());
        assertNull(previsao.getVersao());
        
        // Salva a nova previsão
        previsaoRepository.persistAndFlush(previsao);

        //Verifica se o registro foi incluído
        var quantidade = previsaoRepository.count();
        assertEquals(quantidadeInicial + 1, quantidade);
        assertNotNull(previsao.getDataCriacao());
        assertNotNull(previsao.getVersao());
    }

    /**
     * Verifica se o mapeamento da entidade permite a atualização de registros
     */
    @Test
    @Transactional
    void atualizar_sucesso_test() {
        // Prepara os dados iniciais
        var previsao = Previsao.builder()
            .categoria(categoriaRepository.findById(idCategoria))
            .periodo(LocalDate.now())
            .valor(BigDecimal.ONE)
            .ativo(true)
            .build();
        previsaoRepository.persistAndFlush(previsao);
        var criacaoAnterior = previsao.getDataCriacao();
        var versaoAnterior = previsao.getVersao();

        // Procura a previsão inserida e atualiza as informações
        Previsao previsaoInserida = previsaoRepository.findById(previsao.getId());
        previsaoInserida.setValor(BigDecimal.TEN);
        previsaoRepository.persistAndFlush(previsaoInserida);

        // Verifica se os campos foram atualizados
        assertTrue(criacaoAnterior.isEqual(previsaoInserida.getDataCriacao()));
        assertTrue(versaoAnterior.isBefore(previsaoInserida.getVersao()));
    }

}
