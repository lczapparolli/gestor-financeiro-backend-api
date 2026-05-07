package br.com.lczapparolli.api;

import java.util.Optional;
import java.util.stream.Stream;

import br.com.lczapparolli.dto.CategoriaDTO;
import br.com.lczapparolli.exception.GerenciadorException;
import br.com.lczapparolli.service.CategoriaService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

@Path("/api/categoria")
public class CategoriaResource {

  @Inject
  CategoriaService categoriaService;

  @GET
  public Stream<CategoriaDTO> listarCategorias(@QueryParam("ativas") Boolean ativas) {
    return categoriaService.listarCategorias(ativas);
  }

  @GET
  @Path("/{idCategoria}")
  public Optional<CategoriaDTO> obterCategoria(@PathParam("idCategoria") Long idCategoria) {
    return categoriaService.obterCategoria(idCategoria);
  }

  @POST
  public CategoriaDTO inserirCategoria(CategoriaDTO categoriaDTO) throws GerenciadorException {
    return categoriaService.inserirCategoria(categoriaDTO);
  }

  @PUT
  @Path("/{idCategoria}")
  public CategoriaDTO atualizarCategoria(@PathParam("idCategoria") Long idCategoria, CategoriaDTO categoriaDTO)
      throws GerenciadorException {
    return categoriaService.atualizarCategoria(idCategoria, categoriaDTO);
  }

  // TODO: Alterar o verbo para DELETE
  @PUT
  @Path("/{idCategoria}/desativar")
  public void desativarCategoria(@PathParam("idCategoria") Long idCategoria) throws GerenciadorException {
    categoriaService.desativarCategoria(idCategoria);
  }

  // TODO: Realizar a reativação da categoria ao tentar inserir uma descrição já
  // existente
  @PUT
  @Path("/{idCategoria}/reativar")
  public void reativarCategoria(@PathParam("idCategoria") Long idCategoria) throws GerenciadorException {
    categoriaService.reativarCategoria(idCategoria);
  }

}
