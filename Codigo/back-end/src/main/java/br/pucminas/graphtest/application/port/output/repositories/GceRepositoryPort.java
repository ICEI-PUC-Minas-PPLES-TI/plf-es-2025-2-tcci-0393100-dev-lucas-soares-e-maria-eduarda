package br.pucminas.graphtest.application.port.output.repositories;

import br.pucminas.graphtest.application.domain.Gce;

import java.util.Optional;
import java.util.UUID;

/**
 * Porta de saida responsavel por persistir e recuperar agregados de GCE.
 */
public interface GceRepositoryPort {

    /**
     * Persiste o agregado informado.
     *
     * @param graph agregado a ser salvo
     * @return agregado persistido
     */
    Gce save(Gce graph);

    /**
     * Busca um GCE pelo identificador.
     *
     * @param id identificador do grafo
     * @return agregado encontrado, quando existir
     */
    Optional<Gce> findById(UUID id);

    /**
     * Remove todos os GCEs associados ao projeto informado.
     *
     * @param projectId identificador do projeto dono dos grafos
     */
    void deleteAllByProjectId(UUID projectId);
}
