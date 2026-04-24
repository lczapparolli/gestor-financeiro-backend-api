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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "MOVIMENTO")
public class Movimento {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "descricao")
  private String descricao;

  @Column(name = "valor", precision = 19, scale = 2)
  private BigDecimal valor;

  @Column(name = "data", nullable = false)
  private LocalDate data;

  @ManyToOne(optional = false)
  @JoinColumn(name = "id_conta", nullable = false)
  private Conta conta;

  @ManyToOne(optional = false)
  @JoinColumn(name = "id_categoria", nullable = false)
  private Categoria categoria;

  @Column(name = "periodo", nullable = false)
  private LocalDate periodo;

  @Column(name = "ativo", nullable = false)
  private boolean ativo;

  @CreationTimestamp
  @Column(name = "data_criacao", nullable = false, updatable = false)
  private LocalDateTime dataCriacao;

  @Version
  @Column(name = "versao", nullable = false)
  private LocalDateTime versao;
}
