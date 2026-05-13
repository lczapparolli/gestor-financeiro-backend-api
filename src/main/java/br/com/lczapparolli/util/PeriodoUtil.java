package br.com.lczapparolli.util;

import java.time.LocalDate;

public class PeriodoUtil {

  public static LocalDate normalizarPeriodo(LocalDate periodo) {
    return LocalDate.of(periodo.getYear(), periodo.getMonth(), 1);
  }

  public static LocalDate fromAnoMes(String ano, String mes) {
    return LocalDate.of(Integer.parseInt(ano), Integer.parseInt(mes), 1);
  }

}
