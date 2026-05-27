package br.com.lczapparolli.api;

import java.time.LocalDate;
import java.util.stream.Stream;

import br.com.lczapparolli.dto.ContaPagarDTO;
import br.com.lczapparolli.exception.GerenciadorException;
import br.com.lczapparolli.service.ContaPagarService;
import br.com.lczapparolli.util.PeriodoUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

@Path("/api/conta-pagar")
public class ContaPagarResource {

  @Inject
  ContaPagarService contaPagarService;

  @GET
  public Stream<ContaPagarDTO> listarContasPagar(@QueryParam("ano") String ano, @QueryParam("mes") String mes)
      throws GerenciadorException {
    LocalDate periodo = PeriodoUtil.fromAnoMes(ano, mes);
    return contaPagarService.listarContasPagar(periodo);
  }

  @POST
  public ContaPagarDTO inserirContaPagar(ContaPagarDTO contaPagarDTO) throws GerenciadorException {
    return contaPagarService.inserirContaPagar(contaPagarDTO);
  }

  @PATCH
  @Path("/{idContaPagar}")
  public ContaPagarDTO atualizarContaPagar(@PathParam("idContaPagar") Long idContaPagar, ContaPagarDTO contaPagarDTO)
      throws GerenciadorException {
    return contaPagarService.atualizarContaPagar(idContaPagar, contaPagarDTO);
  }

  @DELETE
  @Path("/{idContaPagar}")
  public void desativarContaPagar(@PathParam("idContaPagar") Long idContaPagar) throws GerenciadorException {
    contaPagarService.desativarContaPagar(idContaPagar);
  }
}
