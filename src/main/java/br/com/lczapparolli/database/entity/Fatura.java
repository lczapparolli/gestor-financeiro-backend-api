package br.com.lczapparolli.database.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "FATURA")
public class Fatura extends ContaPagar {

  @ManyToOne(optional = false)
  @JoinColumn(name = "id_cartao_credito", nullable = false)
  private CartaoCredito cartaoCredito;

  @Builder(builderMethodName = "faturaBuilder")
  public Fatura(Long id, LocalDate periodo, Categoria categoria, LocalDate vencimento, BigDecimal valor, boolean ativo,
      LocalDateTime dataCriacao, LocalDateTime versao, CartaoCredito cartaoCredito) {
    super(id, periodo, categoria, vencimento, valor, ativo, dataCriacao, versao);
    this.cartaoCredito = cartaoCredito;
  }

  

}
