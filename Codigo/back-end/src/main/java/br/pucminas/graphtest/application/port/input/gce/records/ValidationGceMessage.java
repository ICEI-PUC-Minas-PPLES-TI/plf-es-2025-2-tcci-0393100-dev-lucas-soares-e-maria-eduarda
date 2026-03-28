package br.pucminas.graphtest.application.port.input.gce.records;

/**
 * Mensagem padronizada retornada durante a validacao de um GCE.
 *
 * @param code identificador da regra ou verificacao executada
 * @param message descricao humana da ocorrencia encontrada
 */
public record ValidationGceMessage(
        String code,
        String message
) {
}
