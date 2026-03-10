package br.pucminas.graphtest.application.port.security;

import br.pucminas.graphtest.adapters.outbound.security.UserDetailsImpl;

import java.util.UUID;

/**
 * Valida a autorização do usuário logado para realizar uma determinada requisição
 */
public interface ValidadorAutorizacaoRequisicao {

    UserDetailsImpl validarAutorizacaoRequisicao(UUID id, String topico);

    UserDetailsImpl getUsuarioLogado();

    UserDetailsImpl validarAutorizacaoRequisicao();
}
