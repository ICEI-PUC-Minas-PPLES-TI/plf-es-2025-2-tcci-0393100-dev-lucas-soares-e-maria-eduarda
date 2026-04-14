package br.pucminas.graphtest.infrastructure.config;

import jakarta.persistence.EntityManagerFactory;
import org.neo4j.driver.Driver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.neo4j.core.DatabaseSelectionProvider;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.data.neo4j.core.mapping.Neo4jMappingContext;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Separa o escopo de varredura dos repositorios JPA e Neo4j.
 *
 * <p>Como a aplicacao usa dois modulos Spring Data, a configuracao explicita
 * evita ambiguidade no binding entre interface de repositorio e tecnologia de
 * persistencia.</p>
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "br.pucminas.graphtest.adapters.outbound.repositories.jpa.interfaces",
        transactionManagerRef = "transactionManager"
)
@EnableNeo4jRepositories(
        basePackages = "br.pucminas.graphtest.adapters.outbound.repositories.neo4j.interfaces",
        transactionManagerRef = "neo4jTransactionManager",
        neo4jTemplateRef = "neo4jTemplate"
)
public class PersistenceRepositoriesConfig {

    /**
     * Registra o transaction manager padrao do JPA.
     *
     * @param entityManagerFactory fabrica de entity managers do JPA
     * @return transaction manager primario da aplicacao relacional
     */
    @Bean(name = "transactionManager")
    @Primary
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
     * Registra explicitamente o template Neo4j com o transaction manager da tecnologia.
     *
     * <p>No contexto atual, apenas expor o transaction manager do Neo4j nao foi
     * suficiente para o SDN montar um template transacional valido.</p>
     *
     * @param neo4jClient cliente Neo4j do Spring Data
     * @param neo4jMappingContext contexto de mapeamento do SDN
     * @param neo4jTransactionManager transaction manager do Neo4j
     * @return template Neo4j configurado com suporte transacional
     */
    @Bean
    public Neo4jTemplate neo4jTemplate(
            Neo4jClient neo4jClient,
            Neo4jMappingContext neo4jMappingContext,
            @Qualifier("neo4jTransactionManager") PlatformTransactionManager neo4jTransactionManager
    ) {
        return new Neo4jTemplate(neo4jClient, neo4jMappingContext, neo4jTransactionManager);
    }

    @Bean
    public CommandLineRunner ensureNeo4jConstraints(Neo4jClient neo4jClient) {
        return args -> neo4jClient.query("""
                CREATE CONSTRAINT gce_node_graph_scoped_code_unique IF NOT EXISTS
                FOR (node:GceNode)
                REQUIRE node.graphScopedCode IS UNIQUE
                """).run();
    }
}
