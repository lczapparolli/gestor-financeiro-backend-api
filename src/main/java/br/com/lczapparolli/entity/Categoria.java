package br.com.lczapparolli.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

@Entity
public class Categoria extends PanacheEntityBase {
  
  @Id
  @GeneratedValue
  public Long id;
  
  public String descricao;
  
  @CreationTimestamp
  public LocalDateTime dataCriacao;
  
  @Version
  public LocalDateTime versao;

}
