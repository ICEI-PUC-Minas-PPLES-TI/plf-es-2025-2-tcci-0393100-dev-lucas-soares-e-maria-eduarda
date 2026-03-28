package br.pucminas.graphtest.infrastructure.config;

import br.pucminas.graphtest.application.port.input.project.CreateProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.project.DeleteProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.project.FindProjectByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.project.ListProjectsByUserUseCasePort;
import br.pucminas.graphtest.application.port.input.project.ListProjectsUseCasePort;
import br.pucminas.graphtest.application.port.input.project.UpdateProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.CreateGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.FindGceByIdUseCasePort;
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
import br.pucminas.graphtest.adapters.outbound.repositories.GceRepositoryPortImpl;
import br.pucminas.graphtest.adapters.outbound.repositories.interfaces.Neo4jGceRepository;
import br.pucminas.graphtest.adapters.outbound.repositories.mappers.GceMapper;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.UserRepositoryPort;
import br.pucminas.graphtest.application.port.output.security.CurrentUserPort;
import br.pucminas.graphtest.application.port.output.security.PasswordEncoderPort;
import br.pucminas.graphtest.application.port.output.security.TokenServicePort;
import br.pucminas.graphtest.application.service.GceValidationResultServiceImpl;
import br.pucminas.graphtest.application.service.ProjectAccessServiceImpl;
import br.pucminas.graphtest.application.service.UserAuthorizationServiceImpl;
import br.pucminas.graphtest.application.service.interfaces.GceValidationResultService;
import br.pucminas.graphtest.application.service.interfaces.ProjectAccessService;
import br.pucminas.graphtest.application.service.interfaces.UserAuthorizationService;
import br.pucminas.graphtest.application.usecases.gce.CreateGceUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gce.FindGceByIdUseCaseImpl;
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
    public GceRepositoryPort gceRepositoryPort(Neo4jGceRepository neo4jGceRepository, GceMapper gceMapper) {
        return new GceRepositoryPortImpl(neo4jGceRepository, gceMapper);
    }

    @Bean
    public GceValidationResultService gceValidationResultService() {
        return new GceValidationResultServiceImpl();
    }

    @Bean
    public CreateGceUseCasePort createGceUseCase(GceRepositoryPort gceRepositoryPort,
                                                 GceValidationResultService gceValidationResultService) {
        return new CreateGceUseCaseImpl(gceRepositoryPort, gceValidationResultService);
    }

    @Bean
    public FindGceByIdUseCasePort findGceByIdUseCase(GceRepositoryPort gceRepositoryPort) {
        return new FindGceByIdUseCaseImpl(gceRepositoryPort);
    }

    @Bean
    public ValidateGceUseCasePort validateGceUseCase(GceValidationResultService gceValidationResultService) {
        return new ValidateGceUseCaseImpl(gceValidationResultService);
    }

    @Bean
    public ProjectAccessService projectAccessService(ProjectRepositoryPort projectRepository, CurrentUserPort currentUserPort) {
        return new ProjectAccessServiceImpl(projectRepository, currentUserPort);
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
    public DeleteProjectUseCasePort deleteProjectUseCase(ProjectRepositoryPort projectRepositoryPort, ProjectAccessService projectAccessService) {
        return new DeleteProjectUseCaseImpl(projectRepositoryPort, projectAccessService);
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
                                                   ProjectRepositoryPort projectRepositoryPort) {
        return new DeleteUserUseCaseImpl(userRepository, userAuthorizationService, projectRepositoryPort);
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
