package br.pucminas.graphtest.application.port.input.decisiontable.records;

import java.util.UUID;

/**
 * Dados de entrada para listagem de tabelas de decisao por projeto.
 */
public record ListDecisionTablesByProjectInput(
        UUID projectId
) {
}
