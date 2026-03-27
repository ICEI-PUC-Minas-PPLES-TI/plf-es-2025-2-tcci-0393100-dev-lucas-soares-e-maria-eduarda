package br.pucminas.graphtest.adapters.outbound.repositories.mappers;

/**
 * Contrato generico para conversao entre um objeto de dominio e sua
 * representacao de persistencia.
 *
 * @param <D> tipo do objeto de dominio
 * @param <E> tipo da entidade de persistencia
 */
public interface PersistenceMapper<D, E> {

    /**
     * Converte um objeto de dominio para sua representacao de persistencia.
     *
     * @param domain objeto de dominio a ser convertido
     * @return entidade correspondente para persistencia
     */
    E toEntity(D domain);

    /**
     * Converte uma entidade de persistencia para seu objeto de dominio.
     *
     * @param entity entidade de persistencia a ser convertida
     * @return objeto correspondente no dominio
     */
    D toDomain(E entity);
}
