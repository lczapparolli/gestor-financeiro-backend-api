package br.com.lczapparolli.database.repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import br.com.lczapparolli.database.entity.Previsao;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PrevisaoRepository implements PanacheRepository<Previsao> {

  public Stream<Previsao> listarPorPeriodo(LocalDate periodo) {
    Parameters parametros = Parameters.with("ativo", true)
        .and("periodo", periodo);

    return stream("ativo = :ativo AND periodo = :periodo", parametros);
  }

  public Optional<Previsao> findByPeriodoCategoria(LocalDate periodo, Long idCategoria) {
    Parameters parametros = Parameters.with("periodo", periodo)
        .and("idCategoria", idCategoria);

    return find("periodo = :periodo AND categoria.id = :idCategoria", parametros).singleResultOptional();
  }

}
