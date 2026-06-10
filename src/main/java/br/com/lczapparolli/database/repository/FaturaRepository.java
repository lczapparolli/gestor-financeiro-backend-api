package br.com.lczapparolli.database.repository;

import java.time.LocalDate;
import java.util.Optional;

import br.com.lczapparolli.database.entity.Fatura;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FaturaRepository implements PanacheRepository<Fatura> {

  public Optional<Fatura> findByCartaoPeriodo(Long idCartao, LocalDate periodo) {
    Parameters params = Parameters.with("idCartao", idCartao)
        .and("periodo", periodo)
        .and("ativo", true);

    return find("cartaoCredito.id = :idCartao AND periodo = :periodo AND ativo = :ativo", params)
        .singleResultOptional();
  }

}
