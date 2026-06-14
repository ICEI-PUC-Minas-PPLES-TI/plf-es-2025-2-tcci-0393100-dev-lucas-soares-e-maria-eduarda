package br.pucminas.graphtest.application.port.input.gce;

import br.pucminas.graphtest.application.port.input.gce.records.ValidateGceInput;
import br.pucminas.graphtest.application.port.input.gce.records.ValidationGceOutput;

/**
 * Porta de entrada do caso de uso responsavel por validar um GCE.
 */
public interface ValidateGceUseCasePort {

    /**
     * Executa a validacao estrutural e semantica do grafo informado.
     *
     * @param input dados do grafo a ser validado
     * @return resultado consolidado da validacao
     */
    ValidationGceOutput execute(ValidateGceInput input);
}
