package br.pucminas.graphtest.infrastructure.config;

import jakarta.persistence.EntityManagerFactory;
import org.neo4j.driver.Driver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.core.DatabaseSelectionProvider;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.data.neo4j.core.mapping.Neo4jMappingContext;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Configuracao explicita do SDN para garantir suporte transacional ao Neo4j.
 */
@Configuration
public class Neo4jConfig {

    /**
     * Registra o transaction manager padrao do JPA com o nome esperado pelo Spring.
     *
     * @param entityManagerFactory fabrica de entity managers do JPA
     * @return transaction manager padrao da aplicacao relacional
     */
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    /**
     * Registra o transaction manager especifico do Neo4j.
     *
     * @param driver driver Neo4j configurado pelo Spring Boot
     * @param databaseSelectionProvider seletor de banco utilizado pelo SDN
     * @return transaction manager do Neo4j
     */
    @Bean
    public PlatformTransactionManager neo4jTransactionManager(
            Driver driver,
            DatabaseSelectionProvider databaseSelectionProvider
    ) {
        return new Neo4jTransactionManager(driver, databaseSelectionProvider);
    }

    /**
     * Registra explicitamente o template Neo4j com transaction manager,
     * evitando que o SDN crie um template sem suporte transacional.
     *
     * @param neo4jClient cliente Neo4j do Spring Data
     * @param neo4jMappingContext contexto de mapeamento do SDN
     * @param neo4jTransactionManager transaction manager do Neo4j
     * @return template Neo4j configurado
     */
    @Bean
    public Neo4jTemplate neo4jTemplate(
            Neo4jClient neo4jClient,
            Neo4jMappingContext neo4jMappingContext,
            @Qualifier("neo4jTransactionManager") PlatformTransactionManager neo4jTransactionManager
    ) {
        return new Neo4jTemplate(neo4jClient, neo4jMappingContext, neo4jTransactionManager);
    }
}
