package br.pucminas.graphtest.application.port.input.project.records;

import java.util.UUID;

public record RelatedArtifactOutput(
        String type,
        UUID id,
        String name
) {
}
