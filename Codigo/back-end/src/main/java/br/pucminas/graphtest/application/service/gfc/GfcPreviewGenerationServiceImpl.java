package br.pucminas.graphtest.application.service.gfc;

import br.pucminas.graphtest.application.domain.gfc.model.Gfc;
import br.pucminas.graphtest.application.exception.EmptyJavaSourceCodeException;
import br.pucminas.graphtest.application.exception.GfcMethodNotFoundException;
import br.pucminas.graphtest.application.exception.InvalidJavaSourceCodeException;
import br.pucminas.graphtest.application.port.input.gfc.records.PreviewGfcInput;
import br.pucminas.graphtest.application.service.gfc.interfaces.GfcPreviewGenerationService;
import br.pucminas.graphtest.application.service.gfc.interfaces.GfcGenerationService;
import br.pucminas.graphtest.application.service.gfc.records.GfcGenerationInput;

/**
 * Gerador inicial de pre-visualizacao de Grafo de Fluxo de Controle baseado em JavaParser.
 *
 * <p>Esta implementacao pertence a camada de aplicacao porque interpreta codigo-fonte Java
 * e transforma uma AST em objetos do dominio GFC. O dominio permanece independente do
 * JavaParser e de qualquer infraestrutura externa.</p>
 *
 * <p>Nesta etapa inicial, o gerador escolhe o metodo indicado pela assinatura recebida
 * ou, quando nenhuma assinatura e informada, usa o primeiro metodo com corpo encontrado no
 * codigo-fonte. A geracao e feita inteiramente em memoria e nao realiza persistencia.</p>
 */
public class GfcPreviewGenerationServiceImpl implements GfcPreviewGenerationService {

    private final GfcGenerationService gfcGenerationService;

    public GfcPreviewGenerationServiceImpl(GfcGenerationService gfcGenerationService) {
        this.gfcGenerationService = gfcGenerationService;
    }

    /**
     * Gera uma pre-visualizacao de GFC para o metodo Java selecionado.
     *
     * @param input dados de entrada contendo projeto, metadados e codigo-fonte Java
     * @return agregado GFC montado em memoria
     * @throws EmptyJavaSourceCodeException quando o codigo-fonte esta vazio
     * @throws InvalidJavaSourceCodeException quando o codigo-fonte esta invalido
     * @throws GfcMethodNotFoundException quando nao ha metodo com corpo ou a assinatura solicitada nao existe
     */
    @Override
    public Gfc generate(PreviewGfcInput input) {
        return gfcGenerationService.generate(new GfcGenerationInput(
                input.projectId(),
                null,
                input.sourceCode(),
                input.methodSignature(),
                input.name(),
                input.description()
        ));
    }
}
