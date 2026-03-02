package br.com.lczapparolli.database.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "FATURA")
public class Fatura extends ContaPagar {

  @ManyToOne(optional = false)
  @JoinColumn(name = "id_cartao_credito", nullable = false)
  public CartaoCredito cartaoCredito;

}
