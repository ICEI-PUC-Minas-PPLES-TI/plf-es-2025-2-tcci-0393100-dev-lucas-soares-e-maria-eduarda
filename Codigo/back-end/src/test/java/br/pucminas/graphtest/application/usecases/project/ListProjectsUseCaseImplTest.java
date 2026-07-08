package br.pucminas.graphtest.application.usecases.project;

import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.domain.user.enums.UserProfileEnum;
import br.pucminas.graphtest.application.exception.UnauthorizedUserException;
import br.pucminas.graphtest.application.port.input.project.records.ProjectOutput;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepositoryPort;
import br.pucminas.graphtest.application.port.output.security.CurrentUserPort;
import br.pucminas.graphtest.application.security.AuthenticatedUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListProjectsUseCaseImplTest {

    @Mock
    private ProjectRepositoryPort projectRepository;

    @Mock
    private CurrentUserPort currentUserPort;

    @InjectMocks
    private ListProjectsUseCaseImpl useCase;

    @Test
    void shouldListAllProjectsWhenCurrentUserIsAdmin() {
        when(currentUserPort.getCurrentUser())
                .thenReturn(new AuthenticatedUser(UUID.randomUUID(), "admin", UserProfileEnum.ADMIN));
        Project project = new Project(UUID.randomUUID(), "Projeto", "Descricao", UUID.randomUUID());
        when(projectRepository.findAll()).thenReturn(List.of(project));

        List<ProjectOutput> outputs = useCase.execute();

        assertEquals(1, outputs.size());
    }

    @Test
    void shouldThrowWhenCurrentUserIsNotAdmin() {
        when(currentUserPort.getCurrentUser())
                .thenReturn(new AuthenticatedUser(UUID.randomUUID(), "usuario", UserProfileEnum.USUARIO));

        assertThrows(UnauthorizedUserException.class, () -> useCase.execute());
        verify(projectRepository, never()).findAll();
    }
}
