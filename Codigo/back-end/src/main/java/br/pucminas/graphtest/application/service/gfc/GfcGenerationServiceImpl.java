package br.pucminas.graphtest.application.service.gfc;

import br.pucminas.graphtest.application.domain.gfc.model.Gfc;
import br.pucminas.graphtest.application.service.gfc.builder.GfcBuilder;
import br.pucminas.graphtest.application.service.gfc.builder.GfcControlFlowBuildResult;
import br.pucminas.graphtest.application.service.gfc.interfaces.GfcGenerationService;
import br.pucminas.graphtest.application.service.gfc.parser.JavaSourceParser;
import br.pucminas.graphtest.application.service.gfc.records.GfcGenerationInput;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.UUID;

import static br.pucminas.graphtest.application.domain.gfc.rules.GfcDomainRules.JAVA_LANGUAGE;

/**
 * Gerador de Grafo de Fluxo de Controle baseado em JavaParser.
 */
public class GfcGenerationServiceImpl implements GfcGenerationService {

    private final JavaSourceParser javaSourceParser;

    public GfcGenerationServiceImpl(JavaSourceParser javaSourceParser) {
        this.javaSourceParser = javaSourceParser;
    }

    @Override
    public Gfc generate(GfcGenerationInput input) {
        MethodDeclaration method = javaSourceParser.parseMethodBySignature(input.sourceCode(), input.methodSignature());
        GfcControlFlowBuildResult buildResult = new GfcBuilder().build(method);

        if (input.sourceFileId() == null) {
            return Gfc.preview(
                    UUID.randomUUID(),
                    input.projectId(),
                    input.methodSignature(),
                    input.name(),
                    input.description(),
                    JAVA_LANGUAGE,
                    buildResult.nodes(),
                    buildResult.edges()
            );
        }

        return Gfc.persisted(
                UUID.randomUUID(),
                input.projectId(),
                input.sourceFileId(),
                input.methodSignature(),
                input.name(),
                input.description(),
                JAVA_LANGUAGE,
                buildResult.nodes(),
                buildResult.edges()
        );
    }
}
