package br.pucminas.graphtest.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static br.pucminas.graphtest.adapters.inbound.util.SecurityHttpConstantsUtil.HEADER_AUTORIZACAO;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integracao do GCE, da Tabela de Decisao e do teste funcional
 * (TI6, TI7 e TI8 da Secao 6.2).
 *
 * <p>Exercitam a modelagem e validacao de um Grafo de Causa e Efeito, a geracao
 * da Tabela de Decisao derivada do GCE e a geracao da assinatura de teste
 * funcional, persistindo o grafo e a tabela no Neo4j.</p>
 */
@DisplayName("Testes de Integracao - GCE, TD e Teste Funcional (TI6, TI7, TI8)")
class GceE2ETest extends AbstractE2ETest {

    private static final String SENHA_VALIDA = "senha12345";

    @Test
    @DisplayName("TI6 - Modelar GCE e validar consistencia")
    void deveModelarGceComConsistenciaValidada() throws Exception {
        String token = registerAndLogin(SENHA_VALIDA);
        UUID projectId = createProject(token);

        // Valida a consistencia do modelo antes de persistir (consistencia validada).
        mockMvc.perform(post("/projeto/{projectId}/gce/validar", projectId)
                        .header(HEADER_AUTORIZACAO, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gcePayload(projectId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.errors").isEmpty());

        // Persiste o GCE modelado.
        mockMvc.perform(post("/projeto/{projectId}/gce", projectId)
                        .header(HEADER_AUTORIZACAO, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gcePayload(projectId))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id_gce", notNullValue()));
    }

    @Test
    @DisplayName("TI7 - Gerar Tabela de Decisao a partir do GCE")
    void deveGerarTabelaDeDecisaoAPartirDoGce() throws Exception {
        String token = registerAndLogin(SENHA_VALIDA);
        UUID projectId = createProject(token);
        UUID gceId = createGce(token, projectId);

        mockMvc.perform(post("/projeto/{projectId}/tabela-de-decisao/a-partir-do-gce/{gceId}", projectId, gceId)
                        .header(HEADER_AUTORIZACAO, token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.gceId").value(gceId.toString()))
                .andExpect(jsonPath("$.rules", notNullValue()));
    }

    @Test
    @DisplayName("TI8 - Gerar esqueleto de teste funcional a partir do GCE validado")
    void deveGerarEsqueletoDeTesteFuncional() throws Exception {
        String token = registerAndLogin(SENHA_VALIDA);
        UUID projectId = createProject(token);
        UUID gceId = createGce(token, projectId);
        UUID decisionTableId = generateDecisionTable(token, projectId, gceId);

        mockMvc.perform(get("/projeto/{projectId}/tabela-de-decisao/{decisionTableId}/assinatura-teste-funcional",
                        projectId, decisionTableId)
                        .header(HEADER_AUTORIZACAO, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.decisionTableId").value(decisionTableId.toString()))
                .andExpect(jsonPath("$.testMethods", notNullValue()))
                .andExpect(jsonPath("$.generatedCode", notNullValue()));
    }

    /**
     * Monta o payload de um GCE valido: duas causas (C1 e C2) que alimentam um
     * operador AND (O1), o qual produz o efeito E1.
     */
    private Map<String, Object> gcePayload(UUID projectId) {
        Map<String, Object> causa1 = new LinkedHashMap<>();
        causa1.put("code", "C1");
        causa1.put("label", "Numero positivo");
        causa1.put("type", "CAUSE");

        Map<String, Object> causa2 = new LinkedHashMap<>();
        causa2.put("code", "C2");
        causa2.put("label", "Numero par");
        causa2.put("type", "CAUSE");

        Map<String, Object> operador = new LinkedHashMap<>();
        operador.put("code", "O1");
        operador.put("type", "OPERATOR");
        operador.put("operatorType", "AND");
        operador.put("sourceNodeCodes", List.of("C1", "C2"));
        operador.put("targetNodeCodes", List.of("E1"));

        Map<String, Object> efeito = new LinkedHashMap<>();
        efeito.put("code", "E1");
        efeito.put("label", "Aceitar numero");
        efeito.put("type", "EFFECT");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("projectId", projectId.toString());
        payload.put("name", "GCE Validacao de Numero");
        payload.put("description", "GCE modelado em teste de integracao");
        payload.put("selected", false);
        payload.put("nodes", List.of(causa1, causa2, operador, efeito));
        payload.put("edges", List.of());
        payload.put("restrictions", List.of());
        return payload;
    }

    private UUID createGce(String token, UUID projectId) throws Exception {
        MvcResult result = mockMvc.perform(post("/projeto/{projectId}/gce", projectId)
                        .header(HEADER_AUTORIZACAO, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gcePayload(projectId))))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return UUID.fromString(json.get("id_gce").asText());
    }

    private UUID generateDecisionTable(String token, UUID projectId, UUID gceId) throws Exception {
        MvcResult result = mockMvc.perform(
                        post("/projeto/{projectId}/tabela-de-decisao/a-partir-do-gce/{gceId}", projectId, gceId)
                                .header(HEADER_AUTORIZACAO, token))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return UUID.fromString(json.get("id").asText());
    }
}
