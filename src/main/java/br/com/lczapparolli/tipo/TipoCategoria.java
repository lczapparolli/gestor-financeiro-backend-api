package br.com.lczapparolli.tipo;

import java.util.Optional;
import java.util.stream.Stream;

public enum TipoCategoria {

  RECEITAS, ESSENCIAIS, DIVERSAO, IMPREVISTOS, OUTROS;

  public static Optional<TipoCategoria> fromName(String tipo) {
    return Stream.of(values()).filter(tc -> tc.name().toLowerCase().equals(tipo.toLowerCase())).findFirst();
  }

}
