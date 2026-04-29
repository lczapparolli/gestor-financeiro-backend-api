package br.com.lczapparolli.database.repository;

import java.util.Optional;
import java.util.stream.Stream;

import br.com.lczapparolli.database.entity.Categoria;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CategoriaRepository implements PanacheRepository<Categoria> {

  public Optional<Categoria> findByDescricao(String descricao) {
    return find("lower(descricao) = lower(?1)", descricao).firstResultOptional();
  }

  public Stream<Categoria> listarPorSituacao(boolean ativas) {
    return find("ativo = ?1", ativas).stream();
  }

}
