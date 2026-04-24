package br.com.lczapparolli.database.repository;

import br.com.lczapparolli.database.entity.CartaoCredito;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CartaoCreditoRepository implements PanacheRepository<CartaoCredito> {
    
}
