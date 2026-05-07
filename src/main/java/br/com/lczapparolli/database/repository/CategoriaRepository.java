package br.com.lczapparolli.database.repository;

import java.time.LocalDate;
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

  public Stream<Categoria> listarSemPrevisao(LocalDate periodo) {
    String hql = "SELECT c FROM Categoria c " +
        "LEFT JOIN Previsao p ON p.categoria = c AND p.periodo = ?1 AND p.ativo = true " +
        "WHERE p.id IS NULL";

    return find(hql, periodo).stream();
  }

}
