package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceMethodOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.ListGfcSourceMethodsInput;
import br.pucminas.graphtest.application.service.gfc.GfcSourceMethodListingServiceImpl;
import br.pucminas.graphtest.application.service.gfc.parser.JavaSourceParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListGfcSourceMethodsUseCaseImplTest {

    private final ListGfcSourceMethodsUseCaseImpl useCase = new ListGfcSourceMethodsUseCaseImpl(
            new GfcSourceMethodListingServiceImpl(new JavaSourceParser())
    );

    @Test
    void shouldListMethodsFromJavaSource() {
        String sourceCode = """
                        class Exemplo {
                            int soma(int a, int b) {
                                return a + b;
                            }

                            boolean valido(String email) {
                                return email != null;
                            }
                        }
                        """;
        ListGfcSourceMethodsInput input = new ListGfcSourceMethodsInput(sourceCode);

        List<GfcSourceMethodOutput> methods = useCase.execute(input);

        assertEquals(2, methods.size());
        assertEquals("soma", methods.get(0).name());
        assertEquals("int soma(int a, int b)", methods.get(0).signature());
        assertEquals(2, methods.get(0).startLine());
        assertEquals("boolean valido(String email)", methods.get(1).signature());
    }
}
