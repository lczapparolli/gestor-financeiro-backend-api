package br.com.lczapparolli.api;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Stream;

import br.com.lczapparolli.dto.PrevisaoDTO;
import br.com.lczapparolli.exception.GerenciadorException;
import br.com.lczapparolli.service.PrevisaoService;
import br.com.lczapparolli.util.PeriodoUtil;
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
    LocalDate periodo = PeriodoUtil.fromAnoMes(ano, mes);
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

  @POST
  @Path("/clonar")
  public Stream<PrevisaoDTO> clonarPrevisoes(Map<String, Object> parametros) throws GerenciadorException {
    String origem = parametros.get("origem").toString();
    LocalDate periodoOrigem = LocalDate.parse(origem);
    String destino = parametros.get("destino").toString();
    LocalDate periodoDestino = LocalDate.parse(destino);

    return previsaoService.clonarPrevisoes(periodoOrigem, periodoDestino);
  }

}
