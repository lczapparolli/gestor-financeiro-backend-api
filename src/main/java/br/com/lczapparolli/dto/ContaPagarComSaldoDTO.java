package br.com.lczapparolli.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContaPagarComSaldoDTO extends ContaPagarDTO {

  private BigDecimal saldo;

  @Builder(builderMethodName = "comSaldoBuilder")
  public ContaPagarComSaldoDTO(Long id, String descricao, LocalDate periodo, LocalDate vencimento, BigDecimal valor,
      CategoriaDTO categoria, BigDecimal saldo) {
    super(id, descricao, periodo, vencimento, valor, categoria);
    this.saldo = saldo;
  }

}
