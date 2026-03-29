package br.pucminas.graphtest.application.service.interfaces;

import br.pucminas.graphtest.application.domain.Gce;
import br.pucminas.graphtest.application.port.input.gce.records.ValidationGceOutput;

/**
 * Define o contrato do servico responsavel por validar modelos GCE.
 */
public interface GceValidationResultService {

    /**
     * Executa a validacao estrutural e semantica de um grafo.
     *
     * @param graph agregado de GCE a ser analisado
     * @return resultado consolidado da validacao
     */
    ValidationGceOutput validate(Gce graph);
}
