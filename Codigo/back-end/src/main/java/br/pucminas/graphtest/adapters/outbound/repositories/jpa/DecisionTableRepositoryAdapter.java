package br.pucminas.graphtest.adapters.outbound.repositories.jpa;

import br.pucminas.graphtest.adapters.outbound.entities.jpa.decisiontable.JpaDecisionTableEntity;
import br.pucminas.graphtest.adapters.outbound.repositories.jpa.interfaces.JpaDecisionTableRepository;
import br.pucminas.graphtest.adapters.outbound.repositories.shared.BasePersistenceMapper;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class DecisionTableRepositoryAdapter implements DecisionTableRepositoryPort {

    private final JpaDecisionTableRepository jpaDecisionTableRepository;
    private final BasePersistenceMapper<DecisionTable, JpaDecisionTableEntity> mapper;

    @Override
    public DecisionTable save(DecisionTable decisionTable) {
        JpaDecisionTableEntity entity = mapper.toEntity(decisionTable);
        markEntityState(entity);
        return mapper.toDomain(jpaDecisionTableRepository.save(entity));
    }

    /**
     * A tabela de decisao nasce no dominio com ID proprio. Por isso o adapter precisa informar
     * ao Spring Data se a entidade representa uma insercao inicial ou uma atualizacao.
     */
    private void markEntityState(JpaDecisionTableEntity entity) {
        if (entity.getId() != null && jpaDecisionTableRepository.existsById(entity.getId())) {
            entity.markAsExisting();
            return;
        }
        entity.markAsNew();
    }

    @Override
    public Optional<DecisionTable> findById(UUID id) {
        return jpaDecisionTableRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<DecisionTable> findByGceId(UUID gceId) {
        return jpaDecisionTableRepository.findByGceId(gceId).map(mapper::toDomain);
    }

    @Override
    public List<DecisionTable> findAllByProjectId(UUID projectId) {
        return jpaDecisionTableRepository.findAllByProjectId(projectId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteAllByProjectId(UUID projectId) {
        jpaDecisionTableRepository.deleteAllByProjectId(projectId);
    }

    @Override
    public void deleteById(UUID id) {
        jpaDecisionTableRepository.deleteById(id);
    }

    @Override
    public void deleteByGceId(UUID gceId) {
        jpaDecisionTableRepository.deleteByGceId(gceId);
    }

    @Override
    public boolean existsByGceId(UUID gceId) {
        return jpaDecisionTableRepository.existsByGceId(gceId);
    }
}
