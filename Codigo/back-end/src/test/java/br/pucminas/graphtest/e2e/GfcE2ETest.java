package br.pucminas.graphtest.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static br.pucminas.graphtest.adapters.inbound.util.SecurityHttpConstantsUtil.HEADER_AUTORIZACAO;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integracao do Grafo de Fluxo de Controle (TI4 e TI5 da Secao 6.2).
 *
 * <p>Exercitam o fluxo completo: autenticacao, criacao de projeto, importacao de
 * arquivo Java, geracao do GFC a partir de um metodo selecionado, calculo da
 * complexidade ciclomatica e geracao da assinatura de teste estrutural,
 * atravessando o banco relacional (arquivo-fonte) e o Neo4j (grafo).</p>
 */
@DisplayName("Testes de Integracao - GFC (TI4, TI5)")
class GfcE2ETest extends AbstractE2ETest {

    private static final String SENHA_VALIDA = "senha12345";

    private static final String CODIGO_JAVA = """
            public class Calculadora {
                public String classificar(int numero) {
                    if (numero > 0) {
                        return "positivo";
                    } else if (numero < 0) {
                        return "negativo";
                    }
                    return "zero";
                }
            }
            """;

    @Test
    @DisplayName("TI4 - Gerar GFC a partir de um metodo selecionado e calcular complexidade ciclomatica")
    void deveGerarGfcESuaComplexidadeCiclomatica() throws Exception {
        String token = registerAndLogin(SENHA_VALIDA);
        UUID projectId = createProject(token);
        UUID sourceFileId = uploadJavaFile(token, projectId);
        String methodSignature = firstMethodSignature(token, projectId, sourceFileId);

        UUID gfcId = createGfc(token, projectId, sourceFileId, methodSignature);

        mockMvc.perform(get("/projeto/{projectId}/gfc/{gfcId}/complexidade-ciclomatica", projectId, gfcId)
                        .header(HEADER_AUTORIZACAO, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gfcId").value(gfcId.toString()))
                .andExpect(jsonPath("$.cyclomaticComplexityByEdgesAndNodes", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.cyclomaticComplexityByPredicateNodes", greaterThanOrEqualTo(1)));
    }

    @Test
    @DisplayName("TI5 - Gerar esqueleto de teste estrutural a partir do GFC")
    void deveGerarEsqueletoDeTesteEstruturalAPartirDoGfc() throws Exception {
        String token = registerAndLogin(SENHA_VALIDA);
        UUID projectId = createProject(token);
        UUID sourceFileId = uploadJavaFile(token, projectId);
        String methodSignature = firstMethodSignature(token, projectId, sourceFileId);
        UUID gfcId = createGfc(token, projectId, sourceFileId, methodSignature);

        mockMvc.perform(get("/projeto/{projectId}/gfc/{gfcId}/assinatura-teste-estrutural", projectId, gfcId)
                        .header(HEADER_AUTORIZACAO, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gfcId").value(gfcId.toString()))
                .andExpect(jsonPath("$.testMethods", notNullValue()))
                .andExpect(jsonPath("$.generatedCode", notNullValue()));
    }

    private UUID uploadJavaFile(String token, UUID projectId) throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "Calculadora.java",
                MediaType.TEXT_PLAIN_VALUE,
                CODIGO_JAVA.getBytes(StandardCharsets.UTF_8)
        );

        MvcResult result = mockMvc.perform(multipart("/projeto/{projectId}/arquivos-java", projectId)
                        .file(file)
                        .header(HEADER_AUTORIZACAO, token))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return UUID.fromString(json.get("id_arquivo").asText());
    }

    private String firstMethodSignature(String token, UUID projectId, UUID sourceFileId) throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/projeto/{projectId}/arquivos-java/{fileId}/methods", projectId, sourceFileId)
                                .header(HEADER_AUTORIZACAO, token))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode methods = objectMapper.readTree(result.getResponse().getContentAsString());
        if (!methods.isArray() || methods.isEmpty()) {
            throw new IllegalStateException("Nenhum metodo encontrado no arquivo Java importado.");
        }
        return methods.get(0).get("signature").asText();
    }

    private UUID createGfc(String token, UUID projectId, UUID sourceFileId, String methodSignature) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("projectId", projectId.toString());
        body.put("sourceFileId", sourceFileId.toString());
        body.put("methodSignature", methodSignature);
        body.put("name", "GFC Calculadora");
        body.put("description", "GFC gerado em teste de integracao");

        MvcResult result = mockMvc.perform(post("/projeto/{projectId}/gfc", projectId)
                        .header(HEADER_AUTORIZACAO, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return UUID.fromString(json.get("id_gfc").asText());
    }
}
