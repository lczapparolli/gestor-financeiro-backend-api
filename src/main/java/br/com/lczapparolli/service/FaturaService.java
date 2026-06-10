package br.com.lczapparolli.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import br.com.lczapparolli.database.entity.CartaoCredito;
import br.com.lczapparolli.database.entity.Conta;
import br.com.lczapparolli.database.entity.Fatura;
import br.com.lczapparolli.database.repository.CategoriaRepository;
import br.com.lczapparolli.database.repository.FaturaRepository;
import br.com.lczapparolli.util.PeriodoUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class FaturaService {

  @Inject
  FaturaRepository faturaRepository;
  @Inject
  CategoriaRepository categoriaRepository;

  @Transactional
  public void atualizarFatura(CartaoCredito cartaoCredito, LocalDate periodoMovimento, BigDecimal valor) {
    // TODO: Alterar a lógica para considerar os dias de fechamento e vencimento da
    // fatura
    LocalDate periodo = PeriodoUtil.normalizarPeriodo(periodoMovimento);

    Optional<Fatura> fatura = faturaRepository.findByCartaoPeriodo(cartaoCredito.getId(), periodo);
    if (fatura.isPresent()) {
      fatura.get().setValor(fatura.get().getValor().add(valor));
      faturaRepository.persist(fatura.get());
      return;
    }

    Fatura novo = new Fatura();
    novo.setCartaoCredito(cartaoCredito);
    novo.setValor(valor);
    novo.setAtivo(true);
    novo.setVencimento(periodo.plusDays(cartaoCredito.getDiaVencimento() - 1));
    novo.setDescricao("Fatura - " + cartaoCredito.getDescricao());
    novo.setPeriodo(periodo);
    novo.setCategoria(categoriaRepository.findById(CategoriaService.CategoriasSistema.FATURA.getId()));
    faturaRepository.persist(novo);
  }

  public void atualizarFatura(Conta conta, LocalDate periodoMovimento, BigDecimal valor) {
    if (conta instanceof CartaoCredito cartaoCredito) {
      atualizarFatura(cartaoCredito, periodoMovimento, valor);
    }
  }

}
