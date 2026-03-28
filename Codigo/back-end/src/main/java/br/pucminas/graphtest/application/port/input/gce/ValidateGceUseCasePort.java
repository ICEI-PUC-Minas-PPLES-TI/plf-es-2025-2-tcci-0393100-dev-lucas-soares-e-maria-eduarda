package br.pucminas.graphtest.application.port.input.gce;

import br.pucminas.graphtest.application.domain.Gce;
import br.pucminas.graphtest.application.port.input.gce.records.ValidationGceOutput;

/**
 * Porta de entrada do caso de uso responsavel por validar um GCE.
 */
public interface ValidateGceUseCasePort {

    /**
     * Executa a validacao estrutural e semantica do grafo informado.
     *
     * @param graph agregado de GCE a ser validado
     * @return resultado consolidado da validacao
     */
    ValidationGceOutput execute(Gce graph);
}
