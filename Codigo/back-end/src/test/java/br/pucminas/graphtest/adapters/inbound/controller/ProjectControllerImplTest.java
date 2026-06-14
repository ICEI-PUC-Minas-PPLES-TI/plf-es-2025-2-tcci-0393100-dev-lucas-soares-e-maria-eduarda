package br.pucminas.graphtest.adapters.inbound.controller;

import br.pucminas.graphtest.adapters.inbound.dto.project.ProjectArtifactDTO;
import br.pucminas.graphtest.application.port.input.project.CreateProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.project.DeleteProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.project.FindProjectByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.project.ListProjectArtifactsUseCasePort;
import br.pucminas.graphtest.application.port.input.project.ListProjectsByUserUseCasePort;
import br.pucminas.graphtest.application.port.input.project.ListProjectsUseCasePort;
import br.pucminas.graphtest.application.port.input.project.UpdateProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.project.records.ProjectArtifactOutput;
import br.pucminas.graphtest.application.port.input.project.records.RelatedArtifactOutput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class ProjectControllerImplTest {

    @Mock
    private CreateProjectUseCasePort createProjectUseCasePort;
    @Mock
    private DeleteProjectUseCasePort deleteProjectUseCasePort;
    @Mock
    private FindProjectByIdUseCasePort findProjectByIdUseCasePort;
    @Mock
    private ListProjectsUseCasePort listProjectsUseCasePort;
    @Mock
    private ListProjectsByUserUseCasePort listProjectsByUserUseCasePort;
    @Mock
    private ListProjectArtifactsUseCasePort listProjectArtifactsUseCasePort;
    @Mock
    private UpdateProjectUseCasePort updateProjectUseCasePort;

    @InjectMocks
    private ProjectControllerImpl controller;

    @Test
    void shouldListProjectArtifactsThroughInputPort() {
        UUID projectId = UUID.randomUUID();
        UUID sourceFileId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.of(2026, 5, 31, 10, 0);
        ProjectArtifactOutput output = new ProjectArtifactOutput(
                UUID.randomUUID(),
                "GFC",
                "GFC Login",
                createdAt,
                null,
                new RelatedArtifactOutput("GFC_SOURCE_FILE", sourceFileId, "Login.java")
        );
        when(listProjectArtifactsUseCasePort.execute(projectId)).thenReturn(List.of(output));

        ResponseEntity<List<ProjectArtifactDTO>> response = controller.listArtifacts(projectId);

        verify(listProjectArtifactsUseCasePort).execute(projectId);
        assertEquals(OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("GFC", response.getBody().getFirst().type());
        assertEquals("GFC Login", response.getBody().getFirst().name());
        assertEquals(createdAt, response.getBody().getFirst().createdAt());
        assertEquals("GFC_SOURCE_FILE", response.getBody().getFirst().relatedArtifact().type());
        assertEquals(sourceFileId, response.getBody().getFirst().relatedArtifact().id());
        assertEquals("Login.java", response.getBody().getFirst().relatedArtifact().name());
    }
}
