package br.com.lczapparolli.dto;

import br.com.lczapparolli.database.entity.CartaoCredito;
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
public class CartaoDTO {

  private Long id;
  private String descricao;
  private int diaVencimento;
  private int diaFechamento;

  public static CartaoDTO from(CartaoCredito cartaoCredito) {
    return CartaoDTO.builder()
        .id(cartaoCredito.getId())
        .descricao(cartaoCredito.getDescricao())
        .diaVencimento(cartaoCredito.getDiaVencimento())
        .diaFechamento(cartaoCredito.getDiaFechamento())
        .build();
  }

}
