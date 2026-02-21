package br.com.lczapparolli;

import java.util.List;

import br.com.lczapparolli.entity.Conta;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
public class GreetingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public List<String> hello() {
        return Conta.listAll()
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
        entity.persist();
        return entity.toString();
    }

}
