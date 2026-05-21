package br.pucminas.graphtest.application.service.gfc;

import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceMethodOutput;
import br.pucminas.graphtest.application.service.gfc.interfaces.GfcSourceMethodListingService;
import br.pucminas.graphtest.application.service.gfc.parser.JavaSourceParser;

import java.util.List;

/**
 * Implementacao do servico de listagem de metodos fonte para GFC.
 */
public class GfcSourceMethodListingServiceImpl implements GfcSourceMethodListingService {

    private final JavaSourceParser javaSourceParser;

    public GfcSourceMethodListingServiceImpl(JavaSourceParser javaSourceParser) {
        this.javaSourceParser = javaSourceParser;
    }

    @Override
    public List<GfcSourceMethodOutput> listMethods(String sourceCode) {
        return javaSourceParser.listMethods(sourceCode);
    }
}
