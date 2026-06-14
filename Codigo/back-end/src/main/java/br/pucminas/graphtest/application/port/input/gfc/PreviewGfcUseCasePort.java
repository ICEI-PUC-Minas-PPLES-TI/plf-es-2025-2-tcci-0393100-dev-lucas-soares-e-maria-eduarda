package br.pucminas.graphtest.application.port.input.gfc;

import br.pucminas.graphtest.application.port.input.gfc.records.GfcOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.PreviewGfcInput;

/**
 * Porta de entrada do caso de uso responsavel por gerar uma pre-visualizacao de GFC.
 */
public interface PreviewGfcUseCasePort {

    /**
     * Gera uma pre-visualizacao do GFC a partir do codigo-fonte informado.
     *
     * @param input dados de entrada para geracao da pre-visualizacao
     * @return representacao do GFC gerado em memoria
     */
    GfcOutput execute(PreviewGfcInput input);
}
