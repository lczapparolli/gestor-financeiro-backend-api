package br.com.lczapparolli.service;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import br.com.lczapparolli.database.entity.CartaoCredito;
import br.com.lczapparolli.database.entity.Conta;
import br.com.lczapparolli.database.repository.CartaoCreditoRepository;
import br.com.lczapparolli.database.repository.ContaRepository;
import br.com.lczapparolli.dto.CartaoDTO;
import br.com.lczapparolli.exception.GerenciadorException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CartaoCreditoService {

  @Inject
  CartaoCreditoRepository cartaoCreditoRepository;

  @Inject
  ContaRepository contaRepository;

  public Stream<CartaoDTO> listarCartoes() {
    return cartaoCreditoRepository.listarAtivos()
        .map(CartaoDTO::from);
  }

  @Transactional
  public CartaoDTO inserirCartao(CartaoDTO cartaoDTO) throws GerenciadorException {
    if (Objects.isNull(cartaoDTO)) {
      throw new GerenciadorException("Os dados do cartão precisam ser preenchidos");
    }

    if (Objects.isNull(cartaoDTO.getDescricao()) || cartaoDTO.getDescricao().isEmpty()) {
      throw new GerenciadorException("A descrição do cartão precisa ser preenchida");
    }

    if (cartaoDTO.getDiaVencimento() <= 0) {
      throw new GerenciadorException("O dia de vencimento do cartão precisa ser preenchido");
    }

    if (cartaoDTO.getDiaFechamento() <= 0) {
      throw new GerenciadorException("O dia de fechamento do cartão precisa ser preenchido");
    }

    Optional<Conta> conta = contaRepository.findByDescricao(cartaoDTO.getDescricao());
    if (conta.isPresent()) {
      if (conta.get().isAtivo()) {
        throw new GerenciadorException("Já existe uma conta ou cartão com essa descrição");
      }

      // Verificar se a conta é, na verdade, um cartão
      Optional<CartaoCredito> cartaoCredito = cartaoCreditoRepository.findByIdOptional(conta.get().getId());
      if (cartaoCredito.isEmpty()) {
        throw new GerenciadorException("Existe uma conta desativada com essa descrição");
      }

      cartaoCredito.get().setAtivo(true);
      cartaoCredito.get().setDiaFechamento(cartaoDTO.getDiaFechamento());
      cartaoCredito.get().setDiaVencimento(cartaoDTO.getDiaVencimento());
      cartaoCreditoRepository.persist(cartaoCredito.get());
      return CartaoDTO.from(cartaoCredito.get());
    }

    CartaoCredito cartaoCredito = new CartaoCredito();
    cartaoCredito.setDescricao(cartaoDTO.getDescricao());
    cartaoCredito.setDiaFechamento(cartaoDTO.getDiaFechamento());
    cartaoCredito.setDiaVencimento(cartaoDTO.getDiaVencimento());
    cartaoCredito.setAtivo(true);
    cartaoCreditoRepository.persist(cartaoCredito);
    return CartaoDTO.from(cartaoCredito);
  }

  @Transactional
  public CartaoDTO atualizarCartao(Long idCartao, CartaoDTO cartaoDTO) throws GerenciadorException {
    if (Objects.isNull(cartaoDTO)) {
      throw new GerenciadorException("Os dados do cartão precisam ser preenchidos");
    }

    if (Objects.isNull(cartaoDTO.getDescricao()) || cartaoDTO.getDescricao().isBlank()) {
      throw new GerenciadorException("A descrição precisa ser preenchida");
    }

    if (cartaoDTO.getDiaVencimento() <= 0) {
      throw new GerenciadorException("O dia de vencimento do cartão precisa ser preenchido");
    }

    if (cartaoDTO.getDiaFechamento() <= 0) {
      throw new GerenciadorException("O dia de fechamento do cartão precisa ser preenchido");
    }

    if (Objects.isNull(idCartao) || idCartao.compareTo(0L) <= 0) {
      throw new GerenciadorException("O id precisa ser preenchido");
    }

    var pesquisa = contaRepository.findByDescricao(cartaoDTO.getDescricao());
    if (pesquisa.isPresent() && pesquisa.get().getId() != idCartao) {
      if (!pesquisa.get().isAtivo()) {
        throw new GerenciadorException("Já existe uma conta/cartão desativada com a mesma descrição");
      }

      throw new GerenciadorException("Já existe uma conta/cartão com a mesma descrição");
    }

    var pesquisaId = cartaoCreditoRepository.findByIdOptional(idCartao);
    if (pesquisaId.isEmpty()) {
      throw new GerenciadorException("Cartão não encontrado");
    }

    if (!pesquisaId.get().isAtivo()) {
      throw new GerenciadorException("O cartão está desativado");
    }

    var cartao = pesquisaId.get();
    cartao.setDescricao(cartaoDTO.getDescricao());
    cartao.setDiaFechamento(cartaoDTO.getDiaFechamento());
    cartao.setDiaVencimento(cartaoDTO.getDiaVencimento());

    cartaoCreditoRepository.persist(cartao);

    return CartaoDTO.from(cartao);

  }

  @Transactional
  public void desativarCartao(Long idCartao) throws GerenciadorException {
    var resultadoConsulta = cartaoCreditoRepository.findByIdOptional(idCartao);
    if (resultadoConsulta.isEmpty()) {
      throw new GerenciadorException("Cartão não encontrado");
    }

    if (!resultadoConsulta.get().isAtivo()) {
      throw new GerenciadorException("O cartão já está desativado");
    }

    CartaoCredito cartaoCredito = resultadoConsulta.get();
    cartaoCredito.setAtivo(false);

    cartaoCreditoRepository.persist(cartaoCredito);
  }

}
