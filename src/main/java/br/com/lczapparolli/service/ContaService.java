package br.com.lczapparolli.service;

import java.util.Objects;

import br.com.lczapparolli.database.entity.Conta;
import br.com.lczapparolli.database.repository.ContaRepository;
import br.com.lczapparolli.dto.ContaDTO;
import br.com.lczapparolli.exception.GerenciadorException;
import br.com.lczapparolli.exception.ValidacaoException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ContaService {

  @Inject
  ContaRepository contaRepository;

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
      if (!pesquisa.get().isAtivo()) {
        throw new ValidacaoException("Já existe uma conta desativada com a mesma descrição");
      }

      throw new ValidacaoException("Já existe uma conta com a mesma descrição");
    }

    var conta = Conta.builder()
        .descricao(contaDTO.getDescricao())
        .ativo(true)
        .build();

    contaRepository.persist(conta);

    contaDTO.setId(conta.getId());

    return contaDTO;
  }

  @Transactional
  public ContaDTO atualizarConta(ContaDTO contaDTO) throws ValidacaoException {
    if (Objects.isNull(contaDTO)) {
      throw new ValidacaoException("Os dados estão vazios");
    }

    if (Objects.isNull(contaDTO.getDescricao()) || contaDTO.getDescricao().isBlank()) {
      throw new ValidacaoException("A descrição precisa ser preenchida");
    }

    if (Objects.isNull(contaDTO.getId()) || contaDTO.getId().compareTo(0L) <= 0) {
      throw new ValidacaoException("O id precisa ser preenchido");
    }

    var pesquisa = contaRepository.findByDescricao(contaDTO.getDescricao());
    if (pesquisa.isPresent() && pesquisa.get().getId() != contaDTO.getId()) {
      if (!pesquisa.get().isAtivo()) {
        throw new ValidacaoException("Já existe uma conta desativada com a mesma descrição");
      }

      throw new ValidacaoException("Já existe uma conta com a mesma descrição");
    }

    var pesquisaId = contaRepository.findByIdOptional(contaDTO.getId());
    if (pesquisaId.isEmpty()) {
      throw new ValidacaoException("Conta não encontrada");
    }
    if (!pesquisaId.get().isAtivo()) {
      throw new ValidacaoException("A conta está desativada");
    }

    pesquisaId.get().setDescricao(contaDTO.getDescricao());

    contaRepository.persist(pesquisaId.get());

    return contaDTO;
  }

  @Transactional
  public void desativarConta(Long id) throws ValidacaoException {
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
