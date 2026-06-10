package br.com.lczapparolli.database.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import br.com.lczapparolli.database.entity.Categoria;
import br.com.lczapparolli.service.CategoriaService.CategoriasSistema;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
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
    Parameters params = Parameters.with("periodo", periodo)
        .and("ativo", true)
        .and("idsSistema", List.of(CategoriasSistema.FATURA.getId(), CategoriasSistema.TRANSFERENCIA.getId()));

    String hql = "SELECT c FROM Categoria c " +
        "LEFT JOIN Previsao p ON p.categoria = c AND p.periodo = :periodo AND p.ativo = true " +
        "WHERE c.ativo = :ativo " +
        "AND p.id IS NULL " +
        "AND c.id NOT IN :idsSistema";

    return find(hql, params).stream();
  }

}
