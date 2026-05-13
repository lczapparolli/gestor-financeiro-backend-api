package br.com.lczapparolli.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.lczapparolli.database.entity.Movimento;
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
public class MovimentoDTO {

  private Long id;
  private String descricao;
  private ContaDTO conta;
  private CategoriaDTO categoria;
  private LocalDate data;
  private LocalDate periodo;
  private BigDecimal valor;

  public static MovimentoDTO from(Movimento movimento) {
    return MovimentoDTO.builder()
        .id(movimento.getId())
        .descricao(movimento.getDescricao())
        .data(movimento.getData())
        .periodo(movimento.getPeriodo())
        .valor(movimento.getValor())
        .conta(ContaDTO.from(movimento.getConta()))
        .categoria(CategoriaDTO.from(movimento.getCategoria()))
        .build();
  }

}
