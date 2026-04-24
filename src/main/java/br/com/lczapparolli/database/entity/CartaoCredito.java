package br.com.lczapparolli.database.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "CARTAO_CREDITO")
public class CartaoCredito extends Conta {

  @Column(name = "dia_vencimento", nullable = false)
  private int diaVencimento;

  @Column(name = "dia_fechamento", nullable = false)
  private int diaFechamento;

  @Builder(builderMethodName = "cartaoCreditoBuilder")
  public CartaoCredito(Long id, String descricao, boolean ativo, LocalDateTime dataCriacao, LocalDateTime versao, int diaVencimento, int diaFechamento) {
    super(id, descricao, ativo, dataCriacao, versao);
    this.diaVencimento = diaVencimento;
    this.diaFechamento = diaFechamento;
  }

}
