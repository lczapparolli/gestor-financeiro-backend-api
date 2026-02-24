package br.com.lczapparolli.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
public class Fatura extends PanacheEntityBase {

  @EmbeddedId
  public FaturaId id;

  @OneToOne
  public ContaPagar contaPagar;

  @CreationTimestamp
  public LocalDateTime dataCriacao;

  @jakarta.persistence.Version
  public LocalDateTime versao;

  @Embeddable
  public static class FaturaId implements Serializable {

    public Long conta;
    public Long periodo;

    @Override
    public int hashCode() {
      return Objects.hash(this.conta, this.periodo);
    }
    
    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;

      FaturaId other = (FaturaId) obj;
      return Objects.equals(this.conta, other.conta) &&
          Objects.equals(this.periodo, other.periodo);
    }
  }

}
