package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.domain.Gce;
import br.pucminas.graphtest.application.domain.GceEdge;
import br.pucminas.graphtest.application.domain.GceNode;
import br.pucminas.graphtest.application.domain.GceRestriction;
import br.pucminas.graphtest.application.exception.ConflictException;
import br.pucminas.graphtest.application.port.input.gce.CreateGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.records.CreateGceInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceEdgeInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceNodeInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.input.gce.records.GceRestrictionInput;
import br.pucminas.graphtest.application.port.input.gce.records.ValidationGceOutput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.interfaces.GceValidationResultService;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Caso de uso responsavel por criar um novo GCE.
 */
public class CreateGceUseCaseImpl implements CreateGceUseCasePort {

    private final GceRepositoryPort gceRepository;
    private final GceValidationResultService gceValidationResultService;

    /**
     * Cria o caso de uso com as dependencias necessarias para validar e persistir um GCE.
     *
     * @param gceRepository repositorio responsavel pela persistencia do agregado
     * @param gceValidationResultService servico responsavel pela validacao do modelo
     */
    public CreateGceUseCaseImpl(GceRepositoryPort gceRepository, GceValidationResultService gceValidationResultService) {
        this.gceRepository = gceRepository;
        this.gceValidationResultService = gceValidationResultService;
    }

    /**
     * Cria, valida e persiste um novo GCE.
     *
     * @param input dados de entrada do grafo
     * @return representacao do GCE persistido
     */
    @Override
    public GceOutput execute(CreateGceInput input) {
        Gce graph = new Gce(
                null,
                input.projectId(),
                input.name(),
                input.description(),
                Boolean.TRUE.equals(input.selected()),
                toNodes(input.nodes()),
                toEdges(input.edges()),
                toRestrictions(input.restrictions())
        );

        ValidationGceOutput validation = gceValidationResultService.validate(graph);
        if (!validation.valid()) {
            throw new InvalidGceModelException("GCE invalido: " + validation.errors());
        }

        return GceOutput.from(gceRepository.save(graph));
    }

    private Collection<GceNode> toNodes(List<GceNodeInput> nodes) {
        if (nodes == null) {
            return List.of();
        }

        return nodes.stream()
                .map(node -> new GceNode(null, node.code(), node.label(), node.type(), node.operatorType()))
                .toList();
    }

    private Collection<GceEdge> toEdges(List<GceEdgeInput> edges) {
        if (edges == null) {
            return List.of();
        }

        return edges.stream()
                .map(edge -> new GceEdge(
                        UUID.randomUUID(),
                        edge.sourceNodeCode(),
                        edge.targetNodeCode(),
                        edge.type()
                ))
                .toList();
    }

    private Collection<GceRestriction> toRestrictions(List<GceRestrictionInput> restrictions) {
        if (restrictions == null) {
            return List.of();
        }

        return restrictions.stream()
                .map(restriction -> new GceRestriction(
                        null,
                        restriction.type(),
                        restriction.nodeCodes()
                ))
                .toList();
    }

    /**
     * Excecao de conflito usada quando o modelo do GCE nao passa na validacao.
     */
    private static final class InvalidGceModelException extends ConflictException {

        private InvalidGceModelException(String message) {
            super(message);
        }
    }
}
