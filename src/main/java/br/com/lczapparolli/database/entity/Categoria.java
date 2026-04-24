package br.com.lczapparolli.database.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "CATEGORIA")
public class Categoria {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  
  @Column(name = "descricao", nullable = false)
  private String descricao;

  @Column(name = "ativo", nullable = false)
  private boolean ativo;
  
  @CreationTimestamp
  @Column(name = "data_criacao", nullable = false, updatable = false)
  private LocalDateTime dataCriacao;
  
  @Version
  @Column(name = "versao", nullable = false)
  private LocalDateTime versao;

}
