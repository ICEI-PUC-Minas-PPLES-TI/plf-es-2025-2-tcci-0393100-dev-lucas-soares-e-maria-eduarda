package br.pucminas.graphtest.application.port.input.gce.records;

import java.util.List;

/**
 * Saida consolidada do caso de uso de validacao de um GCE.
 *
 * @param valid indica se a validacao nao encontrou erros
 * @param errors erros identificados durante a validacao
 * @param warnings avisos identificados durante a validacao
 */
public record ValidationGceOutput(
        boolean valid,
        List<ValidationGceMessage> errors,
        List<ValidationGceMessage> warnings
) {

    /**
     * Cria a saida de validacao a partir das colecoes acumuladas no processamento.
     *
     * @param errors erros identificados
     * @param warnings avisos identificados
     */
    public ValidationGceOutput(List<ValidationGceMessage> errors, List<ValidationGceMessage> warnings) {
        this(errors.isEmpty(), List.copyOf(errors), List.copyOf(warnings));
    }
}
