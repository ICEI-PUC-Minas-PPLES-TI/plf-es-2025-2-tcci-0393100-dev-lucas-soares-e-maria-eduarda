package br.pucminas.graphtest.infrastructure.config;

import br.pucminas.graphtest.application.port.input.decisiontable.DeleteDecisionTableByGceIdUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.DeleteDecisionTableByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.FindDecisionTableByGceIdUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.FindDecisionTableByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.FindDecisionTableStatusByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.GenerateDecisionTableUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.GenerateFunctionalTestSignatureUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.ListDecisionTablesUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.ListDecisionTablesByProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.PatchDecisionTableDetailsUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.PreviewDecisionTableUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.RefreshDecisionTableUseCasePort;
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
import br.pucminas.graphtest.application.port.input.gce.PatchGceDetailsUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.AddNodeToGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.ToggleGceEdgeUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.UpdateGceNodeUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.UpdateGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.ValidateGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.CreateGfcSourceFileUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.CalculateCyclomaticComplexityUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.CreateGfcUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.DeleteGfcSourceFileUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.DeleteGfcUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.FindGfcByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.GenerateStructuralTestSignatureUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.FindGfcSourceFileByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.GetGfcSourceCodeUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.GetGfcSourceMethodDetailsUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.ListGfcByProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.ListGfcSourceFilesByProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.ListGfcSourceMethodsUseCasePort;
import br.pucminas.graphtest.application.port.input.gfc.PreviewGfcUseCasePort;
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
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GfcSourceFileRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GfcRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.UserRepositoryPort;
import br.pucminas.graphtest.application.port.output.security.CurrentUserPort;
import br.pucminas.graphtest.application.port.output.security.PasswordEncoderPort;
import br.pucminas.graphtest.application.port.output.security.TokenServicePort;
import br.pucminas.graphtest.application.service.decisiontable.DecisionTableDerivationServiceImpl;
import br.pucminas.graphtest.application.service.decisiontable.DecisionTableSyncServiceImpl;
import br.pucminas.graphtest.application.service.decisiontable.DecisionTableSyncStatusUpdateServiceImpl;
import br.pucminas.graphtest.application.service.decisiontable.interfaces.DecisionTableDerivationService;
import br.pucminas.graphtest.application.service.decisiontable.interfaces.DecisionTableSyncService;
import br.pucminas.graphtest.application.service.decisiontable.interfaces.DecisionTableSyncStatusUpdateService;
import br.pucminas.graphtest.application.service.gce.GceValidationResultServiceImpl;
import br.pucminas.graphtest.application.service.gfc.GfcGenerationServiceImpl;
import br.pucminas.graphtest.application.service.gfc.GfcPreviewGenerationServiceImpl;
import br.pucminas.graphtest.application.service.gfc.GfcSourceMethodDetailsServiceImpl;
import br.pucminas.graphtest.application.service.gfc.GfcSourceMethodListingServiceImpl;
import br.pucminas.graphtest.application.service.gfc.interfaces.GfcGenerationService;
import br.pucminas.graphtest.application.service.gfc.interfaces.GfcPreviewGenerationService;
import br.pucminas.graphtest.application.service.gfc.interfaces.GfcSourceMethodDetailsService;
import br.pucminas.graphtest.application.service.gfc.interfaces.GfcSourceMethodListingService;
import br.pucminas.graphtest.application.service.gfc.parser.JavaSourceParser;
import br.pucminas.graphtest.application.service.project.ProjectAccessServiceImpl;
import br.pucminas.graphtest.application.service.project.ProjectDeletionServiceImpl;
import br.pucminas.graphtest.application.service.user.UserAuthorizationServiceImpl;
import br.pucminas.graphtest.application.service.user.UserEmailUniquenessServiceImpl;
import br.pucminas.graphtest.application.service.gce.GceMutationServiceImpl;
import br.pucminas.graphtest.application.service.gce.interfaces.GceMutationService;
import br.pucminas.graphtest.application.service.gce.interfaces.GceValidationResultService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectDeletionService;
import br.pucminas.graphtest.application.service.user.interfaces.UserAuthorizationService;
import br.pucminas.graphtest.application.service.user.interfaces.UserEmailUniquenessService;
import br.pucminas.graphtest.application.usecases.gce.CreateGceUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gce.DeleteGceUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gce.FindGceByIdUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gce.ListGcesUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gce.ListGcesByProjectUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gce.PatchGceDetailsUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gce.AddNodeToGceUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gce.ToggleGceEdgeUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gce.UpdateGceNodeUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gce.UpdateGceUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gce.ValidateGceUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gfc.CreateGfcSourceFileUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gfc.CalculateCyclomaticComplexityUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gfc.CreateGfcUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gfc.DeleteGfcSourceFileUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gfc.DeleteGfcUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gfc.FindGfcByIdUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gfc.GenerateStructuralTestSignatureUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gfc.FindGfcSourceFileByIdUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gfc.GetGfcSourceCodeUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gfc.GetGfcSourceMethodDetailsUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gfc.ListGfcByProjectUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gfc.ListGfcSourceFilesByProjectUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gfc.ListGfcSourceMethodsUseCaseImpl;
import br.pucminas.graphtest.application.usecases.gfc.PreviewGfcUseCaseImpl;
import br.pucminas.graphtest.application.usecases.decisiontable.DeleteDecisionTableByGceIdUseCaseImpl;
import br.pucminas.graphtest.application.usecases.decisiontable.DeleteDecisionTableByIdUseCaseImpl;
import br.pucminas.graphtest.application.usecases.decisiontable.FindDecisionTableByGceIdUseCaseImpl;
import br.pucminas.graphtest.application.usecases.decisiontable.FindDecisionTableByIdUseCaseImpl;
import br.pucminas.graphtest.application.usecases.decisiontable.FindDecisionTableStatusByIdUseCaseImpl;
import br.pucminas.graphtest.application.usecases.decisiontable.GenerateDecisionTableUseCaseImpl;
import br.pucminas.graphtest.application.usecases.decisiontable.GenerateFunctionalTestSignatureUseCaseImpl;
import br.pucminas.graphtest.application.usecases.decisiontable.ListDecisionTablesUseCaseImpl;
import br.pucminas.graphtest.application.usecases.decisiontable.ListDecisionTablesByProjectUseCaseImpl;
import br.pucminas.graphtest.application.usecases.decisiontable.PatchDecisionTableDetailsUseCaseImpl;
import br.pucminas.graphtest.application.usecases.decisiontable.PreviewDecisionTableUseCaseImpl;
import br.pucminas.graphtest.application.usecases.decisiontable.RefreshDecisionTableUseCaseImpl;
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
    public JavaSourceParser javaSourceParser() {
        return new JavaSourceParser();
    }

    @Bean
    public GfcGenerationService gfcGenerationService(JavaSourceParser javaSourceParser) {
        return new GfcGenerationServiceImpl(javaSourceParser);
    }

    @Bean
    public GfcPreviewGenerationService gfcPreviewGenerationService(GfcGenerationService gfcGenerationService) {
        return new GfcPreviewGenerationServiceImpl(gfcGenerationService);
    }

    @Bean
    public GfcSourceMethodListingService gfcSourceMethodListingService(JavaSourceParser javaSourceParser) {
        return new GfcSourceMethodListingServiceImpl(javaSourceParser);
    }

    @Bean
    public GfcSourceMethodDetailsService gfcSourceMethodDetailsService(JavaSourceParser javaSourceParser) {
        return new GfcSourceMethodDetailsServiceImpl(javaSourceParser);
    }

    @Bean
    public DecisionTableDerivationService decisionTableDerivationService() {
        return new DecisionTableDerivationServiceImpl();
    }

    @Bean
    public DecisionTableSyncService decisionTableSyncService() {
        return new DecisionTableSyncServiceImpl();
    }

    @Bean
    public DecisionTableSyncStatusUpdateService decisionTableSyncStatusUpdateService(DecisionTableRepositoryPort decisionTableRepositoryPort) {
        return new DecisionTableSyncStatusUpdateServiceImpl(decisionTableRepositoryPort);
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
                                                 DecisionTableRepositoryPort decisionTableRepositoryPort,
                                                 ProjectAccessService projectAccessService,
                                                 GceMutationService gceMutationService) {
        return new DeleteGceUseCaseImpl(
                gceRepositoryPort,
                decisionTableRepositoryPort,
                projectAccessService,
                gceMutationService
        );
    }

    @Bean
    public ValidateGceUseCasePort validateGceUseCase(ProjectAccessService projectAccessService,
                                                     GceValidationResultService gceValidationResultService,
                                                     GceMutationService gceMutationService) {
        return new ValidateGceUseCaseImpl(projectAccessService, gceValidationResultService, gceMutationService);
    }

    @Bean
    public PatchGceDetailsUseCasePort patchGceDetailsUseCase(GceRepositoryPort gceRepositoryPort,
                                                             ProjectAccessService projectAccessService,
                                                             GceMutationService gceMutationService) {
        return new PatchGceDetailsUseCaseImpl(gceRepositoryPort, projectAccessService, gceMutationService);
    }

    @Bean
    public UpdateGceUseCasePort updateGceUseCase(GceRepositoryPort gceRepositoryPort,
                                                 ProjectAccessService projectAccessService,
                                                 GceValidationResultService gceValidationResultService,
                                                 GceMutationService gceMutationService,
                                                 DecisionTableSyncStatusUpdateService decisionTableSyncStatusUpdateService) {
        return new UpdateGceUseCaseImpl(gceRepositoryPort, projectAccessService, gceValidationResultService, gceMutationService, decisionTableSyncStatusUpdateService);
    }

    @Bean
    public AddNodeToGceUseCasePort addNodeToGceUseCase(GceRepositoryPort gceRepositoryPort,
                                                       ProjectAccessService projectAccessService,
                                                       GceValidationResultService gceValidationResultService,
                                                       GceMutationService gceMutationService,
                                                       DecisionTableSyncStatusUpdateService decisionTableSyncStatusUpdateService) {
        return new AddNodeToGceUseCaseImpl(gceRepositoryPort, projectAccessService, gceValidationResultService, gceMutationService, decisionTableSyncStatusUpdateService);
    }

    @Bean
    public UpdateGceNodeUseCasePort updateGceNodeUseCase(GceRepositoryPort gceRepositoryPort,
                                                         ProjectAccessService projectAccessService,
                                                         GceValidationResultService gceValidationResultService,
                                                         GceMutationService gceMutationService,
                                                         DecisionTableSyncStatusUpdateService decisionTableSyncStatusUpdateService) {
        return new UpdateGceNodeUseCaseImpl(gceRepositoryPort, projectAccessService, gceValidationResultService, gceMutationService, decisionTableSyncStatusUpdateService);
    }

    @Bean
    public ToggleGceEdgeUseCasePort toggleGceEdgeUseCase(GceRepositoryPort gceRepositoryPort,
                                                         ProjectAccessService projectAccessService,
                                                         GceValidationResultService gceValidationResultService,
                                                         GceMutationService gceMutationService,
                                                         DecisionTableSyncStatusUpdateService decisionTableSyncStatusUpdateService) {
        return new ToggleGceEdgeUseCaseImpl(gceRepositoryPort, projectAccessService, gceValidationResultService, gceMutationService, decisionTableSyncStatusUpdateService);
    }

    @Bean
    public ListGfcSourceMethodsUseCasePort listGfcSourceMethodsUseCase(ProjectAccessService projectAccessService,
                                                                       GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort,
                                                                       GfcSourceMethodListingService gfcSourceMethodListingService) {
        return new ListGfcSourceMethodsUseCaseImpl(projectAccessService, gfcSourceFileRepositoryPort, gfcSourceMethodListingService);
    }

    @Bean
    public CreateGfcUseCasePort createGfcUseCase(ProjectAccessService projectAccessService,
                                                 GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort,
                                                 GfcGenerationService gfcGenerationService,
                                                 GfcRepositoryPort gfcRepositoryPort) {
        return new CreateGfcUseCaseImpl(
                projectAccessService,
                gfcSourceFileRepositoryPort,
                gfcGenerationService,
                gfcRepositoryPort
        );
    }

    @Bean
    public FindGfcByIdUseCasePort findGfcByIdUseCase(GfcRepositoryPort gfcRepositoryPort,
                                                     ProjectAccessService projectAccessService) {
        return new FindGfcByIdUseCaseImpl(gfcRepositoryPort, projectAccessService);
    }

    @Bean
    public CalculateCyclomaticComplexityUseCasePort calculateCyclomaticComplexityUseCase(GfcRepositoryPort gfcRepositoryPort,
                                                                                        ProjectAccessService projectAccessService) {
        return new CalculateCyclomaticComplexityUseCaseImpl(gfcRepositoryPort, projectAccessService);
    }

    @Bean
    public GenerateStructuralTestSignatureUseCasePort generateStructuralTestSignatureUseCase(
            GfcRepositoryPort gfcRepositoryPort,
            ProjectAccessService projectAccessService,
            CalculateCyclomaticComplexityUseCasePort calculateCyclomaticComplexityUseCasePort
    ) {
        return new GenerateStructuralTestSignatureUseCaseImpl(
                gfcRepositoryPort,
                projectAccessService,
                calculateCyclomaticComplexityUseCasePort
        );
    }

    @Bean
    public DeleteGfcUseCasePort deleteGfcUseCase(GfcRepositoryPort gfcRepositoryPort,
                                                 ProjectAccessService projectAccessService) {
        return new DeleteGfcUseCaseImpl(gfcRepositoryPort, projectAccessService);
    }

    @Bean
    public ListGfcByProjectUseCasePort listGfcByProjectUseCase(GfcRepositoryPort gfcRepositoryPort,
                                                               ProjectAccessService projectAccessService) {
        return new ListGfcByProjectUseCaseImpl(gfcRepositoryPort, projectAccessService);
    }

    @Bean
    public CreateGfcSourceFileUseCasePort createGfcSourceFileUseCase(ProjectAccessService projectAccessService,
                                                                     GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort) {
        return new CreateGfcSourceFileUseCaseImpl(projectAccessService, gfcSourceFileRepositoryPort);
    }

    @Bean
    public FindGfcSourceFileByIdUseCasePort findGfcSourceFileByIdUseCase(GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort,
                                                                         ProjectAccessService projectAccessService) {
        return new FindGfcSourceFileByIdUseCaseImpl(gfcSourceFileRepositoryPort, projectAccessService);
    }

    @Bean
    public ListGfcSourceFilesByProjectUseCasePort listGfcSourceFilesByProjectUseCase(GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort,
                                                                                     ProjectAccessService projectAccessService) {
        return new ListGfcSourceFilesByProjectUseCaseImpl(gfcSourceFileRepositoryPort, projectAccessService);
    }

    @Bean
    public DeleteGfcSourceFileUseCasePort deleteGfcSourceFileUseCase(GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort,
                                                                     GfcRepositoryPort gfcRepositoryPort,
                                                                     ProjectAccessService projectAccessService) {
        return new DeleteGfcSourceFileUseCaseImpl(gfcSourceFileRepositoryPort, gfcRepositoryPort, projectAccessService);
    }

    @Bean
    public GetGfcSourceCodeUseCasePort getGfcSourceCodeUseCase(ProjectAccessService projectAccessService,
                                                               GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort) {
        return new GetGfcSourceCodeUseCaseImpl(projectAccessService, gfcSourceFileRepositoryPort);
    }

    @Bean
    public GetGfcSourceMethodDetailsUseCasePort getGfcSourceMethodDetailsUseCase(ProjectAccessService projectAccessService,
                                                                                 GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort,
                                                                                 GfcSourceMethodDetailsService gfcSourceMethodDetailsService) {
        return new GetGfcSourceMethodDetailsUseCaseImpl(
                projectAccessService,
                gfcSourceFileRepositoryPort,
                gfcSourceMethodDetailsService
        );
    }

    @Bean
    public PreviewGfcUseCasePort previewGfcUseCase(ProjectAccessService projectAccessService,
                                                   GfcPreviewGenerationService gfcPreviewGenerationService) {
        return new PreviewGfcUseCaseImpl(projectAccessService, gfcPreviewGenerationService);
    }

    @Bean
    public GenerateDecisionTableUseCasePort generateDecisionTableUseCase(DecisionTableRepositoryPort decisionTableRepositoryPort,
                                                                         GceRepositoryPort gceRepositoryPort,
                                                                         ProjectAccessService projectAccessService,
                                                                         GceMutationService gceMutationService,
                                                                         DecisionTableDerivationService decisionTableDerivationService) {
        return new GenerateDecisionTableUseCaseImpl(
                decisionTableRepositoryPort,
                gceRepositoryPort,
                projectAccessService,
                gceMutationService,
                decisionTableDerivationService
        );
    }

    @Bean
    public PreviewDecisionTableUseCasePort previewDecisionTableUseCase(DecisionTableRepositoryPort decisionTableRepositoryPort,
                                                                       GceRepositoryPort gceRepositoryPort,
                                                                       ProjectAccessService projectAccessService,
                                                                       GceMutationService gceMutationService,
                                                                       DecisionTableDerivationService decisionTableDerivationService) {
        return new PreviewDecisionTableUseCaseImpl(
                decisionTableRepositoryPort,
                gceRepositoryPort,
                projectAccessService,
                gceMutationService,
                decisionTableDerivationService
        );
    }

    @Bean
    public RefreshDecisionTableUseCasePort refreshDecisionTableUseCase(DecisionTableRepositoryPort decisionTableRepositoryPort,
                                                                       GceRepositoryPort gceRepositoryPort,
                                                                       ProjectAccessService projectAccessService,
                                                                       GceMutationService gceMutationService,
                                                                       DecisionTableDerivationService decisionTableDerivationService,
                                                                       DecisionTableSyncService decisionTableSyncService) {
        return new RefreshDecisionTableUseCaseImpl(
                decisionTableRepositoryPort,
                gceRepositoryPort,
                projectAccessService,
                gceMutationService,
                decisionTableDerivationService,
                decisionTableSyncService
        );
    }

    @Bean
    public FindDecisionTableByGceIdUseCasePort findDecisionTableByGceIdUseCase(DecisionTableRepositoryPort decisionTableRepositoryPort,
                                                                               ProjectAccessService projectAccessService) {
        return new FindDecisionTableByGceIdUseCaseImpl(
                decisionTableRepositoryPort,
                projectAccessService
        );
    }

    @Bean
    public FindDecisionTableByIdUseCasePort findDecisionTableByIdUseCase(DecisionTableRepositoryPort decisionTableRepositoryPort,
                                                                         ProjectAccessService projectAccessService) {
        return new FindDecisionTableByIdUseCaseImpl(
                decisionTableRepositoryPort,
                projectAccessService
        );
    }

    @Bean
    public GenerateFunctionalTestSignatureUseCasePort generateFunctionalTestSignatureUseCase(
            DecisionTableRepositoryPort decisionTableRepositoryPort,
            ProjectAccessService projectAccessService
    ) {
        return new GenerateFunctionalTestSignatureUseCaseImpl(decisionTableRepositoryPort, projectAccessService);
    }

    @Bean
    public FindDecisionTableStatusByIdUseCasePort findDecisionTableStatusByIdUseCase(DecisionTableRepositoryPort decisionTableRepositoryPort,
                                                                                     GceRepositoryPort gceRepositoryPort,
                                                                                     ProjectAccessService projectAccessService,
                                                                                     DecisionTableSyncService decisionTableSyncService) {
        return new FindDecisionTableStatusByIdUseCaseImpl(
                decisionTableRepositoryPort,
                gceRepositoryPort,
                projectAccessService,
                decisionTableSyncService
        );
    }

    @Bean
    public ListDecisionTablesUseCasePort listDecisionTablesUseCase(DecisionTableRepositoryPort decisionTableRepositoryPort,
                                                                   ProjectRepositoryPort projectRepositoryPort,
                                                                   CurrentUserPort currentUserPort) {
        return new ListDecisionTablesUseCaseImpl(
                decisionTableRepositoryPort,
                projectRepositoryPort,
                currentUserPort
        );
    }

    @Bean
    public ListDecisionTablesByProjectUseCasePort listDecisionTablesByProjectUseCase(DecisionTableRepositoryPort decisionTableRepositoryPort,
                                                                                     ProjectAccessService projectAccessService) {
        return new ListDecisionTablesByProjectUseCaseImpl(
                decisionTableRepositoryPort,
                projectAccessService
        );
    }

    @Bean
    public PatchDecisionTableDetailsUseCasePort patchDecisionTableDetailsUseCase(DecisionTableRepositoryPort decisionTableRepositoryPort,
                                                                                 ProjectAccessService projectAccessService) {
        return new PatchDecisionTableDetailsUseCaseImpl(decisionTableRepositoryPort, projectAccessService);
    }

    @Bean
    public DeleteDecisionTableByGceIdUseCasePort deleteDecisionTableByGceIdUseCase(DecisionTableRepositoryPort decisionTableRepositoryPort,
                                                                                   ProjectAccessService projectAccessService) {
        return new DeleteDecisionTableByGceIdUseCaseImpl(decisionTableRepositoryPort, projectAccessService);
    }

    @Bean
    public DeleteDecisionTableByIdUseCasePort deleteDecisionTableByIdUseCase(DecisionTableRepositoryPort decisionTableRepositoryPort,
                                                                             ProjectAccessService projectAccessService) {
        return new DeleteDecisionTableByIdUseCaseImpl(decisionTableRepositoryPort, projectAccessService);
    }

    @Bean
    public ProjectAccessService projectAccessService(ProjectRepositoryPort projectRepository, CurrentUserPort currentUserPort) {
        return new ProjectAccessServiceImpl(projectRepository, currentUserPort);
    }

    @Bean
    public ProjectDeletionService projectDeletionService(ProjectRepositoryPort projectRepositoryPort,
                                                         GceRepositoryPort gceRepositoryPort,
                                                         DecisionTableRepositoryPort decisionTableRepositoryPort,
                                                         GfcRepositoryPort gfcRepositoryPort,
                                                         GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort) {
        return new ProjectDeletionServiceImpl(
                projectRepositoryPort,
                gceRepositoryPort,
                decisionTableRepositoryPort,
                gfcRepositoryPort,
                gfcSourceFileRepositoryPort
        );
    }

    @Bean
    public UserAuthorizationService userAuthorizationService(CurrentUserPort currentUserPort) {
        return new UserAuthorizationServiceImpl(currentUserPort);
    }

    @Bean
    public UserEmailUniquenessService userEmailUniquenessService(UserRepositoryPort userRepository) {
        return new UserEmailUniquenessServiceImpl(userRepository);
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
    public CreateUserUseCasePort createUserUseCase(UserRepositoryPort userRepository,
                                                   PasswordEncoderPort passwordEncoderPort,
                                                   UserEmailUniquenessService userEmailUniquenessService) {
        return new CreateUserUseCaseImpl(userRepository, passwordEncoderPort, userEmailUniquenessService);
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
                                                   UserAuthorizationService userAuthorizationService,
                                                   UserEmailUniquenessService userEmailUniquenessService) {
        return new UpdateUserUseCaseImpl(userRepository, userAuthorizationService, userEmailUniquenessService);
    }
}
