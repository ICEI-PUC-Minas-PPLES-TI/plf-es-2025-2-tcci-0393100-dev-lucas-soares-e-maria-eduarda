package br.pucminas.graphtest.application.port.input.gfc.records;

import java.util.UUID;

public record CreateGfcSourceFileInput(
        UUID projectId,
        String fileName,
        String content
) {
}
