package br.pucminas.graphtest.application.service;

import br.pucminas.graphtest.application.exception.GfcMethodNotFoundException;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceMethodDetailsOutput;
import br.pucminas.graphtest.application.service.gfc.GfcSourceMethodDetailsServiceImpl;
import br.pucminas.graphtest.application.service.gfc.parser.JavaSourceParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GfcSourceMethodDetailsServiceImplTest {

    private final GfcSourceMethodDetailsServiceImpl service = new GfcSourceMethodDetailsServiceImpl(new JavaSourceParser());

    @Test
    void shouldExtractMethodDetailsBySignature() {
        String sourceCode = """
                public class Exemplo {
                    int soma(int a, int b) {
                        return a + b;
                    }

                    int calcular(int valor) {
                        int resultado = 0;
                        if (valor > 0) {
                            resultado = valor * 2;
                        }
                        return resultado;
                    }
                }
                """;

        GfcSourceMethodDetailsOutput output = service.getDetails(sourceCode, "int calcular(int valor)");

        assertEquals("calcular", output.name());
        assertEquals("int calcular(int valor)", output.signature());
        assertEquals(6, output.startLine());
        assertEquals(12, output.endLine());
        assertTrue(output.sourceCode().contains("int calcular(int valor)"));
        assertTrue(output.sourceCode().contains("return resultado;"));
    }

    @Test
    void shouldThrowWhenSignatureDoesNotExist() {
        String sourceCode = "class Exemplo { int soma(int a, int b) { return a + b; } }";

        assertThrows(GfcMethodNotFoundException.class, () -> service.getDetails(sourceCode, "void inexistente()"));
    }
}
