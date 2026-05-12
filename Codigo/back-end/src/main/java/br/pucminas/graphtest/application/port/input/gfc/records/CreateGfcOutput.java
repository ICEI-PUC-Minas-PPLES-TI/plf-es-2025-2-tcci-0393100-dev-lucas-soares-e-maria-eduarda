package br.pucminas.graphtest.application.port.input.gfc.records;

import java.util.UUID;

/**
 * Saida da criacao persistida de um GFC.
 */
public record CreateGfcOutput(
        UUID gfcId
) {
}
