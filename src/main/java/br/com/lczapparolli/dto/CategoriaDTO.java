package br.com.lczapparolli.dto;

import br.com.lczapparolli.database.entity.Categoria;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoriaDTO {

  private Long id;
  private String descricao;

  public static CategoriaDTO from(Categoria categoria) {
    return CategoriaDTO.builder()
        .id(categoria.getId())
        .descricao(categoria.getDescricao())
        .build();
  }

}
