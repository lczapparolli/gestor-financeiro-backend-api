package br.com.lczapparolli.entity;

import java.time.LocalDateTime;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

@Entity
public class Conta extends PanacheEntityBase {

  @Id
  @GeneratedValue
  public Long id;
  public String descricao;
  public LocalDateTime dataCriacao;
  @Version
  public LocalDateTime versao;

}