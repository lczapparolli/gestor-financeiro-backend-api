package br.com.lczapparolli.service;

import java.util.Objects;
import java.util.Optional;

import br.com.lczapparolli.database.entity.Conta;
import br.com.lczapparolli.database.repository.ContaRepository;
import br.com.lczapparolli.dto.ContaDTO;
import br.com.lczapparolli.dto.ResultadoDTO;
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

}