package br.pucminas.graphtest.application.port.input.gfc;

import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceMethodOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.ListGfcSourceMethodsInput;

import java.util.List;

/**
 * Porta de entrada do caso de uso responsavel por listar metodos disponiveis em codigo Java.
 */
public interface ListGfcSourceMethodsUseCasePort {

    /**
     * Lista os metodos encontrados no codigo-fonte informado.
     *
     * @param input dados de entrada contendo codigo-fonte Java
     * @return metodos encontrados
     */
    List<GfcSourceMethodOutput> execute(ListGfcSourceMethodsInput input);
}
