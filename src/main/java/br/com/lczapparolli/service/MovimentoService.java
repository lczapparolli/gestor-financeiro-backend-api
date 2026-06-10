package br.com.lczapparolli.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import br.com.lczapparolli.database.entity.Categoria;
import br.com.lczapparolli.database.entity.Conta;
import br.com.lczapparolli.database.entity.Movimento;
import br.com.lczapparolli.database.repository.CategoriaRepository;
import br.com.lczapparolli.database.repository.ContaRepository;
import br.com.lczapparolli.database.repository.MovimentoRepository;
import br.com.lczapparolli.dto.MovimentoDTO;
import br.com.lczapparolli.exception.GerenciadorException;
import br.com.lczapparolli.util.PeriodoUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class MovimentoService {

  @Inject
  MovimentoRepository movimentoRepository;
  @Inject
  ContaRepository contaRepository;
  @Inject
  CategoriaRepository categoriaRepository;
  @Inject
  FaturaService faturaService;

  public Stream<MovimentoDTO> listarMovimentos(LocalDate periodo) {
    LocalDate periodoNormalizado = PeriodoUtil.normalizarPeriodo(periodo);

    return movimentoRepository.listarPorPeriodo(periodoNormalizado)
        .map(MovimentoDTO::from);
  }

  @Transactional
  public MovimentoDTO inserirMovimento(MovimentoDTO movimentoDTO) throws GerenciadorException {
    if (Objects.isNull(movimentoDTO)) {
      throw new GerenciadorException("O movimento precisa ser preenchido");
    }

    if (Objects.isNull(movimentoDTO.getDescricao())) {
      throw new GerenciadorException("A descrição precisa ser preenchida");
    }

    if (Objects.isNull(movimentoDTO.getValor())) {
      throw new GerenciadorException("O valor precisa ser preenchido");
    }

    if (Objects.isNull(movimentoDTO.getData())) {
      throw new GerenciadorException("A data precisa ser preenchida");
    }

    if (Objects.isNull(movimentoDTO.getPeriodo())) {
      throw new GerenciadorException("O período precisa ser preenchido");
    }

    if (Objects.isNull(movimentoDTO.getConta()) || Objects.isNull(movimentoDTO.getConta().getId())
        || movimentoDTO.getConta().getId().compareTo(0L) <= 0) {
      throw new GerenciadorException("A conta precisa ser preenchida");
    }

    if (Objects.isNull(movimentoDTO.getCategoria()) || Objects.isNull(movimentoDTO.getCategoria().getId())
        || movimentoDTO.getCategoria().getId().compareTo(0L) <= 0) {
      throw new GerenciadorException("A categoria precisa ser preenchida");
    }

    Optional<Conta> conta = contaRepository.findByIdOptional(movimentoDTO.getConta().getId());
    if (conta.isEmpty() || (conta.isPresent() && !conta.get().isAtivo())) {
      throw new GerenciadorException("A conta informada não foi encontrada");
    }

    Optional<Categoria> categoria = categoriaRepository.findByIdOptional(movimentoDTO.getCategoria().getId());
    if (categoria.isEmpty() || (categoria.isPresent() && !categoria.get().isAtivo())) {
      throw new GerenciadorException("A categoria informada não foi encontrada");
    }

    Movimento movimento = new Movimento();
    movimento.setDescricao(movimentoDTO.getDescricao());
    movimento.setData(movimentoDTO.getData());
    // TODO: Tratar o período com base na data. Caso a conta seja um cartão,
    // considera também a data de fechamento da fatura
    movimento.setPeriodo(PeriodoUtil.normalizarPeriodo(movimentoDTO.getPeriodo()));
    movimento.setValor(movimentoDTO.getValor());
    movimento.setConta(conta.get());
    movimento.setCategoria(categoria.get());
    movimento.setAtivo(true);

    movimentoRepository.persist(movimento);
    faturaService.atualizarFatura(movimento.getConta(), movimento.getPeriodo(), movimento.getValor());

    return MovimentoDTO.from(movimento);
  }

  @Transactional
  public MovimentoDTO atualizarMovimento(Long id, MovimentoDTO movimentoDTO) throws GerenciadorException {
    if (Objects.isNull(movimentoDTO)) {
      throw new GerenciadorException("O movimento precisa ser preenchido");
    }

    if (Objects.isNull(movimentoDTO.getDescricao())) {
      throw new GerenciadorException("A descrição precisa ser preenchida");
    }

    if (Objects.isNull(movimentoDTO.getValor())) {
      throw new GerenciadorException("O valor precisa ser preenchido");
    }

    if (Objects.isNull(movimentoDTO.getData())) {
      throw new GerenciadorException("A data precisa ser preenchida");
    }

    if (Objects.isNull(movimentoDTO.getConta()) || Objects.isNull(movimentoDTO.getConta().getId())
        || movimentoDTO.getConta().getId().compareTo(0L) <= 0) {
      throw new GerenciadorException("A conta precisa ser preenchida");
    }

    if (Objects.isNull(movimentoDTO.getCategoria()) || Objects.isNull(movimentoDTO.getCategoria().getId())
        || movimentoDTO.getCategoria().getId().compareTo(0L) <= 0) {
      throw new GerenciadorException("A categoria precisa ser preenchida");
    }

    Optional<Conta> conta = contaRepository.findByIdOptional(movimentoDTO.getConta().getId());
    if (conta.isEmpty() || (conta.isPresent() && !conta.get().isAtivo())) {
      throw new GerenciadorException("A conta informada não foi encontrada");
    }

    Optional<Categoria> categoria = categoriaRepository.findByIdOptional(movimentoDTO.getCategoria().getId());
    if (categoria.isEmpty() || (categoria.isPresent() && !categoria.get().isAtivo())) {
      throw new GerenciadorException("A categoria informada não foi encontrada");
    }

    if (Objects.isNull(id) || id.compareTo(0L) <= 0) {
      throw new GerenciadorException("A identificação do movimento precisa ser informada");
    }

    Optional<Movimento> movimento = movimentoRepository.findByIdOptional(id);
    if (movimento.isEmpty()) {
      throw new GerenciadorException("O movimento não foi encontrado");
    }

    BigDecimal diferencaValor = movimentoDTO.getValor().subtract(movimento.get().getValor());
    movimento.get().setDescricao(movimentoDTO.getDescricao());
    movimento.get().setValor(movimentoDTO.getValor());
    movimento.get().setData(movimentoDTO.getData());
    movimento.get().setConta(conta.get());
    movimento.get().setCategoria(categoria.get());
    movimento.get().setAtivo(true);

    movimentoRepository.persist(movimento.get());
    faturaService.atualizarFatura(movimento.get().getConta(),
        movimento.get().getPeriodo(), diferencaValor);

    return MovimentoDTO.from(movimento.get());
  }

  @Transactional
  public void desativarMovimento(Long id) throws GerenciadorException {
    if (Objects.isNull(id) || id.compareTo(0L) <= 0) {
      throw new GerenciadorException("A identificação do movimento precisa ser informada");
    }

    Optional<Movimento> movimento = movimentoRepository.findByIdOptional(id);
    if (movimento.isEmpty()) {
      throw new GerenciadorException("O movimento não foi encontrado");
    }

    if (!movimento.get().isAtivo()) {
      throw new GerenciadorException("O mocimento informado já está desativado");
    }

    movimento.get().setAtivo(false);
    movimentoRepository.persist(movimento.get());
    faturaService.atualizarFatura(movimento.get().getConta(), movimento.get().getData(),
        movimento.get().getValor().negate());
  }
}
