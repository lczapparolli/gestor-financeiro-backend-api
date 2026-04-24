package br.com.lczapparolli.entity;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "FATURA")
public class Fatura extends PanacheEntityBase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  public Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "id_cartao_credito", nullable = false)
  public CartaoCredito cartaoCredito;

  @ManyToOne(optional = false)
  @JoinColumn(name = "id_periodo", nullable = false)
  public Periodo periodo;

  @OneToOne(optional = false)
  @JoinColumn(name = "id_conta_pagar")
  public ContaPagar contaPagar;

  @Column(name = "ativo", nullable = false)
  public boolean ativo = true;

  @CreationTimestamp
  @Column(name = "data_criacao", nullable = false, updatable = false)
  public LocalDateTime dataCriacao;

  @Version
  @Column(name = "versao", nullable = false)
  public LocalDateTime versao;

}
