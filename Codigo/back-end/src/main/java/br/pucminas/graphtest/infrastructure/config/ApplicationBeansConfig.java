package br.pucminas.graphtest.infrastructure.config;

import br.pucminas.graphtest.application.port.input.project.CreateProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.project.DeleteProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.project.FindProjectByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.project.ListProjectsByUserUseCasePort;
import br.pucminas.graphtest.application.port.input.project.ListProjectsUseCasePort;
import br.pucminas.graphtest.application.port.input.project.UpdateProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.CreateGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.DeleteGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.FindGceByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.ListGcesUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.ListGcesByProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.AddNodeToGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.ToggleGceEdgeUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.UpdateGceNodeUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.UpdateGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.ValidateGceUseCasePort;
import br.pucminas.graphtest.application.port.input.security.GenerateTokenUseCasePort;
import br.pucminas.graphtest.application.port.input.security.LoadAuthenticationUserUseCasePort;
import br.pucminas.graphtest.application.port.input.security.AuthenticatedUserByTokenUseCasePort;
import br.pucminas.graphtest.application.port.input.security.VerifyTokenUseCasePort;
import br.pucminas.graphtest.application.port.input.user.CreateUserUseCasePort;
import br.pucminas.graphtest.application.port.input.user.DeleteUserUseCasePort;
import br.pucminas.graphtest.application.port.input.user.FindUserByEmailUseCasePort;
import br.pucminas.graphtest.application.port.input.user.FindUserByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.user.ListUsersUseCasePort;
import br.pucminas.graphtest.application.port.input.user.UpdateUserPasswordUseCasePort;
import br.pucminas.graphtest.application.port.input.user.UpdateUserUseCasePort;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.UserRepositoryPort;
import br.pucminas.graphtest.application.port.output.security.CurrentUserPort;
import br.pucminas.graphtest.application.port.output.security.PasswordEncoderPort;
import br.pucminas.graphtest.application.port.output.security.TokenServicePort;
import br.pucminas.graphtest.application.service.gce.GceValidationResultServiceImpl;
import br.pucminas.graphtest.application.service.project.ProjectAccessServiceImpl;
import br.pucminas.graphtest.application.service.project.ProjectDeletionServiceImpl;
import br.pucminas.graphtest.application.service.user.UserAuthorizationServiceImpl;
import br.pucminas.graphtest.application.service.gce.GceMutationServiceImpl;
import br.pucminas.graphtest.application.service.gce.interfaces.GceMutationService;
import br.pucminas.graphtest.application.service.gce.interfaces.GceValidationResultService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectDeletionService;
import br.pucminas.graphtest.application.service.user.interfaces.UserAuthorizationService;
import br.pucminas.graphtest.application.usecases.gce.CreateGceUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gce.DeleteGceUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gce.FindGceByIdUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gce.ListGcesUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gce.ListGcesByProjectUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gce.AddNodeToGceUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gce.ToggleGceEdgeUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gce.UpdateGceNodeUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gce.UpdateGceUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gce.ValidateGceUseCaseImpl;
import br.pucminas.graphtest.application.usecases.project.CreateProjectUseCaseImpl;
import br.pucminas.graphtest.application.usecases.project.DeleteProjectUseCaseImpl;
import br.pucminas.graphtest.application.usecases.project.FindProjectByIdUseCaseImpl;
import br.pucminas.graphtest.application.usecases.project.ListProjectsByUserUseCaseImpl;
import br.pucminas.graphtest.application.usecases.project.ListProjectsUseCaseImpl;
import br.pucminas.graphtest.application.usecases.project.UpdateProjectUseCaseImpl;
import br.pucminas.graphtest.application.usecases.security.GenerateTokenUseCaseImpl;
import br.pucminas.graphtest.application.usecases.security.LoadAuthenticationUserUseCaseImpl;
import br.pucminas.graphtest.application.usecases.security.AuthenticatedUserByTokenUseCaseImpl;
import br.pucminas.graphtest.application.usecases.security.VerifyTokenUseCaseImpl;
import br.pucminas.graphtest.application.usecases.user.CreateUserUseCaseImpl;
import br.pucminas.graphtest.application.usecases.user.DeleteUserUseCaseImpl;
import br.pucminas.graphtest.application.usecases.user.FindUserByEmailUseCaseImpl;
import br.pucminas.graphtest.application.usecases.user.FindUserByIdUseCaseImpl;
import br.pucminas.graphtest.application.usecases.user.ListUsersUseCaseImpl;
import br.pucminas.graphtest.application.usecases.user.UpdateUserPasswordUseCaseImpl;
import br.pucminas.graphtest.application.usecases.user.UpdateUserUseCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationBeansConfig {


    @Bean
    public GceValidationResultService gceValidationResultService() {
        return new GceValidationResultServiceImpl();
    }

    @Bean
    public GceMutationService gceMutationService() {
        return new GceMutationServiceImpl();
    }

    @Bean
    public CreateGceUseCasePort createGceUseCase(GceRepositoryPort gceRepositoryPort,
                                                 GceValidationResultService gceValidationResultService,
                                                 ProjectAccessService projectAccessService,
                                                 GceMutationService gceMutationService) {
        return new CreateGceUseCaseImpl(gceRepositoryPort, gceValidationResultService, projectAccessService, gceMutationService);
    }

    @Bean
    public FindGceByIdUseCasePort findGceByIdUseCase(GceRepositoryPort gceRepositoryPort,
                                                     ProjectAccessService projectAccessService) {
        return new FindGceByIdUseCaseImpl(gceRepositoryPort, projectAccessService);
    }

    @Bean
    public ListGcesUseCasePort listGcesUseCase(GceRepositoryPort gceRepositoryPort,
                                               ProjectRepositoryPort projectRepositoryPort,
                                               CurrentUserPort currentUserPort) {
        return new ListGcesUseCaseImpl(gceRepositoryPort, projectRepositoryPort, currentUserPort);
    }

    @Bean
    public ListGcesByProjectUseCasePort listGcesByProjectUseCase(GceRepositoryPort gceRepositoryPort,
                                                                 ProjectAccessService projectAccessService) {
        return new ListGcesByProjectUseCaseImpl(gceRepositoryPort, projectAccessService);
    }

    @Bean
    public DeleteGceUseCasePort deleteGceUseCase(GceRepositoryPort gceRepositoryPort,
                                                 ProjectAccessService projectAccessService,
                                                 GceMutationService gceMutationService) {
        return new DeleteGceUseCaseImpl(gceRepositoryPort, projectAccessService, gceMutationService);
    }

    @Bean
    public ValidateGceUseCasePort validateGceUseCase(ProjectAccessService projectAccessService,
                                                     GceValidationResultService gceValidationResultService,
                                                     GceMutationService gceMutationService) {
        return new ValidateGceUseCaseImpl(projectAccessService, gceValidationResultService, gceMutationService);
    }

    @Bean
    public UpdateGceUseCasePort updateGceUseCase(GceRepositoryPort gceRepositoryPort,
                                                 ProjectAccessService projectAccessService,
                                                 GceValidationResultService gceValidationResultService,
                                                 GceMutationService gceMutationService) {
        return new UpdateGceUseCaseImpl(gceRepositoryPort, projectAccessService, gceValidationResultService, gceMutationService);
    }

    @Bean
    public AddNodeToGceUseCasePort addNodeToGceUseCase(GceRepositoryPort gceRepositoryPort,
                                                       ProjectAccessService projectAccessService,
                                                       GceValidationResultService gceValidationResultService,
                                                       GceMutationService gceMutationService) {
        return new AddNodeToGceUseCaseImpl(gceRepositoryPort, projectAccessService, gceValidationResultService, gceMutationService);
    }

    @Bean
    public UpdateGceNodeUseCasePort updateGceNodeUseCase(GceRepositoryPort gceRepositoryPort,
                                                         ProjectAccessService projectAccessService,
                                                         GceValidationResultService gceValidationResultService,
                                                         GceMutationService gceMutationService) {
        return new UpdateGceNodeUseCaseImpl(gceRepositoryPort, projectAccessService, gceValidationResultService, gceMutationService);
    }

    @Bean
    public ToggleGceEdgeUseCasePort toggleGceEdgeUseCase(GceRepositoryPort gceRepositoryPort,
                                                         ProjectAccessService projectAccessService,
                                                         GceValidationResultService gceValidationResultService,
                                                         GceMutationService gceMutationService) {
        return new ToggleGceEdgeUseCaseImpl(gceRepositoryPort, projectAccessService, gceValidationResultService, gceMutationService);
    }

    @Bean
    public ProjectAccessService projectAccessService(ProjectRepositoryPort projectRepository, CurrentUserPort currentUserPort) {
        return new ProjectAccessServiceImpl(projectRepository, currentUserPort);
    }

    @Bean
    public ProjectDeletionService projectDeletionService(ProjectRepositoryPort projectRepositoryPort,
                                                         GceRepositoryPort gceRepositoryPort) {
        return new ProjectDeletionServiceImpl(projectRepositoryPort, gceRepositoryPort);
    }

    @Bean
    public UserAuthorizationService userAuthorizationService(CurrentUserPort currentUserPort) {
        return new UserAuthorizationServiceImpl(currentUserPort);
    }

    @Bean
    public CreateProjectUseCasePort createProjectUseCase(ProjectRepositoryPort projectRepository, CurrentUserPort currentUserPort) {
        return new CreateProjectUseCaseImpl(projectRepository, currentUserPort);
    }

    @Bean
    public DeleteProjectUseCasePort deleteProjectUseCase(ProjectAccessService projectAccessService,
                                                         ProjectDeletionService projectDeletionService) {
        return new DeleteProjectUseCaseImpl(projectAccessService, projectDeletionService);
    }

    @Bean
    public FindProjectByIdUseCasePort findProjectByIdUseCase(ProjectAccessService projectAccessService) {
        return new FindProjectByIdUseCaseImpl(projectAccessService);
    }

    @Bean
    public ListProjectsUseCasePort listProjectsUseCase(ProjectRepositoryPort projectRepositoryPort, CurrentUserPort currentUserPort) {
        return new ListProjectsUseCaseImpl(projectRepositoryPort, currentUserPort);
    }

    @Bean
    public ListProjectsByUserUseCasePort listProjectsByUserUseCase(ProjectRepositoryPort projectRepositoryPort, CurrentUserPort currentUserPort) {
        return new ListProjectsByUserUseCaseImpl(projectRepositoryPort, currentUserPort);
    }

    @Bean
    public UpdateProjectUseCasePort updateProjectUseCase(ProjectRepositoryPort projectRepositoryPort, ProjectAccessService projectAccessService) {
        return new UpdateProjectUseCaseImpl(projectRepositoryPort, projectAccessService);
    }

    @Bean
    public GenerateTokenUseCasePort generateTokenUseCase(TokenServicePort tokenServicePort) {
        return new GenerateTokenUseCaseImpl(tokenServicePort);
    }

    @Bean
    public LoadAuthenticationUserUseCasePort loadAuthenticationUserUseCase(UserRepositoryPort userRepository) {
        return new LoadAuthenticationUserUseCaseImpl(userRepository);
    }

    @Bean
    public AuthenticatedUserByTokenUseCasePort resolveAuthenticatedUserByTokenUseCase(TokenServicePort tokenServicePort,
                                                                                      UserRepositoryPort userRepository) {
        return new AuthenticatedUserByTokenUseCaseImpl(tokenServicePort, userRepository);
    }

    @Bean
    public VerifyTokenUseCasePort verifyTokenUseCase(TokenServicePort tokenServicePort) {
        return new VerifyTokenUseCaseImpl(tokenServicePort);
    }

    @Bean
    public CreateUserUseCasePort createUserUseCase(UserRepositoryPort userRepository, PasswordEncoderPort passwordEncoderPort) {
        return new CreateUserUseCaseImpl(userRepository, passwordEncoderPort);
    }

    @Bean
    public DeleteUserUseCasePort deleteUserUseCase(UserRepositoryPort userRepository,
                                                   UserAuthorizationService userAuthorizationService,
                                                   ProjectDeletionService projectDeletionService) {
        return new DeleteUserUseCaseImpl(userRepository, userAuthorizationService, projectDeletionService);
    }

    @Bean
    public FindUserByEmailUseCasePort findUserByEmailUseCase(UserRepositoryPort userRepository) {
        return new FindUserByEmailUseCaseImpl(userRepository);
    }

    @Bean
    public FindUserByIdUseCasePort findUserByIdUseCase(UserRepositoryPort userRepository,
                                                       UserAuthorizationService userAuthorizationService) {
        return new FindUserByIdUseCaseImpl(userRepository, userAuthorizationService);
    }

    @Bean
    public ListUsersUseCasePort listUsersUseCase(UserRepositoryPort userRepository,
                                                 UserAuthorizationService userAuthorizationService) {
        return new ListUsersUseCaseImpl(userRepository, userAuthorizationService);
    }

    @Bean
    public UpdateUserPasswordUseCasePort updateUserPasswordUseCase(UserRepositoryPort userRepository,
                                                                   PasswordEncoderPort passwordEncoderPort,
                                                                   UserAuthorizationService userAuthorizationService) {
        return new UpdateUserPasswordUseCaseImpl(userRepository, passwordEncoderPort, userAuthorizationService);
    }

    @Bean
    public UpdateUserUseCasePort updateUserUseCase(UserRepositoryPort userRepository,
                                                   UserAuthorizationService userAuthorizationService) {
        return new UpdateUserUseCaseImpl(userRepository, userAuthorizationService);
    }
}
