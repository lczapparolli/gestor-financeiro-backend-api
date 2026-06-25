package br.com.lczapparolli.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContaComSaldoDTO extends ContaDTO {

  private BigDecimal saldoInicial;
  private BigDecimal saldoFinal;

  @Builder(builderMethodName = "comSaldoBuilder")
  public ContaComSaldoDTO(Long id, String descricao, BigDecimal saldoInicial, BigDecimal saldoFinal) {
    super(id, descricao);
    this.saldoInicial = saldoInicial;
    this.saldoFinal = saldoFinal;
  }

}
