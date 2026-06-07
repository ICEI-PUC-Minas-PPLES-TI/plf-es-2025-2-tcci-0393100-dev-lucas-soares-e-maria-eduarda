package br.pucminas.graphtest.application.port.input.decisiontable.records;

import java.util.UUID;

/**
 * Dados de entrada para operacoes sobre a tabela de decisao a partir do seu identificador.
 */
public record DecisionTableByIdInput(
        UUID projectId,
        UUID id
) {
}
