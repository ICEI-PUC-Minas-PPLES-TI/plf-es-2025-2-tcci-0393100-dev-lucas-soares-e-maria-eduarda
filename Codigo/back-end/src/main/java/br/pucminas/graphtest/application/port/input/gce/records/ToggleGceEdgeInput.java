package br.pucminas.graphtest.application.port.input.gce.records;

import java.util.UUID;

/**
 * Dados para inverter uma aresta existente do GCE.
 */
public record ToggleGceEdgeInput(
        UUID gceId,
        UUID edgeId
) {
}
