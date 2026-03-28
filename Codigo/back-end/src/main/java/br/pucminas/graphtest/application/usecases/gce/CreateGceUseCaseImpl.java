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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        Map<String, UUID> nodeIdsByCode = createNodeIdsByCode(input.nodes());

        Gce graph = new Gce(
                UUID.randomUUID(),
                input.projectId(),
                input.name(),
                input.description(),
                Boolean.TRUE.equals(input.selected()),
                toNodes(input.nodes(), nodeIdsByCode),
                toEdges(input.edges(), nodeIdsByCode),
                toRestrictions(input.restrictions(), nodeIdsByCode)
        );

        ValidationGceOutput validation = gceValidationResultService.validate(graph);
        if (!validation.valid()) {
            throw new InvalidGceModelException("GCE invalido: " + validation.errors());
        }

        return GceOutput.from(gceRepository.save(graph));
    }

    private Map<String, UUID> createNodeIdsByCode(List<GceNodeInput> nodes) {
        Map<String, UUID> nodeIdsByCode = new LinkedHashMap<>();
        if (nodes == null) {
            return nodeIdsByCode;
        }

        for (GceNodeInput node : nodes) {
            if (node == null) {
                throw new InvalidGceModelException("Payload de nodes nao pode conter itens nulos.");
            }
            nodeIdsByCode.put(node.code(), UUID.randomUUID());
        }

        return nodeIdsByCode;
    }

    private Collection<GceNode> toNodes(List<GceNodeInput> nodes, Map<String, UUID> nodeIdsByCode) {
        if (nodes == null) {
            return List.of();
        }

        return nodes.stream()
                .map(node -> new GceNode(resolveNodeId(nodeIdsByCode, node.code()), node.code(), node.label(), node.type(), node.operatorType()))
                .toList();
    }

    private Collection<GceEdge> toEdges(List<GceEdgeInput> edges, Map<String, UUID> nodeIdsByCode) {
        if (edges == null) {
            return List.of();
        }

        return edges.stream()
                .map(edge -> new GceEdge(
                        UUID.randomUUID(),
                        resolveNodeId(nodeIdsByCode, edge.sourceNodeCode()),
                        resolveNodeId(nodeIdsByCode, edge.targetNodeCode()),
                        edge.type()
                ))
                .toList();
    }

    private Collection<GceRestriction> toRestrictions(List<GceRestrictionInput> restrictions, Map<String, UUID> nodeIdsByCode) {
        if (restrictions == null) {
            return List.of();
        }

        return restrictions.stream()
                .map(restriction -> new GceRestriction(
                        UUID.randomUUID(),
                        restriction.type(),
                        restriction.nodeCodes().stream()
                                .map(code -> resolveNodeId(nodeIdsByCode, code))
                                .toList()
                ))
                .toList();
    }

    private UUID resolveNodeId(Map<String, UUID> nodeIdsByCode, String nodeCode) {
        UUID nodeId = nodeIdsByCode.get(nodeCode);
        if (nodeId == null) {
            throw new InvalidGceModelException("Referencia para no inexistente no payload: " + nodeCode);
        }
        return nodeId;
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
