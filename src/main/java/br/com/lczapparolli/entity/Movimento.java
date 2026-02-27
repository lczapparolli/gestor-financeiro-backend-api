package br.com.lczapparolli.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "MOVIMENTO")
public class Movimento extends PanacheEntityBase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  public Long id;

  @Column(name = "descricao")
  public String descricao;

  @Column(name = "valor", precision = 19, scale = 2)
  public BigDecimal valor;

  @Column(name = "data", nullable = false)
  public LocalDate data;

  @ManyToOne(optional = false)
  @JoinColumn(name = "id_conta", nullable = false)
  public Conta conta;

  @ManyToOne(optional = false)
  @JoinColumn(name = "id_categoria", nullable = false)
  public Categoria categoria;

  @ManyToOne(optional = false)
  @JoinColumn(name = "id_periodo", nullable = false)
  public Periodo periodo;

  @Column(name = "ativo", nullable = false)
  public boolean ativo = true;

  @CreationTimestamp
  @Column(name = "data_criacao", nullable = false, updatable = false)
  public LocalDateTime dataCriacao;

  @Version
  @Column(name = "versao", nullable = false)
  public LocalDateTime versao;
}
