package br.com.lczapparolli.exception;

public class GerenciadorException extends Exception {

  public GerenciadorException(String message) {
    super(message);
  }

  public GerenciadorException(String message, Throwable cause) {
    super(message, cause);
  }

}
