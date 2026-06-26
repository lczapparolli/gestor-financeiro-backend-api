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
public class PrevisaoComSaldoDTO extends PrevisaoDTO {

  private BigDecimal saldo;

  @Builder(builderMethodName = "comSaldoBuilder")
  public PrevisaoComSaldoDTO(Long id, CategoriaDTO categoria, LocalDate periodo, BigDecimal valor, BigDecimal saldo) {
    super(id, categoria, periodo, valor);
    this.saldo = saldo;
  }
}
