package br.com.lczapparolli.api;

import java.util.stream.Stream;

import br.com.lczapparolli.dto.CartaoDTO;
import br.com.lczapparolli.exception.GerenciadorException;
import br.com.lczapparolli.service.CartaoCreditoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/api/cartao-credito")
public class CartaoCreditoResource {

  @Inject
  CartaoCreditoService cartaoCreditoService;

  @GET
  public Stream<CartaoDTO> listarCartoes() {
    return cartaoCreditoService.listarCartoes();
  }

  @POST
  public CartaoDTO adicionarCartao(CartaoDTO cartaoDTO) throws GerenciadorException {
    return cartaoCreditoService.inserirCartao(cartaoDTO);
  }

  @Path("/{idCartao}")
  @PUT
  public CartaoDTO alterarCartao(@PathParam("idCartao") Long idCartao, CartaoDTO cartaoDTO)
      throws GerenciadorException {
    return cartaoCreditoService.atualizarCartao(idCartao, cartaoDTO);
  }

  @Path("/{idCartao}")
  @DELETE
  public void desativarCartao(@PathParam("idCartao") Long idCartao) throws GerenciadorException {
    cartaoCreditoService.desativarCartao(idCartao);
  }

}
