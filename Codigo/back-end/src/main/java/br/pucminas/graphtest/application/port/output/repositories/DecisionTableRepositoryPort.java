package br.pucminas.graphtest.application.port.output.repositories;

import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Porta de saida responsavel por persistir e recuperar agregados de tabela de decisao.
 */
public interface DecisionTableRepositoryPort {

    /**
     * Persiste o agregado informado.
     *
     * @param decisionTable agregado a ser salvo
     * @return agregado persistido
     */
    DecisionTable save(DecisionTable decisionTable);

    /**
     * Busca uma tabela de decisao pelo identificador externo.
     *
     * @param id identificador da tabela
     * @return agregado encontrado, quando existir
     */
    Optional<DecisionTable> findById(UUID id);

    /**
     * Busca a tabela de decisao associada ao GCE informado.
     *
     * @param gceId identificador do GCE de origem
     * @return agregado encontrado, quando existir
     */
    Optional<DecisionTable> findByGceId(UUID gceId);

    /**
     * Lista todas as tabelas de decisao associadas ao projeto informado.
     *
     * @param projectId identificador do projeto
     * @return lista de tabelas do projeto
     */
    List<DecisionTable> findAllByProjectId(UUID projectId);

    /**
     * Remove todas as tabelas de decisao associadas ao projeto informado.
     *
     * @param projectId identificador do projeto
     */
    void deleteAllByProjectId(UUID projectId);

    /**
     * Remove a tabela de decisao pelo identificador externo.
     *
     * @param id identificador da tabela
     */
    void deleteById(UUID id);

    /**
     * Remove a tabela de decisao associada ao GCE informado.
     *
     * @param gceId identificador do GCE de origem
     */
    void deleteByGceId(UUID gceId);

    /**
     * Indica se ja existe tabela de decisao associada ao GCE informado.
     *
     * @param gceId identificador do GCE de origem
     * @return {@code true} quando existir tabela para o GCE
     */
    boolean existsByGceId(UUID gceId);
}
