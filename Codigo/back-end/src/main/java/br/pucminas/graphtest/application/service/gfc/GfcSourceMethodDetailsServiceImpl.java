package br.pucminas.graphtest.application.service.gfc;

import br.pucminas.graphtest.application.exception.GfcMethodNotFoundException;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceMethodDetailsOutput;
import br.pucminas.graphtest.application.service.gfc.interfaces.GfcSourceMethodDetailsService;
import br.pucminas.graphtest.application.service.gfc.parser.JavaSourceParser;
import com.github.javaparser.ast.body.MethodDeclaration;

public class GfcSourceMethodDetailsServiceImpl implements GfcSourceMethodDetailsService {

    private final JavaSourceParser javaSourceParser;

    public GfcSourceMethodDetailsServiceImpl(JavaSourceParser javaSourceParser) {
        this.javaSourceParser = javaSourceParser;
    }

    @Override
    public GfcSourceMethodDetailsOutput getDetails(String sourceCode, String methodSignature) {
        if (methodSignature == null || methodSignature.isBlank()) {
            throw new GfcMethodNotFoundException("Metodo informado nao foi encontrado no codigo-fonte Java.");
        }

        MethodDeclaration method = javaSourceParser.parseMethodBySignature(sourceCode, methodSignature);
        return new GfcSourceMethodDetailsOutput(
                method.getNameAsString(),
                javaSourceParser.signatureOf(method),
                javaSourceParser.startLine(method),
                javaSourceParser.endLine(method),
                method.toString()
        );
    }
}
