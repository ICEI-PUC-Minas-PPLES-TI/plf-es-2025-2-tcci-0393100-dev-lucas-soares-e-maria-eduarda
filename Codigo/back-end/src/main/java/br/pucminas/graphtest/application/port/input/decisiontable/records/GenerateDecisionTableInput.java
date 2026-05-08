package br.pucminas.graphtest.application.port.input.decisiontable.records;

import java.util.UUID;

/**
 * Dados de entrada para geracao da tabela de decisao de um GCE.
 */
public record GenerateDecisionTableInput(
        UUID gceId
) {
}
