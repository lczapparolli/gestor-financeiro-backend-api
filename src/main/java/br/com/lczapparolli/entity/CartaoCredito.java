package br.com.lczapparolli.entity;

import jakarta.persistence.Entity;

@Entity
public class CartaoCredito extends Conta {

  public Integer diaVencimento;
  public Integer diaFechamento;

}
