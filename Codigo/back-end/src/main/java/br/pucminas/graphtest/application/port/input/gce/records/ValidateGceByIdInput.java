package br.pucminas.graphtest.application.port.input.gce.records;

import java.util.UUID;

/**
 * Dados de entrada para validacao de um GCE persistido por identificador.
 */
public record ValidateGceByIdInput(
        UUID id
) {
}
