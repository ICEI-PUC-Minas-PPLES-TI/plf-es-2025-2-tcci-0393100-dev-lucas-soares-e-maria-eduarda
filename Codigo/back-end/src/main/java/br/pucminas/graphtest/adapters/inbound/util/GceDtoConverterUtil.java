package br.pucminas.graphtest.adapters.inbound.util;

import br.pucminas.graphtest.adapters.inbound.dto.GceDTO;
import br.pucminas.graphtest.adapters.inbound.dto.GceInputDTO;
import br.pucminas.graphtest.adapters.inbound.dto.ValidationGceDTO;
import br.pucminas.graphtest.application.domain.Gce;
import br.pucminas.graphtest.application.domain.GceEdge;
import br.pucminas.graphtest.application.domain.GceNode;
import br.pucminas.graphtest.application.domain.GceRestriction;
import br.pucminas.graphtest.application.port.input.gce.records.CreateGceInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceEdgeInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceNodeInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.input.gce.records.GceRestrictionInput;
import br.pucminas.graphtest.application.port.input.gce.records.ValidationGceMessage;
import br.pucminas.graphtest.application.port.input.gce.records.ValidationGceOutput;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static br.pucminas.graphtest.shared.LogTopicsUtil.CONVERSOR_ENTIDADE_DTO_UTIL;
import static java.lang.String.format;

/**
 * Utilitario responsavel por converter DTOs e records do GCE entre as camadas
 * HTTP, aplicacao e dominio.
 */
@UtilityClass
@Slf4j(topic = CONVERSOR_ENTIDADE_DTO_UTIL)
public class GceDtoConverterUtil {

    public static CreateGceInput toCreateInput(@NotNull GceInputDTO graph) {
        log.info(format(">>> toCreateInput: convertendo GceInputDTO para CreateGceInput (projectId: %s)", graph.projectId()));
        return new CreateGceInput(
                graph.projectId(),
                graph.name(),
                graph.description(),
                graph.selected(),
                toNodeInputs(graph.nodes()),
                toEdgeInputs(graph.edges()),
                toRestrictionInputs(graph.restrictions())
        );
    }

    public static Gce toDomain(@NotNull GceInputDTO graph) {
        log.info(format(">>> toDomain: convertendo GceInputDTO para dominio (projectId: %s)", graph.projectId()));
        Map<String, UUID> nodeIdsByCode = createNodeIdsByCode(graph.nodes());
        return new Gce(
                UUID.randomUUID(),
                graph.projectId(),
                graph.name(),
                graph.description(),
                Boolean.TRUE.equals(graph.selected()),
                toNodeInputs(graph.nodes()).stream()
                        .map(node -> new GceNode(resolveNodeId(nodeIdsByCode, node.code()), node.code(), node.label(), node.type(), node.operatorType()))
                        .toList(),
                toEdgeInputs(graph.edges()).stream()
                        .map(edge -> new GceEdge(
                                UUID.randomUUID(),
                                resolveNodeId(nodeIdsByCode, edge.sourceNodeCode()),
                                resolveNodeId(nodeIdsByCode, edge.targetNodeCode()),
                                edge.type()
                        ))
                        .toList(),
                toRestrictionInputs(graph.restrictions()).stream()
                        .map(restriction -> new GceRestriction(
                                UUID.randomUUID(),
                                restriction.type(),
                                restriction.nodeCodes().stream()
                                        .map(code -> resolveNodeId(nodeIdsByCode, code))
                                        .toList()
                        ))
                        .toList()
        );
    }

    public static GceDTO toDto(@NotNull GceOutput graph) {
        log.info(format(">>> toDto: convertendo GceOutput para DTO (id: %s)", graph.id()));
        return GceDTO.builder()
                .id(graph.id())
                .projectId(graph.projectId())
                .name(graph.name())
                .description(graph.description())
                .selected(graph.selected())
                .nodes(graph.nodes().stream()
                        .map(node -> new GceDTO.GceNodeDTO(
                                node.id(),
                                node.code(),
                                node.label(),
                                node.type(),
                                node.operatorType()
                        ))
                        .toList())
                .edges(graph.edges().stream()
                        .map(edge -> new GceDTO.GceEdgeDTO(
                                edge.id(),
                                edge.sourceNodeId(),
                                edge.targetNodeId(),
                                edge.type()
                        ))
                        .toList())
                .restrictions(graph.restrictions().stream()
                        .map(restriction -> new GceDTO.GceRestrictionDTO(
                                restriction.id(),
                                restriction.type(),
                                restriction.nodeIds()
                        ))
                        .toList())
                .build();
    }

    public static ValidationGceDTO toDto(@NotNull ValidationGceOutput validation) {
        log.info(">>> toDto: convertendo ValidationGceOutput para DTO");
        return ValidationGceDTO.builder()
                .valid(validation.valid())
                .errors(toMessageDtos(validation.errors()))
                .warnings(toMessageDtos(validation.warnings()))
                .build();
    }

    private static List<GceNodeInput> toNodeInputs(List<GceInputDTO.GceNodeInputDTO> nodes) {
        if (nodes == null) {
            return List.of();
        }

        return nodes.stream()
                .peek(node -> {
                    if (node == null) {
                        throw new IllegalArgumentException("Payload de nodes nao pode conter itens nulos.");
                    }
                })
                .map(node -> new GceNodeInput(node.code(), node.label(), node.type(), node.operatorType()))
                .toList();
    }

    private static List<GceEdgeInput> toEdgeInputs(List<GceInputDTO.GceEdgeInputDTO> edges) {
        if (edges == null) {
            return List.of();
        }

        return edges.stream()
                .peek(edge -> {
                    if (edge == null) {
                        throw new IllegalArgumentException("Payload de edges nao pode conter itens nulos.");
                    }
                })
                .map(edge -> new GceEdgeInput(edge.sourceNodeCode(), edge.targetNodeCode(), edge.type()))
                .toList();
    }

    private static List<GceRestrictionInput> toRestrictionInputs(List<GceInputDTO.GceRestrictionInputDTO> restrictions) {
        if (restrictions == null) {
            return List.of();
        }

        return restrictions.stream()
                .peek(restriction -> {
                    if (restriction == null) {
                        throw new IllegalArgumentException("Payload de restrictions nao pode conter itens nulos.");
                    }
                })
                .map(restriction -> new GceRestrictionInput(restriction.type(), restriction.nodeCodes()))
                .toList();
    }

    private static Map<String, UUID> createNodeIdsByCode(List<GceInputDTO.GceNodeInputDTO> nodes) {
        Map<String, UUID> nodeIdsByCode = new LinkedHashMap<>();
        if (nodes == null) {
            return nodeIdsByCode;
        }

        for (GceInputDTO.GceNodeInputDTO node : nodes) {
            if (node == null) {
                throw new IllegalArgumentException("Payload de nodes nao pode conter itens nulos.");
            }
            nodeIdsByCode.put(node.code(), UUID.randomUUID());
        }

        return nodeIdsByCode;
    }

    private static UUID resolveNodeId(Map<String, UUID> nodeIdsByCode, String nodeCode) {
        UUID nodeId = nodeIdsByCode.get(nodeCode);
        if (nodeId == null) {
            throw new IllegalArgumentException("Referencia para no inexistente no payload: " + nodeCode);
        }
        return nodeId;
    }

    private static List<ValidationGceDTO.ValidationGceMessageDTO> toMessageDtos(List<ValidationGceMessage> messages) {
        return messages.stream()
                .map(message -> new ValidationGceDTO.ValidationGceMessageDTO(message.code(), message.message()))
                .toList();
    }
}
