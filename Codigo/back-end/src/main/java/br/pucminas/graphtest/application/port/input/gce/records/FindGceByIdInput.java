package br.pucminas.graphtest.application.port.input.gce.records;

import java.util.UUID;

/**
 * Dados de entrada para busca de um GCE por identificador.
 */
public record FindGceByIdInput(
        UUID id
) {
}
