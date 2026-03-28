package br.pucminas.graphtest.application.port.input.gce;

import br.pucminas.graphtest.application.port.input.gce.records.FindGceByIdInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;

/**
 * Porta de entrada do caso de uso responsavel por localizar um GCE por id.
 */
public interface FindGceByIdUseCasePort {

    /**
     * Busca o GCE identificado na entrada.
     *
     * @param input dados contendo o identificador do grafo
     * @return representacao do GCE encontrado
     */
    GceOutput execute(FindGceByIdInput input);
}
