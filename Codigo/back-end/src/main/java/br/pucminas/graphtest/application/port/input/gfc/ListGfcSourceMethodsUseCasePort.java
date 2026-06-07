package br.pucminas.graphtest.application.port.input.gfc;

import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceMethodOutput;

import java.util.List;
import java.util.UUID;

/**
 * Porta de entrada do caso de uso responsavel por listar metodos disponiveis em codigo Java.
 */
public interface ListGfcSourceMethodsUseCasePort {

    /**
     * Lista os metodos encontrados no arquivo-fonte persistido informado.
     *
     * @param sourceFileId identificador do arquivo-fonte Java persistido
     * @return metodos encontrados
     */
    List<GfcSourceMethodOutput> execute(UUID projectId, UUID sourceFileId);
}
