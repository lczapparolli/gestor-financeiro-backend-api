package br.com.lczapparolli.dto;

import br.com.lczapparolli.database.entity.Conta;
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
public class ContaDTO {

  private Long id;
  private String descricao;
  private boolean ativo;

  public static ContaDTO from(Conta conta) {
    return ContaDTO.builder()
        .id(conta.getId())
        .descricao(conta.getDescricao())
        .ativo(conta.isAtivo())
        .build();
  }

}
