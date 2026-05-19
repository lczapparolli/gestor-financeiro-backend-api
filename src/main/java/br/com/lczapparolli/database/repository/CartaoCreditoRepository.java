package br.com.lczapparolli.database.repository;

import java.util.stream.Stream;

import br.com.lczapparolli.database.entity.CartaoCredito;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CartaoCreditoRepository implements PanacheRepository<CartaoCredito> {

  public Stream<CartaoCredito> listarAtivos() {
    return stream("ativo = ?1", true);
  }

}
