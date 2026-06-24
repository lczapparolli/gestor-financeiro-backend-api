package br.com.lczapparolli.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import br.com.lczapparolli.database.entity.Categoria;
import br.com.lczapparolli.database.entity.Conta;
import br.com.lczapparolli.database.entity.ContaPagar;
import br.com.lczapparolli.database.entity.Movimento;
import br.com.lczapparolli.database.repository.CategoriaRepository;
import br.com.lczapparolli.database.repository.ContaPagarRepository;
import br.com.lczapparolli.database.repository.ContaRepository;
import br.com.lczapparolli.database.repository.MovimentoRepository;
import br.com.lczapparolli.dto.CategoriaDTO;
import br.com.lczapparolli.dto.ContaDTO;
import br.com.lczapparolli.dto.ContaPagarDTO;
import br.com.lczapparolli.dto.MovimentoDTO;
import br.com.lczapparolli.exception.GerenciadorException;
import br.com.lczapparolli.util.PeriodoUtil;
import io.quarkus.panache.common.Parameters;
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
  ContaPagarRepository contaPagarRepository;
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
    movimento.setContaPagar(obterContaPagar(movimentoDTO));
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
    movimento.get().setContaPagar(obterContaPagar(movimentoDTO));
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

  @Transactional
  public Stream<MovimentoDTO> inserirMovimentosLote(LocalDate periodo, Stream<String> movimentos)
      throws GerenciadorException, ParseException {
    LocalDate periodoNormalizado = PeriodoUtil.normalizarPeriodo(periodo);
    Iterator<String> iterator = movimentos.iterator();
    List<MovimentoDTO> resultado = new ArrayList<>();
    DecimalFormat format = (DecimalFormat) NumberFormat.getNumberInstance(Locale.of("pt", "BR"));
    format.setParseBigDecimal(true);

    while (iterator.hasNext()) {
      String linha = iterator.next();
      try {
        String[] campos = linha.split("\t");
        String descCategoria = campos[0];
        String descricao = campos[1];
        String data = campos[2];
        String valor = campos[3];
        String descConta = campos[4];
        Optional<Conta> conta = contaRepository.findByDescricao(descConta);
        Optional<Categoria> categoria = categoriaRepository.findByDescricao(descCategoria);
        Optional<ContaPagar> contaPagar = contaPagarRepository.find("descricao = :descricao AND periodo = :periodo",
            Parameters.with("descricao", descricao).and("periodo", periodoNormalizado)).singleResultOptional();

        MovimentoDTO inserir = MovimentoDTO.builder()
            .descricao(descricao)
            .periodo(periodo)
            .valor((BigDecimal) format.parse(valor.replace("R$ ", "")))
            .periodo(periodoNormalizado)
            .conta(ContaDTO.builder().id(conta.get().getId()).build())
            .categoria(CategoriaDTO.builder().id(categoria.get().getId()).build())
            .contaPagar(contaPagar.map(cp -> ContaPagarDTO.builder().id(cp.getId()).build()).orElse(null))
            .data(LocalDate.parse(data, DateTimeFormatter.ofPattern("dd/MM/uuuu")))
            .build();
        MovimentoDTO inserido = this.inserirMovimento(inserir);
        resultado.add(inserido);
      } catch (Exception e) {
        throw new GerenciadorException("Falha ao inserir o registro: " + linha, e);
      }
    }

    return resultado.stream();
  }

  private ContaPagar obterContaPagar(MovimentoDTO movimentoDTO) throws GerenciadorException {
    if (Objects.isNull(movimentoDTO.getContaPagar()) || Objects.isNull(movimentoDTO.getContaPagar().getId())
        || movimentoDTO.getContaPagar().getId().compareTo(0L) == 0) {
      return null;
    }

    Optional<ContaPagar> contaPagar = contaPagarRepository.findByIdOptional(movimentoDTO.getContaPagar().getId());
    if (contaPagar.isEmpty() || (contaPagar.isPresent() && !contaPagar.get().isAtivo())) {
      throw new GerenciadorException("A conta a pagar informada não foi encontrada");
    }

    LocalDate periodoNormalizado = PeriodoUtil.normalizarPeriodo(movimentoDTO.getPeriodo());
    if (!contaPagar.get().getPeriodo().equals(periodoNormalizado)) {
      throw new GerenciadorException("O período do movimento e da conta a pagar precisam ser os mesmos");
    }

    return contaPagar.get();
  }
}
