package br.com.lczapparolli.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import br.com.lczapparolli.database.entity.Conta;
import br.com.lczapparolli.database.repository.CartaoCreditoRepository;
import br.com.lczapparolli.database.repository.ContaRepository;
import br.com.lczapparolli.database.repository.MovimentoRepository;
import br.com.lczapparolli.dto.ContaComSaldoDTO;
import br.com.lczapparolli.dto.ContaDTO;
import br.com.lczapparolli.exception.GerenciadorException;
import br.com.lczapparolli.exception.ValidacaoException;
import br.com.lczapparolli.util.PeriodoUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ContaService {

  @Inject
  ContaRepository contaRepository;

  @Inject
  CartaoCreditoRepository cartaoCreditoRepository;

  @Inject
  MovimentoRepository movimentoRepository;

  public Stream<ContaDTO> listarContas(boolean incluirCartoes) {
    Stream<Conta> listaContas;
    if (incluirCartoes) {
      listaContas = contaRepository.listarAtivas();
    } else {
      listaContas = contaRepository.listarAtivasSemCartoes();
    }

    return listaContas.map(ContaDTO::from);
  }

  public Stream<ContaComSaldoDTO> listarContasComSaldo(LocalDate periodo) throws GerenciadorException {
    LocalDate periodoNormalizado = PeriodoUtil.normalizarPeriodo(periodo);
    List<Conta> listaContas = contaRepository.listarAtivasSemCartoes().toList();
    Iterator<Conta> iterator = listaContas.iterator();
    List<ContaComSaldoDTO> resultado = new ArrayList<>();
    while (iterator.hasNext()) {
      Conta conta = iterator.next();

      BigDecimal saldoInicial = Optional
          .ofNullable(movimentoRepository.saldoContaAteOPeriodo(conta.getId(), periodoNormalizado))
          .orElse(BigDecimal.ZERO);
      BigDecimal saldoFinal = Optional
          .ofNullable(movimentoRepository.saldoContaNoPeriodo(conta.getId(), periodoNormalizado))
          .orElse(BigDecimal.ZERO);
      resultado.add(ContaComSaldoDTO.comSaldoBuilder()
          .id(conta.getId())
          .descricao(conta.getDescricao())
          .saldoInicial(saldoInicial)
          .saldoFinal(saldoInicial.add(saldoFinal))
          .build());
    }

    return resultado.stream();
  }

  public Optional<ContaDTO> obterConta(Long idConta) {
    return contaRepository.findByIdOptional(idConta).map(ContaDTO::from);
  }

  @Transactional
  public ContaDTO inserirConta(ContaDTO contaDTO) throws GerenciadorException {
    if (Objects.isNull(contaDTO)) {
      throw new ValidacaoException("Os dados estão vazios");
    }

    if (Objects.isNull(contaDTO.getDescricao()) || contaDTO.getDescricao().isBlank()) {
      throw new ValidacaoException("A descrição precisa ser preenchida");
    }

    var pesquisa = contaRepository.findByDescricao(contaDTO.getDescricao());
    if (pesquisa.isPresent()) {
      if (pesquisa.get().isAtivo()) {
        throw new ValidacaoException("Já existe uma conta com a mesma descrição");
      }

      var cartaoCredito = cartaoCreditoRepository.findByIdOptional(pesquisa.get().getId());
      if (cartaoCredito.isPresent()) {
        throw new ValidacaoException("Já existe um cartão com a mesma descrição");
      }

      var conta = pesquisa.get();
      conta.setAtivo(true);
      contaRepository.persist(conta);
      return ContaDTO.from(conta);
    }

    var conta = Conta.builder()
        .descricao(contaDTO.getDescricao())
        .ativo(true)
        .build();

    contaRepository.persist(conta);

    return ContaDTO.from(conta);
  }

  @Transactional
  public ContaDTO atualizarConta(Long idConta, ContaDTO contaDTO) throws GerenciadorException {
    if (Objects.isNull(contaDTO)) {
      throw new ValidacaoException("Os dados estão vazios");
    }

    if (Objects.isNull(contaDTO.getDescricao()) || contaDTO.getDescricao().isBlank()) {
      throw new ValidacaoException("A descrição precisa ser preenchida");
    }

    if (Objects.isNull(idConta) || idConta.compareTo(0L) <= 0) {
      throw new ValidacaoException("O id precisa ser preenchido");
    }

    var pesquisa = contaRepository.findByDescricao(contaDTO.getDescricao());
    if (pesquisa.isPresent() && pesquisa.get().getId() != idConta) {
      if (!pesquisa.get().isAtivo()) {
        throw new ValidacaoException("Já existe uma conta desativada com a mesma descrição");
      }

      throw new ValidacaoException("Já existe uma conta com a mesma descrição");
    }

    var pesquisaId = contaRepository.findByIdOptional(idConta);
    if (pesquisaId.isEmpty()) {
      throw new ValidacaoException("Conta não encontrada");
    }

    var cartaoCredito = cartaoCreditoRepository.findByIdOptional(idConta);
    if (cartaoCredito.isPresent()) {
      throw new ValidacaoException("Não é possível alterar porque esse é um cartão de crédito");
    }

    if (!pesquisaId.get().isAtivo()) {
      throw new ValidacaoException("A conta está desativada");
    }

    var conta = pesquisaId.get();
    conta.setDescricao(contaDTO.getDescricao());

    contaRepository.persist(conta);

    return ContaDTO.from(conta);
  }

  @Transactional
  public void desativarConta(Long id) throws GerenciadorException {
    var resultadoConsulta = contaRepository.findByIdOptional(id);
    if (resultadoConsulta.isEmpty()) {
      throw new ValidacaoException("Conta não encontrada");
    }

    if (!resultadoConsulta.get().isAtivo()) {
      throw new ValidacaoException("A conta já está desativada");
    }

    Conta conta = resultadoConsulta.get();
    conta.setAtivo(false);

    contaRepository.persist(conta);
  }

}
