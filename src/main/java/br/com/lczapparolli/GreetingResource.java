package br.com.lczapparolli;

import java.util.List;

import br.com.lczapparolli.database.entity.Conta;
import br.com.lczapparolli.database.repository.ContaRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
public class GreetingResource {

    @Inject
    ContaRepository contaRepository;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public List<String> hello() {
        return contaRepository.listAll()
                .stream()
                .map(item -> item.toString())
                .toList();
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public String criar(String nome) {
        var entity = new Conta();
        entity.descricao = nome;
        contaRepository.persist(entity);
        return entity.toString();
    }

}
