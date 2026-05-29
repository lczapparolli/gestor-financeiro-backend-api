package br.com.lczapparolli.database.repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import br.com.lczapparolli.database.entity.ContaPagar;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ContaPagarRepository implements PanacheRepository<ContaPagar> {

  public Stream<ContaPagar> listarAtivasPorPeriodo(LocalDate periodo) {
    Parameters params = Parameters.with("periodo", periodo)
        .and("ativo", true);

    return stream("periodo = :periodo AND ativo = :ativo", params);
  }

  public Optional<ContaPagar> pesquisarPorPeriodoDescricao(LocalDate periodo, String descricao) {
    Parameters params = Parameters.with("periodo", periodo)
        .and("descricao", descricao);

    return find("periodo = :periodo AND lower(descricao) = lower(:descricao)", params)
        .singleResultOptional();
  }

}
