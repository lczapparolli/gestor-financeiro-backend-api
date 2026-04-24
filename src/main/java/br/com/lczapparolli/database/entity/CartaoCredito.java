package br.com.lczapparolli.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "CARTAO_CREDITO")
public class CartaoCredito extends Conta {

  @Column(name = "dia_vencimento", nullable = false)
  public Integer diaVencimento;

  @Column(name = "dia_fechamento", nullable = false)
  public Integer diaFechamento;

}
