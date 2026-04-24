package br.com.lczapparolli.api;

import java.util.Optional;
import java.util.stream.Stream;

import br.com.lczapparolli.dto.ContaDTO;
import br.com.lczapparolli.exception.GerenciadorException;
import br.com.lczapparolli.service.ContaService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

@Path("/api/conta")
public class ContaResource {

  @Inject
  ContaService contaService;

  @GET
  public Stream<ContaDTO> listarContas(@QueryParam("ativas") Boolean ativas) {
    return contaService.listarContas(ativas);
  }

  @GET
  @Path("/{idConta}")
  public Optional<ContaDTO> obterConta(@PathParam("idConta") Long idConta) {
    return contaService.obterConta(idConta);
  }

  @POST
  public ContaDTO inserirConta(ContaDTO contaDTO) throws GerenciadorException {
    return contaService.inserirConta(contaDTO);
  }

  @PUT
  @Path("/{idConta}")
  public ContaDTO atualizarConta(@PathParam("idConta") Long idConta, ContaDTO contaDTO) throws GerenciadorException {
    return contaService.atualizarConta(idConta, contaDTO);
  }

  @PUT
  @Path("/{idConta}/desativar")
  public void desativarConta(@PathParam("idConta") Long idConta) throws GerenciadorException {
    contaService.desativarConta(idConta);
  }

  @PUT
  @Path("/{idConta}/reativar")
  public void reativarConta(@PathParam("idConta") Long idConta) throws GerenciadorException {
    contaService.reativarConta(idConta);
  }

}
