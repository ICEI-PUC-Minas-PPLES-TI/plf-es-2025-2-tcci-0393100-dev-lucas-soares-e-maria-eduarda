package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.domain.Gce;
import br.pucminas.graphtest.application.port.input.gce.ValidateGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.records.ValidationGceOutput;
import br.pucminas.graphtest.application.service.interfaces.GceValidationResultService;

/**
 * Caso de uso responsavel por orquestrar a validacao de um GCE.
 */
public class ValidateGceUseCaseImpl implements ValidateGceUseCasePort {

    private final GceValidationResultService gceValidationResultService;

    /**
     * Cria o caso de uso com o servico responsavel pela validacao do modelo.
     *
     * @param gceValidationResultService servico que executa as verificacoes do GCE
     */
    public ValidateGceUseCaseImpl(GceValidationResultService gceValidationResultService) {
        this.gceValidationResultService = gceValidationResultService;
    }

    /**
     * Executa a validacao estrutural e semantica do grafo recebido.
     *
     * @param graph agregado de GCE a ser validado
     * @return resultado consolidado da validacao
     */
    @Override
    public ValidationGceOutput execute(Gce graph) {
        return gceValidationResultService.validate(graph);
    }
}
