package br.pucminas.graphtest.application.service.gfc.interfaces;

import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceMethodDetailsOutput;

public interface GfcSourceMethodDetailsService {

    GfcSourceMethodDetailsOutput getDetails(String sourceCode, String methodSignature);
}
