package br.pucminas.graphtest.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;

import static br.pucminas.graphtest.adapters.inbound.util.SecurityHttpConstantsUtil.HEADER_AUTORIZACAO;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Classe base para os testes de integracao ponta a ponta (e2e) descritos na
 * Secao 6.2 do documento de projeto.
 *
 * <p>Cada teste sobe o contexto completo da aplicacao Spring e exercita os
 * endpoints REST reais, atravessando a cadeia de filtros de seguranca, os casos
 * de uso e os dois bancos de dados utilizados pelo sistema:</p>
 *
 * <ul>
 *     <li>PostgreSQL (banco relacional) para usuarios, projetos e arquivos-fonte;</li>
 *     <li>Neo4j (banco orientado a grafos) para GFC, GCE e tabelas de decisao.</li>
 * </ul>
 *
 * <p>Ambos os bancos sao provisionados via Testcontainers (Docker), garantindo
 * que a integracao seja validada contra instancias reais. Os containers seguem
 * o padrao singleton: sao iniciados uma unica vez e reaproveitados por todas as
 * classes de teste que estendem esta base, sendo finalizados automaticamente
 * pelo Ryuk ao fim da execucao.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractE2ETest {

    protected static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"));

    protected static final Neo4jContainer<?> NEO4J =
            new Neo4jContainer<>(DockerImageName.parse("neo4j:5.26"));

    static {
        POSTGRES.start();
        NEO4J.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.sql.init.mode", () -> "never");

        registry.add("spring.neo4j.uri", NEO4J::getBoltUrl);
        registry.add("spring.neo4j.authentication.username", () -> "neo4j");
        registry.add("spring.neo4j.authentication.password", NEO4J::getAdminPassword);
        registry.add("spring.data.neo4j.database", () -> "neo4j");

        registry.add("jwt.segredo", () -> "test-secret-key-test-secret-key-1234567890");
        registry.add("jwt.tempo_expiracao", () -> "86400000");
    }

    @Autowired
    protected MockMvc mockMvc;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Gera um e-mail unico valido conforme o padrao aceito pela aplicacao.
     */
    protected String uniqueEmail() {
        return "usuario" + System.nanoTime() + "@graphtest.com";
    }

    /**
     * Cadastra um usuario na plataforma (POST /usuario) e retorna seu e-mail.
     *
     * @param password senha em texto puro usada no cadastro
     * @return e-mail do usuario criado
     */
    protected String registerUser(String password) throws Exception {
        String email = uniqueEmail();
        String body = objectMapper.writeValueAsString(java.util.Map.of(
                "name", "Usuario de Teste",
                "email", email,
                "password", password
        ));

        mockMvc.perform(post("/usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        return email;
    }

    /**
     * Autentica um usuario (POST /login) e devolve o token JWT retornado.
     *
     * @param email e-mail do usuario
     * @param password senha em texto puro
     * @return token de acesso extraido do header Authorization
     */
    protected String login(String email, String password) throws Exception {
        String body = objectMapper.writeValueAsString(java.util.Map.of(
                "email", email,
                "password", password
        ));

        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        String authorizationHeader = result.getResponse().getHeader(HEADER_AUTORIZACAO);
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
            return "Bearer " + json.get("token").asText();
        }
        return authorizationHeader;
    }

    /**
     * Cadastra e autentica um usuario, retornando o valor pronto para o header
     * Authorization ({@code Bearer <token>}).
     *
     * @param password senha em texto puro
     * @return cabecalho de autorizacao com o token JWT
     */
    protected String registerAndLogin(String password) throws Exception {
        String email = registerUser(password);
        return login(email, password);
    }

    /**
     * Cria um projeto vinculado ao usuario autenticado (POST /projeto).
     *
     * @param authorization cabecalho Authorization com o token JWT
     * @return identificador do projeto criado
     */
    protected UUID createProject(String authorization) throws Exception {
        String body = objectMapper.writeValueAsString(java.util.Map.of(
                "name", "Projeto E2E",
                "description", "Projeto criado em teste de integracao"
        ));

        MvcResult result = mockMvc.perform(post("/projeto")
                        .header(HEADER_AUTORIZACAO, authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return UUID.fromString(json.get("id_projeto").asText());
    }
}
