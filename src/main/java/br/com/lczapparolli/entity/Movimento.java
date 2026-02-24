package br.com.lczapparolli.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;

@Entity
public class Movimento extends PanacheEntityBase {

  @Id
  @GeneratedValue
  public Long id;

  public String descricao;

  @Column(precision = 19, scale = 2)
  public BigDecimal valor;

  public LocalDate data;

  @ManyToOne
  public Conta conta;

  @ManyToOne
  public Categoria categoria;

  @ManyToOne
  public Periodo periodo;

  @CreationTimestamp
  public LocalDateTime dataCriacao;

  @Version
  public LocalDateTime versao;
}
