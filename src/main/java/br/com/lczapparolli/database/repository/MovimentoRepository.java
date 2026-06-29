package br.com.lczapparolli.database.repository;

import java.math.BigDecimal;
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

  public BigDecimal saldoContaAteOPeriodo(Long idConta, LocalDate periodo) {
    Parameters params = Parameters.with("idConta", idConta)
        .and("periodo", periodo)
        .and("ativo", true);

    return find(
        "SELECT sum(valor) FROM Movimento m WHERE m.ativo = :ativo AND m.conta.id = :idConta AND m.periodo < :periodo",
        params)
        .project(BigDecimal.class)
        .singleResult();
  }

  public BigDecimal saldoContaNoPeriodo(Long idConta, LocalDate periodo) {
    Parameters params = Parameters.with("idConta", idConta)
        .and("periodo", periodo)
        .and("ativo", true);

    return find(
        "SELECT sum(valor) FROM Movimento m WHERE m.ativo = :ativo AND m.conta.id = :idConta and m.periodo = :periodo",
        params)
        .project(BigDecimal.class)
        .singleResult();
  }

  public BigDecimal saldoCategoriaNoPeriodo(Long idCategoria, LocalDate periodo) {
    Parameters params = Parameters.with("idCategoria", idCategoria)
        .and("periodo", periodo)
        .and("ativo", true);

    return find(
        "SELECT sum(valor) FROM Movimento m WHERE m.ativo = :ativo AND m.categoria.id = :idCategoria AND m.periodo = :periodo",
        params)
        .project(BigDecimal.class)
        .singleResult();
  }

  public BigDecimal saldoCategoriaAteOPeriodo(Long idCategoria, LocalDate periodo) {
    Parameters params = Parameters.with("idCategoria", idCategoria)
        .and("periodo", periodo)
        .and("ativo", true);

    return find(
        "SELECT sum(valor) FROM Movimento m WHERE m.ativo = :ativo AND m.categoria.id = :idCategoria AND m.periodo < :periodo",
        params)
        .project(BigDecimal.class)
        .singleResult();
  }

  public BigDecimal saldoContaPagarNoPeriodo(Long idContaPagar, LocalDate periodo) {
    Parameters params = Parameters.with("idContaPagar", idContaPagar)
        .and("periodo", periodo)
        .and("ativo", true);

    return find(
        "SELECT sum(valor) FROM Movimento m WHERE m.ativo = :ativo AND m.contaPagar.id = :idContaPagar AND m.periodo = :periodo",
        params)
        .project(BigDecimal.class)
        .singleResult();
  }

}
