package br.pucminas.graphtest.application.usecases.decisiontable;

import br.pucminas.graphtest.application.port.input.decisiontable.ListDecisionTablesUseCasePort;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableOutput;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepositoryPort;
import br.pucminas.graphtest.application.port.output.security.CurrentUserPort;
import br.pucminas.graphtest.application.security.AuthenticatedUser;

import java.util.List;

public class ListDecisionTablesUseCaseImpl implements ListDecisionTablesUseCasePort {

    private final DecisionTableRepositoryPort decisionTableRepository;
    private final ProjectRepositoryPort projectRepository;
    private final CurrentUserPort currentUserPort;

    public ListDecisionTablesUseCaseImpl(DecisionTableRepositoryPort decisionTableRepository,
                                         ProjectRepositoryPort projectRepository,
                                         CurrentUserPort currentUserPort) {
        this.decisionTableRepository = decisionTableRepository;
        this.projectRepository = projectRepository;
        this.currentUserPort = currentUserPort;
    }

    @Override
    public List<DecisionTableOutput> execute() {
        AuthenticatedUser currentUser = currentUserPort.getCurrentUser();

        return projectRepository.findAllByUserId(currentUser.id()).stream()
                .flatMap(project -> decisionTableRepository.findAllByProjectId(project.getId()).stream())
                .map(DecisionTableOutput::from)
                .toList();
    }
}
