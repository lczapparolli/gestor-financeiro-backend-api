package br.com.lczapparolli.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Version;

@Entity
public class Previsao extends PanacheEntityBase {

  @EmbeddedId
  public PrevisaoId id;

  @ManyToOne
  @MapsId("categoria")
  public Categoria categoria;

  @ManyToOne
  @MapsId("periodo")
  public Periodo periodo;

  @Column(precision = 19, scale = 2)
  public BigDecimal valor;

  public LocalDateTime dataCriacao;

  @Version
  public LocalDateTime versao;

  @Embeddable
  public static class PrevisaoId implements Serializable {

    public Long categoria;
    public Long periodo;

    @Override
    public int hashCode() {
      return Objects.hash(categoria, periodo);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;

      PrevisaoId other = (PrevisaoId) obj;
      return Objects.equals(this.categoria, other.categoria) &&
          Objects.equals(this.periodo, other.periodo);
    }

    

  }

}
