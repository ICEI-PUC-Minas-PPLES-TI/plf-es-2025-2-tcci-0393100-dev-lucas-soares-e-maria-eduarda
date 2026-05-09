package br.pucminas.graphtest.application.port.output.repositories;

import br.pucminas.graphtest.application.domain.gfc.model.Gfc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Porta de saida responsavel por persistir e recuperar agregados de GFC.
 */
public interface GfcRepositoryPort {

    Gfc save(Gfc graph);

    Optional<Gfc> findById(UUID id);

    List<Gfc> findAllByProjectId(UUID projectId);

    void deleteById(UUID id);

    void deleteAllByProjectId(UUID projectId);
}
