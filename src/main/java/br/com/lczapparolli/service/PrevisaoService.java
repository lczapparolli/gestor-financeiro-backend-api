package br.com.lczapparolli.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import br.com.lczapparolli.database.entity.Categoria;
import br.com.lczapparolli.database.entity.Previsao;
import br.com.lczapparolli.database.repository.CategoriaRepository;
import br.com.lczapparolli.database.repository.ContaPagarRepository;
import br.com.lczapparolli.database.repository.MovimentoRepository;
import br.com.lczapparolli.database.repository.PrevisaoRepository;
import br.com.lczapparolli.dto.CategoriaDTO;
import br.com.lczapparolli.dto.PrevisaoComSaldoDTO;
import br.com.lczapparolli.dto.PrevisaoDTO;
import br.com.lczapparolli.exception.GerenciadorException;
import br.com.lczapparolli.util.PeriodoUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class PrevisaoService {

  @Inject
  PrevisaoRepository previsaoRepository;
  @Inject
  CategoriaRepository categoriaRepository;
  @Inject
  MovimentoRepository movimentoRepository;
  @Inject
  ContaPagarRepository contaPagarRepository;

  public Stream<PrevisaoDTO> listarPrevisoes(LocalDate periodo) {
    LocalDate periodoNormalizado = PeriodoUtil.normalizarPeriodo(periodo);
    Stream<Previsao> previsoes = previsaoRepository.listarPorPeriodo(periodoNormalizado);
    Stream<Categoria> categorias = categoriaRepository.listarSemPrevisao(periodoNormalizado);

    return Stream.concat(previsoes.map(PrevisaoDTO::from),
        categorias.map(categoria -> PrevisaoDTO.builder()
            .categoria(CategoriaDTO.from(categoria))
            .periodo(periodo)
            .build()));
  }

  @Transactional
  public PrevisaoDTO inserirPrevisao(PrevisaoDTO previsaoDTO) throws GerenciadorException {
    if (Objects.isNull(previsaoDTO)) {
      throw new GerenciadorException("Os dados estão vazios");
    }

    if (Objects.isNull(previsaoDTO.getPeriodo())) {
      throw new GerenciadorException("O período precisa ser preenchido");
    }

    if (Objects.isNull(previsaoDTO.getValor())) {
      throw new GerenciadorException("O valor precisa ser preenchido");
    }

    if (Objects.isNull(previsaoDTO.getCategoria()) || Objects.isNull(previsaoDTO.getCategoria().getId())
        || previsaoDTO.getCategoria().getId().compareTo(0L) <= 0) {
      throw new GerenciadorException("A categoria precisa ser preenchida");
    }

    Optional<Categoria> categoria = categoriaRepository.findByIdOptional(previsaoDTO.getCategoria().getId());

    if (categoria.isEmpty()) {
      throw new GerenciadorException("Categoria não encontrada");
    }

    LocalDate periodoNormalizado = PeriodoUtil.normalizarPeriodo(previsaoDTO.getPeriodo());
    Optional<Previsao> previsaoExistente = previsaoRepository.findByPeriodoCategoria(periodoNormalizado,
        categoria.get().getId());
    if (previsaoExistente.isPresent()) {
      if (previsaoExistente.get().isAtivo()) {
        throw new GerenciadorException("Já existe uma previsão para essa categoria e período");
      }

      previsaoExistente.get().setAtivo(true);
      previsaoExistente.get().setValor(previsaoDTO.getValor());
      previsaoRepository.persist(previsaoExistente.get());
      return PrevisaoDTO.from(previsaoExistente.get());
    }

    Previsao previsaoNova = new Previsao();
    previsaoNova.setCategoria(categoria.get());
    // TODO: Criar uma lógica para os casos em que a previsão é cumulativa com os
    // períodos anteriores (Ex: mesadas)
    previsaoNova.setValor(previsaoDTO.getValor());
    previsaoNova.setPeriodo(periodoNormalizado);
    previsaoNova.setAtivo(true);

    previsaoRepository.persist(previsaoNova);

    return PrevisaoDTO.from(previsaoNova);
  }

  @Transactional
  public PrevisaoDTO atualizarPrevisao(Long idPrevisao, PrevisaoDTO previsaoDTO) throws GerenciadorException {
    if (Objects.isNull(idPrevisao) || idPrevisao.compareTo(0L) <= 0) {
      throw new GerenciadorException("A identificação da previsão precisa ser informada.");
    }

    if (Objects.isNull(previsaoDTO)) {
      throw new GerenciadorException("Os dados para atualização precisam ser informados");
    }

    if (Objects.isNull(previsaoDTO.getValor())) {
      throw new GerenciadorException("O valor precisa ser preenchido");
    }

    Optional<Previsao> previsao = previsaoRepository.findByIdOptional(idPrevisao);

    if (previsao.isEmpty()) {
      throw new GerenciadorException("A previsão informada não foi encontrada");
    }

    previsao.get().setValor(previsaoDTO.getValor());
    previsaoRepository.persist(previsao.get());

    return PrevisaoDTO.from(previsao.get());
  }

  @Transactional
  public void desativarPrevisao(Long idPrevisao) throws GerenciadorException {
    if (Objects.isNull(idPrevisao) || idPrevisao.compareTo(0L) <= 0) {
      throw new GerenciadorException("A identificação da previsão precisa ser informada");
    }

    Optional<Previsao> previsao = previsaoRepository.findByIdOptional(idPrevisao);

    if (previsao.isEmpty()) {
      throw new GerenciadorException("A previsão informada não foi encontrada");
    }

    previsao.get().setAtivo(false);
    previsaoRepository.persist(previsao.get());
  }

  @Transactional
  public Stream<PrevisaoDTO> clonarPrevisoes(LocalDate periodoOrigem, LocalDate periodoDestino)
      throws GerenciadorException {
    LocalDate periodoOrigemNormalizado = PeriodoUtil.normalizarPeriodo(periodoOrigem);
    LocalDate periodoDestinoNormalizado = PeriodoUtil.normalizarPeriodo(periodoDestino);
    long delta = ChronoUnit.MONTHS.between(periodoOrigemNormalizado, periodoDestinoNormalizado);

    if (periodoOrigemNormalizado.isEqual(periodoDestinoNormalizado)) {
      throw new GerenciadorException("Os períodos de origem e destino não podem ser os mesmos");
    }

    Stream<Previsao> previsoesOrigem = previsaoRepository.listarPorPeriodo(periodoOrigemNormalizado);
    Iterator<Previsao> iterator = previsoesOrigem.iterator();
    List<PrevisaoDTO> inseridas = new ArrayList<>();
    while (iterator.hasNext()) {
      Previsao previsao = iterator.next();
      Optional<PrevisaoDTO> inserido = clonar(previsao, periodoDestinoNormalizado, delta);
      if (inserido.isPresent()) {
        inseridas.add(inserido.get());
      }
    }

    return inseridas.stream();
  }

  public Stream<PrevisaoComSaldoDTO> listarComSaldo(LocalDate periodo) {
    LocalDate periodoNormalizado = PeriodoUtil.normalizarPeriodo(periodo);

    List<Previsao> previsoes = Stream.concat(
        previsaoRepository.listarPorPeriodo(periodoNormalizado),
        categoriaRepository.listarSemPrevisao(periodoNormalizado)
            .map(categoria -> Previsao.builder()
                .id(null)
                .categoria(categoria)
                .valor(BigDecimal.ZERO)
                .periodo(periodoNormalizado)
                .build()))
        .toList();
    Iterator<Previsao> iterator = previsoes.iterator();
    List<PrevisaoComSaldoDTO> resultado = new ArrayList<>();
    while (iterator.hasNext()) {
      Previsao previsao = iterator.next();
      BigDecimal valorContasPagar = Optional
          .ofNullable(
              contaPagarRepository.valorPorCategoriaPeriodo(periodoNormalizado, previsao.getCategoria().getId()))
          .orElse(BigDecimal.ZERO);
      BigDecimal valorPrevisao = previsao.getValor().add(valorContasPagar);
      BigDecimal saldoPeriodo = Optional
          .ofNullable(movimentoRepository.saldoCategoriaNoPeriodo(previsao.getCategoria().getId(),
              periodoNormalizado))
          .orElse(BigDecimal.ZERO);

      if (previsao.getCategoria().isCumulativo()) {
        BigDecimal saldoAnterior = Optional
            .ofNullable(
                movimentoRepository.saldoCategoriaAteOPeriodo(previsao.getCategoria().getId(), periodoNormalizado))
            .orElse(BigDecimal.ZERO);

        BigDecimal previsaoAnterior = Optional
            .ofNullable(previsaoRepository.previsaoAteOPeriodo(periodoNormalizado, previsao.getCategoria().getId()))
            .orElse(BigDecimal.ZERO);

        valorPrevisao = previsaoAnterior.add(valorPrevisao).subtract(saldoAnterior);
      }
      resultado.add(PrevisaoComSaldoDTO.comSaldoBuilder()
          .id(previsao.getId())
          .categoria(CategoriaDTO.from(previsao.getCategoria()))
          .periodo(periodoNormalizado)
          .valor(valorPrevisao)
          .saldo(saldoPeriodo)
          .build());
    }

    return resultado.stream();
  }

  private Optional<PrevisaoDTO> clonar(Previsao previsao, LocalDate periodoDestino, long delta) {
    try {
      PrevisaoDTO novo = PrevisaoDTO.from(previsao);
      novo.setPeriodo(periodoDestino);
      novo.setId(null);

      return Optional.of(inserirPrevisao(novo));
    } catch (GerenciadorException exception) {
      return Optional.empty();
    }
  }

}
