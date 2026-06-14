package br.pucminas.graphtest.application.port.input.gfc;

import java.util.UUID;

/**
 * Porta de entrada do caso de uso responsavel por remover um GFC.
 */
public interface DeleteGfcUseCasePort {

    void execute(UUID projectId, UUID gfcId);
}
