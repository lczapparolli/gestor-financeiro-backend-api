package br.com.lczapparolli.service;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import br.com.lczapparolli.database.entity.Categoria;
import br.com.lczapparolli.database.repository.CategoriaRepository;
import br.com.lczapparolli.dto.CategoriaDTO;
import br.com.lczapparolli.exception.GerenciadorException;
import br.com.lczapparolli.exception.ValidacaoException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CategoriaService {

  @Inject
  CategoriaRepository categoriaRepository;

  public Stream<CategoriaDTO> listarCategorias(Boolean ativas) {
    Stream<Categoria> listaCategorias;
    if (Objects.isNull(ativas)) {
      listaCategorias = categoriaRepository.streamAll();
    } else {
      listaCategorias = categoriaRepository.listarPorSituacao(ativas);
    }

    return listaCategorias.map(CategoriaDTO::from);
  }

  public Optional<CategoriaDTO> obterCategoria(Long idCategoria) {
    return categoriaRepository.findByIdOptional(idCategoria).map(CategoriaDTO::from);
  }

  @Transactional
  public CategoriaDTO inserirCategoria(CategoriaDTO categoriaDTO) throws GerenciadorException {
    if (Objects.isNull(categoriaDTO)) {
      throw new ValidacaoException("Os dados estão vazios");
    }

    if (Objects.isNull(categoriaDTO.getDescricao()) || categoriaDTO.getDescricao().isBlank()) {
      throw new ValidacaoException("A descrição precisa ser preenchida");
    }

    var pesquisa = categoriaRepository.findByDescricao(categoriaDTO.getDescricao());
    if (pesquisa.isPresent()) {
      if (!pesquisa.get().isAtivo()) {
        throw new ValidacaoException("Já existe uma categoria desativada com a mesma descrição");
      }

      throw new ValidacaoException("Já existe uma categoria com a mesma descrição");
    }

    var categoria = Categoria.builder()
        .descricao(categoriaDTO.getDescricao())
        .ativo(true)
        .build();

    categoriaRepository.persist(categoria);

    return CategoriaDTO.from(categoria);
  }

  @Transactional
  public CategoriaDTO atualizarCategoria(Long idCategoria, CategoriaDTO categoriaDTO) throws GerenciadorException {
    if (Objects.isNull(categoriaDTO)) {
      throw new ValidacaoException("Os dados estão vazios");
    }

    if (Objects.isNull(categoriaDTO.getDescricao()) || categoriaDTO.getDescricao().isBlank()) {
      throw new ValidacaoException("A descrição precisa ser preenchida");
    }

    if (Objects.isNull(idCategoria) || idCategoria.compareTo(0L) <= 0) {
      throw new ValidacaoException("O id precisa ser preenchido");
    }

    var pesquisa = categoriaRepository.findByDescricao(categoriaDTO.getDescricao());
    if (pesquisa.isPresent() && pesquisa.get().getId() != idCategoria) {
      if (!pesquisa.get().isAtivo()) {
        throw new ValidacaoException("Já existe uma categoria desativada com a mesma descrição");
      }

      throw new ValidacaoException("Já existe uma categoria com a mesma descrição");
    }

    var pesquisaId = categoriaRepository.findByIdOptional(idCategoria);
    if (pesquisaId.isEmpty()) {
      throw new ValidacaoException("Categoria não encontrada");
    }
    if (!pesquisaId.get().isAtivo()) {
      throw new ValidacaoException("A categoria está desativada");
    }

    var categoria = pesquisaId.get();
    categoria.setDescricao(categoriaDTO.getDescricao());

    categoriaRepository.persist(categoria);

    return CategoriaDTO.from(categoria);
  }

  @Transactional
  public void desativarCategoria(Long id) throws GerenciadorException {
    var resultadoConsulta = categoriaRepository.findByIdOptional(id);
    if (resultadoConsulta.isEmpty()) {
      throw new ValidacaoException("Categoria não encontrada");
    }

    if (!resultadoConsulta.get().isAtivo()) {
      throw new ValidacaoException("A categpria já está desativada");
    }

    Categoria categoria = resultadoConsulta.get();
    categoria.setAtivo(false);

    categoriaRepository.persist(categoria);
  }

  @Transactional
  public void reativarCategoria(Long id) throws GerenciadorException {
    var resultadoConsulta = categoriaRepository.findByIdOptional(id);
    if (resultadoConsulta.isEmpty()) {
      throw new ValidacaoException("Categoria não encontrada");
    }

    if (resultadoConsulta.get().isAtivo()) {
      throw new ValidacaoException("A categoria já está ativa");
    }

    Categoria categoria = resultadoConsulta.get();
    categoria.setAtivo(true);

    categoriaRepository.persist(categoria);
  }

}
