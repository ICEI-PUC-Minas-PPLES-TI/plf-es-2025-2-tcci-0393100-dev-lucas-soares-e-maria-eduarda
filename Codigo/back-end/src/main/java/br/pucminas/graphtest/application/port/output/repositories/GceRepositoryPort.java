package br.pucminas.graphtest.application.port.output.repositories;

import br.pucminas.graphtest.application.domain.gce.model.Gce;

import java.util.List;
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
     * Lista todos os GCEs associados ao projeto informado.
     *
     * @param projectId identificador do projeto dono dos grafos
     * @return lista de grafos do projeto
     */
    List<Gce> findAllByProjectId(UUID projectId);

    /**
     * Remove um GCE pelo identificador, incluindo seu agregado persistido.
     *
     * @param id identificador do grafo
     */
    void deleteById(UUID id);

    /**
     * Remove todos os GCEs associados ao projeto informado.
     *
     * @param projectId identificador do projeto dono dos grafos
     */
    void deleteAllByProjectId(UUID projectId);
}
