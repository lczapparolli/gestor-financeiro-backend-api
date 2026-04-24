package br.com.lczapparolli.database.repository;

import br.com.lczapparolli.database.entity.ContaPagar;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ContaPagarRepository implements PanacheRepository<ContaPagar> {
    
}
