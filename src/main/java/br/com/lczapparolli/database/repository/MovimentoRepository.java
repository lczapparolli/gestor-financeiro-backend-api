package br.com.lczapparolli.database.repository;

import java.time.LocalDate;
import java.util.stream.Stream;

import br.com.lczapparolli.database.entity.Movimento;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MovimentoRepository implements PanacheRepository<Movimento> {

  public Stream<Movimento> listarPorPeriodo(LocalDate periodo) {
    Parameters parameters = Parameters.with("periodo", periodo)
        .and("ativo", true);

    return stream("periodo = :periodo AND ativo = :ativo", parameters);
  }

}
