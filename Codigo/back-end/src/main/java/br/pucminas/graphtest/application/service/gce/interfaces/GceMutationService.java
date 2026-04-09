package br.pucminas.graphtest.application.service.gce.interfaces;

import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gce.model.GceEdge;
import br.pucminas.graphtest.application.domain.gce.model.GceNode;
import br.pucminas.graphtest.application.domain.gce.model.GceRestriction;
import br.pucminas.graphtest.application.port.input.gce.records.GceEdgeInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceNodeInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceRestrictionInput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Servico de aplicacao de apoio para mutacoes do GCE.
 */
public interface GceMutationService {

    Gce loadAuthorizedGraph(UUID id, GceRepositoryPort gceRepository, ProjectAccessService projectAccessService);

    void validateAndThrow(Gce graph, GceValidationResultService validationService);

    Collection<GceNode> toNodes(List<GceNodeInput> nodes);

    Collection<GceEdge> toEdges(List<GceNodeInput> nodes, List<GceEdgeInput> explicitEdges);

    Collection<GceRestriction> toRestrictions(List<GceRestrictionInput> restrictions);

    void addNodeWithAutomaticEdges(Gce graph, GceNodeInput nodeInput);
}
