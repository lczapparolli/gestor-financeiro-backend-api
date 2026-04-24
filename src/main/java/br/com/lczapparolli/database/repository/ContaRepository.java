package br.com.lczapparolli.database.repository;

import java.util.Optional;

import br.com.lczapparolli.database.entity.Conta;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ContaRepository implements PanacheRepository<Conta> {

    public Optional<Conta> findByDescricao(String descricao) {
        return find("lower(descricao) = lower(?)", descricao).singleResultOptional();
    }
    
}
