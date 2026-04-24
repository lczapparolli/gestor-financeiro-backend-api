package br.com.lczapparolli.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "PERIODO")
public class Periodo extends PanacheEntityBase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  public Long id;

  @Column(name = "data_inicio", nullable = false)
  public LocalDate dataInicio;
  
  @Column(name = "data_fim", nullable = false)
  public LocalDate dataFim;

  @Column(name = "ativo", nullable = false)
  public boolean ativo = true;

  @CreationTimestamp
  @Column(name = "data_criacao", nullable = false, updatable = false)
  public LocalDateTime dataCriacao;

  @Version
  @Column(name = "versao", nullable = false)
  public LocalDateTime versao;

}
