package br.pucminas.graphtest.application.port.input.gce;

import br.pucminas.graphtest.application.port.input.gce.records.CreateGceInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;

/**
 * Porta de entrada do caso de uso responsavel por criar um novo GCE.
 */
public interface CreateGceUseCasePort {

    /**
     * Cria e persiste um novo GCE a partir dos dados informados.
     *
     * @param input dados de entrada para criacao do grafo
     * @return representacao do GCE criado
     */
    GceOutput execute(CreateGceInput input);
}
