package br.com.lczapparolli.database.repository;

import java.util.Optional;
import java.util.stream.Stream;

import br.com.lczapparolli.database.entity.Conta;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ContaRepository implements PanacheRepository<Conta> {

  public Optional<Conta> findByDescricao(String descricao) {
    return find("lower(descricao) = lower(?1)", descricao).firstResultOptional();
  }

  public Stream<Conta> listarPorSituacao(boolean ativas) {
    return find("ativo = ?1", ativas).stream();
  }

}
