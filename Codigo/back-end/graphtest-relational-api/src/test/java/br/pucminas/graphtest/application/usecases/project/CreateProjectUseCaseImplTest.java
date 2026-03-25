package br.pucminas.graphtest.application.usecases.project;

import br.pucminas.graphtest.application.domain.Project;
import br.pucminas.graphtest.application.domain.enums.UserProfileEnum;
import br.pucminas.graphtest.application.domain.records.AuthenticatedUser;
import br.pucminas.graphtest.application.port.input.project.records.CreateProjectInput;
import br.pucminas.graphtest.application.port.input.project.records.ProjectOutput;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepository;
import br.pucminas.graphtest.application.port.output.security.CurrentUserPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateProjectUseCaseImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private CurrentUserPort currentUserPort;

    @InjectMocks
    private CreateProjectUseCaseImpl useCase;

    @Test
    void shouldKeepProvidedNameWhenCreatingProject() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        CreateProjectInput input = new CreateProjectInput("Projeto Manual", "Descricao");

        when(currentUserPort.getCurrentUser())
                .thenReturn(new AuthenticatedUser(userId, "usuario@teste.com", UserProfileEnum.USUARIO));
        when(projectRepository.save(any(Project.class)))
                .thenReturn(new Project(projectId, "Projeto Manual", "Descricao", userId));

        ProjectOutput output = useCase.execute(input);

        assertEquals("Projeto Manual", output.name());
        verify(projectRepository, never()).countByUserId(userId);
    }

    @Test
    void shouldGenerateDefaultNameWhenNameIsMissing() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        CreateProjectInput input = new CreateProjectInput(null, "Descricao");

        when(currentUserPort.getCurrentUser())
                .thenReturn(new AuthenticatedUser(userId, "usuario@teste.com", UserProfileEnum.USUARIO));
        when(projectRepository.countByUserId(userId)).thenReturn(2L);
        when(projectRepository.save(any(Project.class)))
                .thenAnswer(invocation -> {
                    Project savedProject = invocation.getArgument(0);
                    return new Project(projectId, savedProject.getName(), savedProject.getDescription(), savedProject.getUserId());
                });

        ProjectOutput output = useCase.execute(input);

        assertEquals("Projeto 3", output.name());
        verify(projectRepository).countByUserId(userId);
    }
}
