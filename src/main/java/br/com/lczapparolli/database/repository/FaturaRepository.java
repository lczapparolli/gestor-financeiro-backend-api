package br.com.lczapparolli.database.repository;

import br.com.lczapparolli.database.entity.Fatura;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FaturaRepository implements PanacheRepository<Fatura> {
    
}
