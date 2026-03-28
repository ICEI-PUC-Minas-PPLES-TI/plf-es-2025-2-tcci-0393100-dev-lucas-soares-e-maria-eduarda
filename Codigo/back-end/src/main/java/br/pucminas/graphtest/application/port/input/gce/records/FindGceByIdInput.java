package br.pucminas.graphtest.application.port.input.gce.records;

/**
 * Dados de entrada para busca de um GCE por identificador.
 */
public record FindGceByIdInput(
        Long id
) {
}
