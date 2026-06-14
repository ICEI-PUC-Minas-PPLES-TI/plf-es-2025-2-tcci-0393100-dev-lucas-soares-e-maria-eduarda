package br.pucminas.graphtest.application.port.input.decisiontable.records;

import java.util.UUID;

public record UpdateDecisionTableDetailsInput(
        UUID projectId,
        UUID id,
        String name,
        String description
) {
}
