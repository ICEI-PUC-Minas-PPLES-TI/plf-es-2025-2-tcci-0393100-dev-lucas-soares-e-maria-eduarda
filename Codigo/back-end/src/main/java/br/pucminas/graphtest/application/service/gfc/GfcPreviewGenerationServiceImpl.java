package br.pucminas.graphtest.application.service.gfc;

import br.pucminas.graphtest.application.domain.gfc.model.Gfc;
import br.pucminas.graphtest.application.port.input.gfc.records.PreviewGfcInput;
import br.pucminas.graphtest.application.service.gfc.interfaces.GfcPreviewGenerationService;
import br.pucminas.graphtest.application.service.gfc.builder.GfcControlFlowBuildResult;
import br.pucminas.graphtest.application.service.gfc.builder.GfcBuilder;
import br.pucminas.graphtest.application.service.gfc.parser.JavaSourceParser;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.UUID;

/**
 * Gerador inicial de pre-visualizacao de Grafo de Fluxo de Controle baseado em JavaParser.
 *
 * <p>Esta implementacao pertence a camada de aplicacao porque interpreta codigo-fonte Java
 * e transforma uma AST em objetos do dominio GFC. O dominio permanece independente do
 * JavaParser e de qualquer infraestrutura externa.</p>
 *
 * <p>Nesta etapa inicial, o gerador escolhe o metodo indicado pela assinatura recebida
 * ou, quando nenhuma assinatura e informada, usa o primeiro metodo com corpo encontrado no
 * codigo-fonte. A construcao de nos e arestas e delegada para {@link GfcBuilder}.
 * A geracao e feita inteiramente em memoria e nao realiza persistencia.</p>
 */
public class GfcPreviewGenerationServiceImpl implements GfcPreviewGenerationService {

    private final JavaSourceParser javaSourceParser;

    public GfcPreviewGenerationServiceImpl(JavaSourceParser javaSourceParser) {
        this.javaSourceParser = javaSourceParser;
    }

    /**
     * Gera uma pre-visualizacao de GFC para o metodo Java selecionado.
     *
     * @param input dados de entrada contendo projeto, metadados e codigo-fonte Java
     * @return agregado GFC montado em memoria
     * @throws IllegalArgumentException quando o codigo-fonte esta vazio, invalido, sem metodo com corpo
     *                                  ou sem a assinatura solicitada
     */
    @Override
    public Gfc generate(PreviewGfcInput input) {
        MethodDeclaration method = javaSourceParser.parseMethodBySignature(input.sourceCode(), input.methodSignature());
        GfcControlFlowBuildResult buildResult = new GfcBuilder().build(method);

        return new Gfc(
                UUID.randomUUID(),
                input.projectId(),
                input.name(),
                input.description(),
                input.sourceCode(),
                Gfc.JAVA_LANGUAGE,
                buildResult.nodes(),
                buildResult.edges()
        );
    }
}
