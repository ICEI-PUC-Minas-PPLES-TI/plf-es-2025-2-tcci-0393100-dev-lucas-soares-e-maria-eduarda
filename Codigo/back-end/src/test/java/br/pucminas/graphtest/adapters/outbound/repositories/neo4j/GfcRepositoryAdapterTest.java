package br.pucminas.graphtest.adapters.outbound.repositories.neo4j;

import br.pucminas.graphtest.adapters.outbound.entities.neo4j.gfc.Neo4jGfcEntity;
import br.pucminas.graphtest.adapters.outbound.entities.neo4j.gfc.Neo4jGfcNodeEntity;
import br.pucminas.graphtest.adapters.outbound.repositories.neo4j.interfaces.Neo4jGfcRepository;
import br.pucminas.graphtest.adapters.outbound.repositories.neo4j.mapper.GfcMapperBase;
import br.pucminas.graphtest.application.domain.gfc.enums.GfcEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gfc.enums.GfcNodeTypeEnum;
import br.pucminas.graphtest.application.domain.gfc.model.Gfc;
import br.pucminas.graphtest.application.domain.gfc.model.GfcEdge;
import br.pucminas.graphtest.application.domain.gfc.model.GfcNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

import static br.pucminas.graphtest.application.domain.gfc.rules.GfcDomainRules.JAVA_LANGUAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GfcRepositoryAdapterTest {

    @Mock
    private Neo4jGfcRepository neo4jGfcRepository;

    private final GfcMapperBase mapper = new GfcMapperBase();

    @Test
    void shouldFindByIdAndMapEntityToDomain() {
        GfcRepositoryAdapter adapter = new GfcRepositoryAdapter(neo4jGfcRepository, mapper);
        UUID gfcId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID sourceFileId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Neo4jGfcEntity entity = new Neo4jGfcEntity();
        entity.setId(gfcId);
        entity.setCreatedAt(createdAt);
        entity.setProjectId(projectId);
        entity.setSourceFileId(sourceFileId);
        entity.setMethodSignature("int soma(int a, int b)");
        entity.setName("GFC soma");
        entity.setDescription("Descricao");
        entity.setLanguage(JAVA_LANGUAGE);
        when(neo4jGfcRepository.findById(gfcId)).thenReturn(Optional.of(entity));

        Optional<Gfc> result = adapter.findById(gfcId);

        verify(neo4jGfcRepository).findById(gfcId);
        assertTrue(result.isPresent());
        assertEquals(gfcId, result.get().getId());
        assertEquals(projectId, result.get().getProjectId());
        assertEquals(sourceFileId, result.get().getSourceFileId());
        assertEquals("int soma(int a, int b)", result.get().getMethodSignature());
        assertEquals(createdAt, result.get().getCreatedAt());
    }

    @Test
    void shouldFindAllByProjectIdUsingCreatedAtDescendingRepositoryMethod() {
        GfcRepositoryAdapter adapter = new GfcRepositoryAdapter(neo4jGfcRepository, mapper);
        UUID projectId = UUID.randomUUID();
        UUID firstGfcId = UUID.randomUUID();
        UUID secondGfcId = UUID.randomUUID();
        LocalDateTime firstCreatedAt = LocalDateTime.now();
        LocalDateTime secondCreatedAt = firstCreatedAt.minusDays(1);
        Neo4jGfcEntity firstEntity = new Neo4jGfcEntity();
        firstEntity.setId(firstGfcId);
        firstEntity.setCreatedAt(firstCreatedAt);
        firstEntity.setProjectId(projectId);
        firstEntity.setSourceFileId(UUID.randomUUID());
        firstEntity.setMethodSignature("void recente()");
        firstEntity.setName("Recente");
        firstEntity.setLanguage(JAVA_LANGUAGE);
        Neo4jGfcEntity secondEntity = new Neo4jGfcEntity();
        secondEntity.setId(secondGfcId);
        secondEntity.setCreatedAt(secondCreatedAt);
        secondEntity.setProjectId(projectId);
        secondEntity.setSourceFileId(UUID.randomUUID());
        secondEntity.setMethodSignature("void antigo()");
        secondEntity.setName("Antigo");
        secondEntity.setLanguage(JAVA_LANGUAGE);
        when(neo4jGfcRepository.findAllByProjectIdOrderByCreatedAtDesc(projectId))
                .thenReturn(List.of(firstEntity, secondEntity));

        List<Gfc> result = adapter.findAllByProjectId(projectId);

        verify(neo4jGfcRepository).findAllByProjectIdOrderByCreatedAtDesc(projectId);
        assertEquals(firstGfcId, result.get(0).getId());
        assertEquals(firstCreatedAt, result.get(0).getCreatedAt());
        assertEquals(secondGfcId, result.get(1).getId());
        assertEquals(secondCreatedAt, result.get(1).getCreatedAt());
    }

    @Test
    void shouldDeleteGraphByIdUsingExplicitNeo4jQuery() {
        GfcRepositoryAdapter adapter = new GfcRepositoryAdapter(neo4jGfcRepository, mapper);
        UUID gfcId = UUID.randomUUID();
        doNothing().when(neo4jGfcRepository).deleteGraphById(gfcId);

        adapter.deleteById(gfcId);

        verify(neo4jGfcRepository).deleteGraphById(gfcId);
    }

    @Test
    void shouldDeleteAllGraphsBySourceFileIdUsingExplicitNeo4jQuery() {
        GfcRepositoryAdapter adapter = new GfcRepositoryAdapter(neo4jGfcRepository, mapper);
        UUID sourceFileId = UUID.randomUUID();
        doNothing().when(neo4jGfcRepository).deleteAllBySourceFileId(sourceFileId);

        adapter.deleteAllBySourceFileId(sourceFileId);

        verify(neo4jGfcRepository).deleteAllBySourceFileId(sourceFileId);
    }

    @Test
    void shouldDeleteAllGraphsByProjectIdUsingExplicitNeo4jQuery() {
        GfcRepositoryAdapter adapter = new GfcRepositoryAdapter(neo4jGfcRepository, mapper);
        UUID projectId = UUID.randomUUID();
        doNothing().when(neo4jGfcRepository).deleteAllByProjectId(projectId);

        adapter.deleteAllByProjectId(projectId);

        verify(neo4jGfcRepository).deleteAllByProjectId(projectId);
    }

    @Test
    void shouldMapAdvancedControlFlowEnumValuesToNeo4jEntity() {
        UUID graphId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID sourceFileId = UUID.randomUUID();
        GfcNode loopNode = new GfcNode(UUID.randomUUID(), "N1", "while (ativo)", GfcNodeTypeEnum.LOOP, 3, 3);
        GfcNode statementNode = GfcNode.statement(UUID.randomUUID(), "N2", "executar();", 4, 4);
        GfcEdge loopBodyEdge = new GfcEdge(UUID.randomUUID(), "N1", "N2", GfcEdgeTypeEnum.LOOP_BODY, "body");
        LocalDateTime createdAt = LocalDateTime.now();
        Gfc graph = Gfc.reconstitute(
                graphId,
                projectId,
                sourceFileId,
                "void executar()",
                "GFC executar",
                "Descricao",
                JAVA_LANGUAGE,
                List.of(loopNode, statementNode),
                List.of(loopBodyEdge),
                createdAt,
                null
        );

        Neo4jGfcEntity entity = mapper.toEntity(graph);

        assertEquals(createdAt, entity.getCreatedAt());
        Neo4jGfcNodeEntity mappedLoopNode = entity.getNodes().stream()
                .filter(node -> "N1".equals(node.getCode()))
                .findFirst()
                .orElseThrow();
        assertEquals(GfcNodeTypeEnum.LOOP, mappedLoopNode.getType());
        assertEquals(GfcEdgeTypeEnum.LOOP_BODY, mappedLoopNode.getOutgoingEdges().getFirst().getType());
    }
}
