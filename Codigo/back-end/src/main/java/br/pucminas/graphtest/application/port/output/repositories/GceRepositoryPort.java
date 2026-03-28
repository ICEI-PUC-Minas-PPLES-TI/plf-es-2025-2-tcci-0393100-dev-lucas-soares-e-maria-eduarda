package br.pucminas.graphtest.application.port.output.repositories;

import br.pucminas.graphtest.application.domain.Gce;

import java.util.Optional;

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
    Optional<Gce> findById(Long id);
}
