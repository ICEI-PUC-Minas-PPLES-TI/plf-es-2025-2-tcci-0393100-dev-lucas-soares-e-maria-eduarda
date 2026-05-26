package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.domain.gfc.model.Gfc;
import br.pucminas.graphtest.application.exception.GfcNotFoundException;
import br.pucminas.graphtest.application.exception.InvalidCyclomaticComplexityException;
import br.pucminas.graphtest.application.port.input.gfc.CalculateCyclomaticComplexityUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.GenerateStructuralTestSignatureUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.records.CyclomaticComplexityOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.GenerateStructuralTestSignatureOutput;
import br.pucminas.graphtest.application.port.input.gfc.records.StructuralTestMethodSignatureOutput;
import br.pucminas.graphtest.application.port.output.repositories.GfcRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class GenerateStructuralTestSignatureUseCaseImpl implements GenerateStructuralTestSignatureUseCasePort {

    private static final String INVALID_COMPLEXITY_MESSAGE = "A complexidade ciclomatica deve ser maior ou igual a 1 para gerar assinaturas de teste estrutural.";

    private final GfcRepositoryPort gfcRepositoryPort;
    private final ProjectAccessService projectAccessService;
    private final CalculateCyclomaticComplexityUseCasePort calculateCyclomaticComplexityUseCasePort;

    public GenerateStructuralTestSignatureUseCaseImpl(
            GfcRepositoryPort gfcRepositoryPort,
            ProjectAccessService projectAccessService,
            CalculateCyclomaticComplexityUseCasePort calculateCyclomaticComplexityUseCasePort
    ) {
        this.gfcRepositoryPort = gfcRepositoryPort;
        this.projectAccessService = projectAccessService;
        this.calculateCyclomaticComplexityUseCasePort = calculateCyclomaticComplexityUseCasePort;
    }

    @Override
    public GenerateStructuralTestSignatureOutput execute(UUID gfcId) {
        Gfc gfc = gfcRepositoryPort.findById(gfcId)
                .orElseThrow(GfcNotFoundException::new);

        projectAccessService.findAuthorizedProject(gfc.getProjectId());

        CyclomaticComplexityOutput complexityOutput = calculateCyclomaticComplexityUseCasePort.execute(gfcId);

        int cyclomaticComplexity = complexityOutput.cyclomaticComplexityByEdgesAndNodes();
        validateComplexity(cyclomaticComplexity);

        List<StructuralTestMethodSignatureOutput> testMethods = IntStream.rangeClosed(1, cyclomaticComplexity)
                .mapToObj(this::buildMethod)
                .toList();
        String generatedCode = testMethods.stream()
                .map(StructuralTestMethodSignatureOutput::generatedCode)
                .reduce((first, second) -> first + "\n\n" + second)
                .orElse("");

        return new GenerateStructuralTestSignatureOutput(
                gfc.getId(),
                gfc.getMethodSignature(),
                cyclomaticComplexity,
                testMethods,
                generatedCode
        );
    }

    private void validateComplexity(int cyclomaticComplexity) {
        if (cyclomaticComplexity < 1) {
            throw new InvalidCyclomaticComplexityException(INVALID_COMPLEXITY_MESSAGE);
        }
    }

    private StructuralTestMethodSignatureOutput buildMethod(int index) {
        String methodName = "teste%02d".formatted(index);
        String generatedCode = "@Test\nvoid " + methodName + "() {\n\n}";
        return new StructuralTestMethodSignatureOutput(methodName, generatedCode);
    }
}
