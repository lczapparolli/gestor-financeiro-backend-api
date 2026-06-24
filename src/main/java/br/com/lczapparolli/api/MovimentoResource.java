package br.com.lczapparolli.api;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.stream.Stream;

import br.com.lczapparolli.dto.MovimentoDTO;
import br.com.lczapparolli.exception.GerenciadorException;
import br.com.lczapparolli.service.MovimentoService;
import br.com.lczapparolli.util.PeriodoUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

@Path("/api/movimento")
public class MovimentoResource {

  @Inject
  MovimentoService movimentoService;

  @GET
  public Stream<MovimentoDTO> listarMovimentos(@QueryParam("ano") String ano, @QueryParam("mes") String mes) {
    LocalDate periodo = PeriodoUtil.fromAnoMes(ano, mes);
    return movimentoService.listarMovimentos(periodo);
  }

  @POST
  public MovimentoDTO inserirMovimento(MovimentoDTO movimentoDTO) throws GerenciadorException {
    return movimentoService.inserirMovimento(movimentoDTO);
  }

  @POST
  @Path("/lote")
  public Stream<MovimentoDTO> inserirMovimentosLote(@QueryParam("ano") String ano, @QueryParam("mes") String mes,
      String movimentos) throws GerenciadorException, ParseException {
    LocalDate periodo = PeriodoUtil.fromAnoMes(ano, mes);
    return movimentoService.inserirMovimentosLote(periodo, movimentos.lines());
  }

  @PATCH
  @Path("/{idMovimento}")
  public MovimentoDTO atualizarMovimento(@PathParam("idMovimento") Long idMovimento, MovimentoDTO movimentoDTO)
      throws GerenciadorException {
    return movimentoService.atualizarMovimento(idMovimento, movimentoDTO);
  }

  @DELETE
  @Path("/{idMovimento}")
  public void desativarMovimento(@PathParam("idMovimento") Long idMovimento) throws GerenciadorException {
    movimentoService.desativarMovimento(idMovimento);
  }

}
