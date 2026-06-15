package br.pucminas.graphtest;

import br.pucminas.graphtest.e2e.AbstractE2ETest;
import org.junit.jupiter.api.Test;

/**
 * Verifica que o contexto completo da aplicacao sobe corretamente.
 *
 * <p>Reaproveita a infraestrutura Testcontainers de {@link AbstractE2ETest}
 * (PostgreSQL + Neo4j) para que o contexto carregue contra os bancos reais
 * exigidos pela aplicacao.</p>
 */
class GraphtestApplicationTests extends AbstractE2ETest {

    @Test
    void contextLoads() {
    }

}
