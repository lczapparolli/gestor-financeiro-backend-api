package br.com.lczapparolli.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.lczapparolli.database.entity.Previsao;
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
public class PrevisaoDTO {

  private Long id;
  private CategoriaDTO categoria;
  private LocalDate periodo;
  private BigDecimal valor;

  public static PrevisaoDTO from(Previsao previsao) {
    return PrevisaoDTO.builder()
        .id(previsao.getId())
        .categoria(CategoriaDTO.from(previsao.getCategoria()))
        .periodo(previsao.getPeriodo())
        .valor(previsao.getValor())
        .build();
  }

}
