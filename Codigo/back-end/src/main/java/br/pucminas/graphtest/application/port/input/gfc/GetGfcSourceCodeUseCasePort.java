package br.pucminas.graphtest.application.port.input.gfc;

import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceCodeOutput;

import java.util.UUID;

/**
 * Porta de entrada para recuperar o codigo-fonte Java persistido de um source-file GFC.
 */
public interface GetGfcSourceCodeUseCasePort {

    GfcSourceCodeOutput execute(UUID sourceFileId);
}
