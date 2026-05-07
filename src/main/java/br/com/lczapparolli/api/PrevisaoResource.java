package br.com.lczapparolli.api;

import java.time.LocalDate;
import java.util.stream.Stream;

import br.com.lczapparolli.dto.PrevisaoDTO;
import br.com.lczapparolli.exception.GerenciadorException;
import br.com.lczapparolli.service.PrevisaoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

@Path("/api/previsao")
public class PrevisaoResource {

  @Inject
  PrevisaoService previsaoService;

  @GET
  public Stream<PrevisaoDTO> listar(@QueryParam("ano") String ano, @QueryParam("mes") String mes) {
    LocalDate periodo = LocalDate.of(Integer.parseInt(ano), Integer.parseInt(mes), 1);
    return previsaoService.listarPrevisoes(periodo);
  }

  @GET
  @Path("/{idPrevisao}")
  public PrevisaoDTO obterPrevisao(@PathParam("idPrevisao") Long idPrevisao) {
    throw new UnsupportedOperationException();
  }

  @POST
  public PrevisaoDTO inserirPrevisao(PrevisaoDTO previsaoDTO) throws GerenciadorException {
    return previsaoService.inserirPrevisao(previsaoDTO);
  }

  @PATCH
  @Path("/{idPrevisao}")
  public PrevisaoDTO atualizarPrevisao(@PathParam("idPrevisao") Long idPrevisao, PrevisaoDTO previsaoDTO)
      throws GerenciadorException {
    return previsaoService.atualizarPrevisao(idPrevisao, previsaoDTO);
  }

  @DELETE
  @Path("{idPrevisao}")
  public void desativarPrevisao(@PathParam("idPrevisao") Long idPrevisao) throws GerenciadorException {
    previsaoService.desativarPrevisao(idPrevisao);
  }

}
