package br.com.lczapparolli.database.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "CONTA_PAGAR")
public class ContaPagar {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  public Long id;

  @Column(name = "periodo", nullable = false)
  public LocalDate periodo;

  @ManyToOne(optional = false)
  @JoinColumn(name = "id_categoria", nullable = false)
  public Categoria categoria;

  @Column(name = "vencimento", nullable = false)
  public LocalDate vencimento;

  @Column(name = "valor", nullable = false, precision = 19, scale = 2)
  public BigDecimal valor;

  @Column(name = "ativo", nullable = false)
  public boolean ativo = true;

  @CreationTimestamp
  @Column(name = "data_criacao", nullable = false, updatable = false)
  public LocalDateTime dataCriacao;

  @Version
  @Column(name = "versao", nullable = false)
  public LocalDateTime versao;

}
