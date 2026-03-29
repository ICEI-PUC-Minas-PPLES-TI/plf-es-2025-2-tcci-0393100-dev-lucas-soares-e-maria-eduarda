package br.pucminas.graphtest.application.service.interfaces;

import br.pucminas.graphtest.application.domain.Gce;
import br.pucminas.graphtest.application.domain.GceEdge;
import br.pucminas.graphtest.application.domain.GceNode;
import br.pucminas.graphtest.application.domain.GceRestriction;
import br.pucminas.graphtest.application.port.input.gce.records.GceEdgeInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceNodeInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceRestrictionInput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;

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
