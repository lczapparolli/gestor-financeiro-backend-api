package br.com.lczapparolli.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.lczapparolli.database.entity.ContaPagar;
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
public class ContaPagarDTO {

  private Long id;
  private String descricao;
  private LocalDate periodo;
  private LocalDate vencimento;
  private BigDecimal valor;
  private CategoriaDTO categoria;

  public static ContaPagarDTO from(ContaPagar contaPagar) {
    return ContaPagarDTO.builder()
        .id(contaPagar.getId())
        .descricao(contaPagar.getDescricao())
        .periodo(contaPagar.getPeriodo())
        .vencimento(contaPagar.getVencimento())
        .valor(contaPagar.getValor())
        .categoria(CategoriaDTO.from(contaPagar.getCategoria()))
        .build();
  }

}
