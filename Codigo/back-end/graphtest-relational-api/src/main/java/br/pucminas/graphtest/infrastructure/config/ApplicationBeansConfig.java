package br.pucminas.graphtest.infrastructure.config;

import br.pucminas.graphtest.application.port.input.project.CreateProjectUseCase;
import br.pucminas.graphtest.application.port.input.project.DeleteProjectUseCase;
import br.pucminas.graphtest.application.port.input.project.FindProjectByIdUseCase;
import br.pucminas.graphtest.application.port.input.project.ListProjectsByUserUseCase;
import br.pucminas.graphtest.application.port.input.project.ListProjectsUseCase;
import br.pucminas.graphtest.application.port.input.project.UpdateProjectUseCase;
import br.pucminas.graphtest.application.port.input.security.GenerateTokenUseCase;
import br.pucminas.graphtest.application.port.input.security.LoadAuthenticationUserUseCase;
import br.pucminas.graphtest.application.port.input.security.ResolveAuthenticatedUserByTokenUseCase;
import br.pucminas.graphtest.application.port.input.security.VerifyTokenUseCase;
import br.pucminas.graphtest.application.port.input.user.CreateUserUseCase;
import br.pucminas.graphtest.application.port.input.user.DeleteUserUseCase;
import br.pucminas.graphtest.application.port.input.user.FindUserByEmailUseCase;
import br.pucminas.graphtest.application.port.input.user.FindUserByIdUseCase;
import br.pucminas.graphtest.application.port.input.user.ListUsersUseCase;
import br.pucminas.graphtest.application.port.input.user.UpdateUserPasswordUseCase;
import br.pucminas.graphtest.application.port.input.user.UpdateUserUseCase;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepository;
import br.pucminas.graphtest.application.port.output.repositories.UserRepository;
import br.pucminas.graphtest.application.port.output.security.CurrentUserPort;
import br.pucminas.graphtest.application.port.output.security.PasswordEncoderPort;
import br.pucminas.graphtest.application.port.output.security.TokenServicePort;
import br.pucminas.graphtest.application.service.ProjectAccessServiceImpl;
import br.pucminas.graphtest.application.service.UserAuthorizationServiceImpl;
import br.pucminas.graphtest.application.service.interfaces.ProjectAccessService;
import br.pucminas.graphtest.application.service.interfaces.UserAuthorizationService;
import br.pucminas.graphtest.application.usecases.project.CreateProjectUseCaseImpl;
import br.pucminas.graphtest.application.usecases.project.DeleteProjectUseCaseImpl;
import br.pucminas.graphtest.application.usecases.project.FindProjectByIdUseCaseImpl;
import br.pucminas.graphtest.application.usecases.project.ListProjectsByUserUseCaseImpl;
import br.pucminas.graphtest.application.usecases.project.ListProjectsUseCaseImpl;
import br.pucminas.graphtest.application.usecases.project.UpdateProjectUseCaseImpl;
import br.pucminas.graphtest.application.usecases.security.GenerateTokenUseCaseImpl;
import br.pucminas.graphtest.application.usecases.security.LoadAuthenticationUserUseCaseImpl;
import br.pucminas.graphtest.application.usecases.security.ResolveAuthenticatedUserByTokenUseCaseImpl;
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
    public ProjectAccessService projectAccessService(ProjectRepository projectRepository, CurrentUserPort currentUserPort) {
        return new ProjectAccessServiceImpl(projectRepository, currentUserPort);
    }

    @Bean
    public UserAuthorizationService userAuthorizationService(CurrentUserPort currentUserPort) {
        return new UserAuthorizationServiceImpl(currentUserPort);
    }

    @Bean
    public CreateProjectUseCase createProjectUseCase(ProjectRepository projectRepository, CurrentUserPort currentUserPort) {
        return new CreateProjectUseCaseImpl(projectRepository, currentUserPort);
    }

    @Bean
    public DeleteProjectUseCase deleteProjectUseCase(ProjectRepository projectRepository, ProjectAccessService projectAccessService) {
        return new DeleteProjectUseCaseImpl(projectRepository, projectAccessService);
    }

    @Bean
    public FindProjectByIdUseCase findProjectByIdUseCase(ProjectAccessService projectAccessService) {
        return new FindProjectByIdUseCaseImpl(projectAccessService);
    }

    @Bean
    public ListProjectsUseCase listProjectsUseCase(ProjectRepository projectRepository, CurrentUserPort currentUserPort) {
        return new ListProjectsUseCaseImpl(projectRepository, currentUserPort);
    }

    @Bean
    public ListProjectsByUserUseCase listProjectsByUserUseCase(ProjectRepository projectRepository, CurrentUserPort currentUserPort) {
        return new ListProjectsByUserUseCaseImpl(projectRepository, currentUserPort);
    }

    @Bean
    public UpdateProjectUseCase updateProjectUseCase(ProjectRepository projectRepository, ProjectAccessService projectAccessService) {
        return new UpdateProjectUseCaseImpl(projectRepository, projectAccessService);
    }

    @Bean
    public GenerateTokenUseCase generateTokenUseCase(TokenServicePort tokenServicePort) {
        return new GenerateTokenUseCaseImpl(tokenServicePort);
    }

    @Bean
    public LoadAuthenticationUserUseCase loadAuthenticationUserUseCase(UserRepository userRepository) {
        return new LoadAuthenticationUserUseCaseImpl(userRepository);
    }

    @Bean
    public ResolveAuthenticatedUserByTokenUseCase resolveAuthenticatedUserByTokenUseCase(TokenServicePort tokenServicePort,
                                                                                         UserRepository userRepository) {
        return new ResolveAuthenticatedUserByTokenUseCaseImpl(tokenServicePort, userRepository);
    }

    @Bean
    public VerifyTokenUseCase verifyTokenUseCase(TokenServicePort tokenServicePort) {
        return new VerifyTokenUseCaseImpl(tokenServicePort);
    }

    @Bean
    public CreateUserUseCase createUserUseCase(UserRepository userRepository, PasswordEncoderPort passwordEncoderPort) {
        return new CreateUserUseCaseImpl(userRepository, passwordEncoderPort);
    }

    @Bean
    public DeleteUserUseCase deleteUserUseCase(UserRepository userRepository,
                                               UserAuthorizationService userAuthorizationService,
                                               ProjectRepository projectRepository) {
        return new DeleteUserUseCaseImpl(userRepository, userAuthorizationService, projectRepository);
    }

    @Bean
    public FindUserByEmailUseCase findUserByEmailUseCase(UserRepository userRepository) {
        return new FindUserByEmailUseCaseImpl(userRepository);
    }

    @Bean
    public FindUserByIdUseCase findUserByIdUseCase(UserRepository userRepository,
                                                   UserAuthorizationService userAuthorizationService) {
        return new FindUserByIdUseCaseImpl(userRepository, userAuthorizationService);
    }

    @Bean
    public ListUsersUseCase listUsersUseCase(UserRepository userRepository,
                                             UserAuthorizationService userAuthorizationService) {
        return new ListUsersUseCaseImpl(userRepository, userAuthorizationService);
    }

    @Bean
    public UpdateUserPasswordUseCase updateUserPasswordUseCase(UserRepository userRepository,
                                                               PasswordEncoderPort passwordEncoderPort,
                                                               UserAuthorizationService userAuthorizationService) {
        return new UpdateUserPasswordUseCaseImpl(userRepository, passwordEncoderPort, userAuthorizationService);
    }

    @Bean
    public UpdateUserUseCase updateUserUseCase(UserRepository userRepository,
                                               UserAuthorizationService userAuthorizationService) {
        return new UpdateUserUseCaseImpl(userRepository, userAuthorizationService);
    }
}
