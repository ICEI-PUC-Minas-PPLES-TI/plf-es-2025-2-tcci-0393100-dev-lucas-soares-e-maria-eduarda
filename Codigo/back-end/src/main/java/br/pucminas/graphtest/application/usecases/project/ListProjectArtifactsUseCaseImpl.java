package br.pucminas.graphtest.application.usecases.project;

import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gfc.model.Gfc;
import br.pucminas.graphtest.application.domain.gfc.model.GfcSourceFile;
import br.pucminas.graphtest.application.port.input.project.ListProjectArtifactsUseCasePort;
import br.pucminas.graphtest.application.port.input.project.records.ProjectArtifactOutput;
import br.pucminas.graphtest.application.port.input.project.records.RelatedArtifactOutput;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GfcRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GfcSourceFileRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListProjectArtifactsUseCaseImpl implements ListProjectArtifactsUseCasePort {

    private static final String TYPE_DECISION_TABLE = "DECISION_TABLE";
    private static final String TYPE_GCE = "GCE";
    private static final String TYPE_GFC = "GFC";
    private static final String TYPE_GFC_SOURCE_FILE = "GFC_SOURCE_FILE";

    private final ProjectAccessService projectAccessService;
    private final GceRepositoryPort gceRepositoryPort;
    private final GfcRepositoryPort gfcRepositoryPort;
    private final DecisionTableRepositoryPort decisionTableRepositoryPort;
    private final GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort;

    public ListProjectArtifactsUseCaseImpl(ProjectAccessService projectAccessService,
                                           GceRepositoryPort gceRepositoryPort,
                                           GfcRepositoryPort gfcRepositoryPort,
                                           DecisionTableRepositoryPort decisionTableRepositoryPort,
                                           GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort) {
        this.projectAccessService = projectAccessService;
        this.gceRepositoryPort = gceRepositoryPort;
        this.gfcRepositoryPort = gfcRepositoryPort;
        this.decisionTableRepositoryPort = decisionTableRepositoryPort;
        this.gfcSourceFileRepositoryPort = gfcSourceFileRepositoryPort;
    }

    @Override
    public List<ProjectArtifactOutput> execute(UUID projectId) {
        projectAccessService.findAuthorizedProject(projectId);

        List<Gce> gces = gceRepositoryPort.findAllByProjectId(projectId);
        List<Gfc> gfcs = gfcRepositoryPort.findAllByProjectId(projectId);
        List<DecisionTable> decisionTables = decisionTableRepositoryPort.findAllByProjectId(projectId);

        Map<UUID, Gce> gcesById = gces.stream()
                .collect(Collectors.toMap(Gce::getId, Function.identity()));
        Map<UUID, GfcSourceFile> sourceFilesById = gfcSourceFileRepositoryPort.findAllByProjectId(projectId).stream()
                .collect(Collectors.toMap(GfcSourceFile::getId, Function.identity()));

        List<ProjectArtifactOutput> artifacts = new ArrayList<>();
        artifacts.addAll(gces.stream().map(this::toGceArtifact).toList());
        artifacts.addAll(gfcs.stream().map(gfc -> toGfcArtifact(gfc, sourceFilesById)).toList());
        artifacts.addAll(decisionTables.stream()
                .map(decisionTable -> toDecisionTableArtifact(decisionTable, gcesById))
                .toList());

        return artifacts.stream()
                .sorted(Comparator.comparing(this::mostRecentDate).reversed())
                .toList();
    }

    private ProjectArtifactOutput toGceArtifact(Gce gce) {
        return new ProjectArtifactOutput(
                gce.getId(),
                TYPE_GCE,
                gce.getName(),
                gce.getCreatedAt(),
                gce.getUpdatedAt(),
                null
        );
    }

    private ProjectArtifactOutput toGfcArtifact(Gfc gfc, Map<UUID, GfcSourceFile> sourceFilesById) {
        return new ProjectArtifactOutput(
                gfc.getId(),
                TYPE_GFC,
                gfc.getName(),
                gfc.getCreatedAt(),
                gfc.getUpdatedAt(),
                relatedSourceFile(gfc.getSourceFileId(), sourceFilesById)
        );
    }

    private ProjectArtifactOutput toDecisionTableArtifact(DecisionTable decisionTable, Map<UUID, Gce> gcesById) {
        return new ProjectArtifactOutput(
                decisionTable.getId(),
                TYPE_DECISION_TABLE,
                decisionTable.getName(),
                decisionTable.getCreatedAt(),
                decisionTable.getUpdatedAt(),
                relatedGce(decisionTable.getGceId(), gcesById)
        );
    }

    private RelatedArtifactOutput relatedGce(UUID gceId, Map<UUID, Gce> gcesById) {
        if (gceId == null) {
            return null;
        }
        Gce gce = gcesById.get(gceId);
        return new RelatedArtifactOutput(TYPE_GCE, gceId, gce == null ? null : gce.getName());
    }

    private RelatedArtifactOutput relatedSourceFile(UUID sourceFileId, Map<UUID, GfcSourceFile> sourceFilesById) {
        if (sourceFileId == null) {
            return null;
        }
        GfcSourceFile sourceFile = sourceFilesById.get(sourceFileId);
        return new RelatedArtifactOutput(TYPE_GFC_SOURCE_FILE, sourceFileId, sourceFile == null ? null : sourceFile.getFileName());
    }

    private LocalDateTime mostRecentDate(ProjectArtifactOutput output) {
        LocalDateTime referenceDate = output.updatedAt() == null ? output.createdAt() : output.updatedAt();
        return referenceDate == null ? LocalDateTime.MIN : referenceDate;
    }
}
