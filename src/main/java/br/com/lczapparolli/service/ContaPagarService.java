package br.com.lczapparolli.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import br.com.lczapparolli.database.entity.Categoria;
import br.com.lczapparolli.database.entity.ContaPagar;
import br.com.lczapparolli.database.repository.CategoriaRepository;
import br.com.lczapparolli.database.repository.ContaPagarRepository;
import br.com.lczapparolli.dto.ContaPagarDTO;
import br.com.lczapparolli.exception.GerenciadorException;
import br.com.lczapparolli.util.PeriodoUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ContaPagarService {

  @Inject
  ContaPagarRepository contaPagarRepository;
  @Inject
  CategoriaRepository categoriaRepository;

  public Stream<ContaPagarDTO> listarContasPagar(LocalDate periodo) {
    LocalDate periodoNormalizado = PeriodoUtil.normalizarPeriodo(periodo);

    return contaPagarRepository.listarAtivasPorPeriodo(periodoNormalizado)
        .map(ContaPagarDTO::from);
  }

  @Transactional
  public ContaPagarDTO inserirContaPagar(ContaPagarDTO contaPagarDTO) throws GerenciadorException {
    if (Objects.isNull(contaPagarDTO)) {
      throw new GerenciadorException("É necessário informar os dados da conta a pagar");
    }

    if (Objects.isNull(contaPagarDTO.getDescricao()) || contaPagarDTO.getDescricao().isBlank()) {
      throw new GerenciadorException("É necessário informar a descrição");
    }

    if (Objects.isNull(contaPagarDTO.getPeriodo())) {
      throw new GerenciadorException("O período precisa ser preenchido");
    }

    if (Objects.isNull(contaPagarDTO.getValor()) || contaPagarDTO.getValor().compareTo(BigDecimal.ZERO) <= 0) {
      throw new GerenciadorException("O valor da conta precisa ser preenchido");
    }

    if (Objects.isNull(contaPagarDTO.getCategoria()) || Objects.isNull(contaPagarDTO.getCategoria().getId())
        || contaPagarDTO.getCategoria().getId().compareTo(0L) <= 0) {
      throw new GerenciadorException("A categoria precisa ser preenchida");
    }

    Optional<Categoria> categoria = categoriaRepository.findByIdOptional(contaPagarDTO.getCategoria().getId());
    if (categoria.isEmpty() || !categoria.get().isAtivo()) {
      throw new GerenciadorException("A categoria informada não foi encontrada");
    }

    LocalDate periodoNormalizado = PeriodoUtil.normalizarPeriodo(contaPagarDTO.getPeriodo());
    Optional<ContaPagar> pesquisa = contaPagarRepository.pesquisarPorPeriodoDescricao(periodoNormalizado,
        contaPagarDTO.getDescricao());
    if (pesquisa.isPresent()) {
      if (pesquisa.get().isAtivo()) {
        throw new GerenciadorException("Já existe uma conta a pagar com a mesma descrição");
      }

      pesquisa.ifPresent(cc -> {
        cc.setAtivo(true);
        cc.setCategoria(categoria.get());
        cc.setValor(contaPagarDTO.getValor());
        cc.setVencimento(contaPagarDTO.getVencimento());
      });

      contaPagarRepository.persist(pesquisa.get());
      return ContaPagarDTO.from(pesquisa.get());
    }

    ContaPagar contaPagar = ContaPagar.builder()
        .ativo(true)
        .descricao(contaPagarDTO.getDescricao())
        .periodo(periodoNormalizado)
        .vencimento(contaPagarDTO.getVencimento())
        .valor(contaPagarDTO.getValor())
        .categoria(categoria.get())
        .build();

    contaPagarRepository.persist(contaPagar);
    return ContaPagarDTO.from(contaPagar);
  }

  @Transactional
  public ContaPagarDTO atualizarContaPagar(Long idContaPagar, ContaPagarDTO contaPagarDTO) throws GerenciadorException {
    if (Objects.isNull(contaPagarDTO)) {
      throw new GerenciadorException("É necessário informar os dados da conta a pagar");
    }

    if (Objects.isNull(contaPagarDTO.getDescricao()) || contaPagarDTO.getDescricao().isBlank()) {
      throw new GerenciadorException("É necessário informar a descrição");
    }

    if (Objects.isNull(contaPagarDTO.getValor()) || contaPagarDTO.getValor().compareTo(BigDecimal.ZERO) <= 0) {
      throw new GerenciadorException("O valor da conta precisa ser preenchido");
    }

    if (Objects.isNull(idContaPagar) || idContaPagar.compareTo(0L) <= 0) {
      throw new GerenciadorException("A identificação da conta a pagar precisa ser preenchida");
    }

    if (Objects.isNull(contaPagarDTO.getCategoria()) || Objects.isNull(contaPagarDTO.getCategoria().getId())
        || contaPagarDTO.getCategoria().getId().compareTo(0L) <= 0) {
      throw new GerenciadorException("A categoria precisa ser preenchida");
    }

    Optional<Categoria> categoria = categoriaRepository.findByIdOptional(contaPagarDTO.getCategoria().getId());
    if (categoria.isEmpty() || !categoria.get().isAtivo()) {
      throw new GerenciadorException("A categoria informada não foi encontrada");
    }

    Optional<ContaPagar> pesquisaId = contaPagarRepository.findByIdOptional(idContaPagar);
    if (pesquisaId.isEmpty()) {
      throw new GerenciadorException("A conta a pagar informada não está ativa");
    }

    if (!pesquisaId.get().isAtivo()) {
      throw new GerenciadorException("A conta a pagar informada está desativada");
    }

    Optional<ContaPagar> pesquisa = contaPagarRepository.pesquisarPorPeriodoDescricao(pesquisaId.get().getPeriodo(),
        contaPagarDTO.getDescricao());
    if (pesquisa.isPresent() && pesquisa.get().getId() != idContaPagar) {
      if (!pesquisa.get().isAtivo()) {
        throw new GerenciadorException("Já existe uma conta a pagar desativada com a mesma descrição");
      }

      throw new GerenciadorException("Já existe uma conta a pagar com a mesma descrição");
    }

    pesquisaId.ifPresent(contaPagar -> {
      contaPagar.setCategoria(categoria.get());
      contaPagar.setValor(contaPagarDTO.getValor());
      contaPagar.setDescricao(contaPagarDTO.getDescricao());
      contaPagar.setVencimento(contaPagarDTO.getVencimento());
    });

    contaPagarRepository.persist(pesquisaId.get());
    return ContaPagarDTO.from(pesquisaId.get());
  }

  @Transactional
  public void desativarContaPagar(Long idContaPagar) throws GerenciadorException {
    if (Objects.isNull(idContaPagar) || idContaPagar.compareTo(0L) <= 0) {
      throw new GerenciadorException("A identificação da conta a pagar precisa ser preenchida");
    }

    Optional<ContaPagar> pesquisaId = contaPagarRepository.findByIdOptional(idContaPagar);
    if (pesquisaId.isEmpty()) {
      throw new GerenciadorException("A conta a pagar informada não foi encontrada");
    }

    if (!pesquisaId.get().isAtivo()) {
      throw new GerenciadorException("A conta a pagar informada já está desativada");
    }

    pesquisaId.get().setAtivo(false);
    contaPagarRepository.persist(pesquisaId.get());
  }

  @Transactional
  public Stream<ContaPagarDTO> clonarContasPagar(LocalDate periodoOrigem, LocalDate periodoDestino)
      throws GerenciadorException {
    LocalDate periodoOrigemNormalizado = PeriodoUtil.normalizarPeriodo(periodoOrigem);
    LocalDate periodoDestinoNormalizado = PeriodoUtil.normalizarPeriodo(periodoDestino);
    long delta = ChronoUnit.MONTHS.between(periodoOrigemNormalizado, periodoDestinoNormalizado);

    if (periodoOrigemNormalizado.isEqual(periodoDestinoNormalizado)) {
      throw new GerenciadorException("Os períodos de origem e destino não podem ser os mesmos");
    }

    Stream<ContaPagar> contasPagarOrigem = contaPagarRepository.listarAtivasPorPeriodo(periodoOrigemNormalizado);
    Iterator<ContaPagar> iterator = contasPagarOrigem.iterator();
    List<ContaPagarDTO> inseridas = new ArrayList<>();
    while (iterator.hasNext()) {
      ContaPagar contaPagar = iterator.next();
      Optional<ContaPagarDTO> inserido = clonar(contaPagar, periodoDestinoNormalizado, delta);
      if (inserido.isPresent()) {
        inseridas.add(inserido.get());
      }
    }

    return inseridas.stream();
  }

  private Optional<ContaPagarDTO> clonar(ContaPagar contaPagar, LocalDate periodoDestino, long delta) {
    try {
      ContaPagarDTO novo = ContaPagarDTO.from(contaPagar);
      novo.setPeriodo(periodoDestino);
      novo.setId(null);
      if (!Objects.isNull(contaPagar.getVencimento())) {
        LocalDate novoVencimento = Stream
            .of(contaPagar.getVencimento().plusMonths(delta), periodoDestino.plusMonths(1).minusDays(1))
            .min(Comparator.naturalOrder())
            .get();
        novo.setVencimento(novoVencimento);
      }

      return Optional.of(inserirContaPagar(novo));
    } catch (GerenciadorException exception) {
      return Optional.empty();
    }
  }
}
