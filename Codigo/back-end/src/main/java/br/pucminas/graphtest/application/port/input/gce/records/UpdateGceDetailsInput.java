package br.pucminas.graphtest.application.port.input.gce.records;

import java.util.UUID;

public record UpdateGceDetailsInput(
        UUID projectId,
        UUID id,
        String name,
        String description
) {
}
