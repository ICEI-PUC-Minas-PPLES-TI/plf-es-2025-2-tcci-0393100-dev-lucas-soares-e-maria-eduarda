package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.domain.gfc.model.Gfc;
import br.pucminas.graphtest.application.domain.gfc.model.GfcEdge;
import br.pucminas.graphtest.application.domain.gfc.model.GfcNode;
import br.pucminas.graphtest.application.exception.GfcNotFoundException;
import br.pucminas.graphtest.application.exception.InvalidCyclomaticComplexityException;
import br.pucminas.graphtest.application.port.input.gfc.CalculateCyclomaticComplexityUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.records.CyclomaticComplexityOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GenerateStructuralTestSignatureOutput;
import br.pucminas.graphtest.application.port.output.repositories.GfcRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static br.pucminas.graphtest.application.domain.gfc.enums.GfcEdgeTypeEnum.SEQUENTIAL;
import static br.pucminas.graphtest.application.domain.gfc.rules.GfcDomainRules.JAVA_LANGUAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerateStructuralTestSignatureUseCaseImplTest {

    private static final UUID GFC_ID = UUID.fromString("00000000-0000-0000-0000-000000000011");
    private static final UUID PROJECT_ID = UUID.fromString("00000000-0000-0000-0000-000000000012");
    private static final String METHOD_SIGNATURE = "public int calcular(int a, int b)";

    @Mock
    private GfcRepositoryPort gfcRepositoryPort;

    @Mock
    private ProjectAccessService projectAccessService;

    @Mock
    private CalculateCyclomaticComplexityUseCasePort calculateCyclomaticComplexityUseCasePort;

    @InjectMocks
    private GenerateStructuralTestSignatureUseCaseImpl useCase;

    @Test
    void shouldGenerateOneEmptyTestMethodForComplexityOne() {
        GenerateStructuralTestSignatureOutput output = executeForComplexity(1);

        assertEquals(GFC_ID, output.gfcId());
        assertEquals(METHOD_SIGNATURE, output.methodSignature());
        assertEquals(1, output.cyclomaticComplexity());
        assertEquals(1, output.testMethods().size());
        assertEquals("teste01", output.testMethods().getFirst().methodName());
        assertEquals("@Test\nvoid teste01() {\n\n}", output.generatedCode());
    }

    @Test
    void shouldGenerateThreeSequentialAnnotatedEmptyTestMethods() {
        GenerateStructuralTestSignatureOutput output = executeForComplexity(3);

        assertEquals(List.of("teste01", "teste02", "teste03"),
                output.testMethods().stream().map(method -> method.methodName()).toList());
        assertEquals(3, countOccurrences(output.generatedCode(), "@Test"));
        assertEquals("""
                @Test
                void teste01() {

                }

                @Test
                void teste02() {

                }

                @Test
                void teste03() {

                }""", output.generatedCode());
        assertTrue(output.testMethods().stream().allMatch(method -> method.generatedCode().endsWith("{\n\n}")));
    }

    @Test
    void shouldFormatTestNameWithTwoDigitsForComplexityTen() {
        GenerateStructuralTestSignatureOutput output = executeForComplexity(10);

        assertEquals(10, output.testMethods().size());
        assertEquals("teste01", output.testMethods().getFirst().methodName());
        assertEquals("teste09", output.testMethods().get(8).methodName());
        assertEquals("teste10", output.testMethods().get(9).methodName());
        assertEquals(10, countOccurrences(output.generatedCode(), "@Test"));
    }

    @Test
    void shouldThrowNotFoundWhenGfcDoesNotExist() {
        when(gfcRepositoryPort.findById(GFC_ID)).thenReturn(Optional.empty());

        assertThrows(GfcNotFoundException.class, () -> useCase.execute(GFC_ID));

        verifyNoInteractions(projectAccessService, calculateCyclomaticComplexityUseCasePort);
    }

    @Test
    void shouldRejectComplexityLowerThanOne() {
        when(gfcRepositoryPort.findById(GFC_ID)).thenReturn(Optional.of(gfc()));
        when(calculateCyclomaticComplexityUseCasePort.execute(GFC_ID)).thenReturn(complexityOutput(0));

        assertThrows(InvalidCyclomaticComplexityException.class, () -> useCase.execute(GFC_ID));

        verify(projectAccessService).findAuthorizedProject(PROJECT_ID);
    }

    private GenerateStructuralTestSignatureOutput executeForComplexity(int complexity) {
        when(gfcRepositoryPort.findById(GFC_ID)).thenReturn(Optional.of(gfc()));
        when(calculateCyclomaticComplexityUseCasePort.execute(GFC_ID)).thenReturn(complexityOutput(complexity));

        GenerateStructuralTestSignatureOutput output = useCase.execute(GFC_ID);

        verify(projectAccessService).findAuthorizedProject(PROJECT_ID);
        return output;
    }

    private CyclomaticComplexityOutput complexityOutput(int complexity) {
        return new CyclomaticComplexityOutput(
                GFC_ID,
                2,
                complexity + 1,
                complexity - 1,
                complexity,
                complexity,
                "V(g) = a - n + 2",
                "V(G) = P + 1",
                List.of()
        );
    }

    private Gfc gfc() {
        return Gfc.persisted(
                GFC_ID,
                PROJECT_ID,
                UUID.randomUUID(),
                METHOD_SIGNATURE,
                "GFC",
                null,
                JAVA_LANGUAGE,
                List.of(
                        GfcNode.start(UUID.randomUUID(), "N0", "Inicio"),
                        GfcNode.end(UUID.randomUUID(), "N_END", "Fim")
                ),
                List.of(new GfcEdge(UUID.randomUUID(), "N0", "N_END", SEQUENTIAL, null))
        );
    }

    private long countOccurrences(String value, String text) {
        return value.lines().filter(text::equals).count();
    }
}
