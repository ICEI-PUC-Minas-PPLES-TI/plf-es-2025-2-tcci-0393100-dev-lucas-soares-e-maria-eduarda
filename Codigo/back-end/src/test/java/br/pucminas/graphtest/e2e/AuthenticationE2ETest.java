package br.pucminas.graphtest.e2e;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static br.pucminas.graphtest.adapters.inbound.util.SecurityHttpConstantsUtil.HEADER_AUTORIZACAO;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integracao de autenticacao (TI1, TI2 e TI3 da Secao 6.2).
 *
 * <p>Exercitam o cadastro de usuario e o login da plataforma de ponta a ponta,
 * passando pela cadeia real de filtros de seguranca e pelo banco relacional.</p>
 */
@DisplayName("Testes de Integracao - Autenticacao (TI1, TI2, TI3)")
class AuthenticationE2ETest extends AbstractE2ETest {

    private static final String SENHA_VALIDA = "senha12345";

    @Test
    @DisplayName("TI1 - Cadastrar usuario na plataforma com dados validos")
    void deveCadastrarUsuarioComDadosValidos() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "name", "Maria Eduarda Amaral",
                "email", uniqueEmail(),
                "password", SENHA_VALIDA
        ));

        mockMvc.perform(post("/usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensagem", containsString("usuario criado com sucesso")))
                .andExpect(jsonPath("$.id_usuario", notNullValue()));
    }

    @Test
    @DisplayName("TI2 - Autenticar usuario com credenciais validas")
    void deveAutenticarUsuarioComCredenciaisValidas() throws Exception {
        String email = registerUser(SENHA_VALIDA);

        String body = objectMapper.writeValueAsString(Map.of(
                "email", email,
                "password", SENHA_VALIDA
        ));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(header().string(HEADER_AUTORIZACAO, containsString("Bearer ")))
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    @Test
    @DisplayName("TI3 - Impedir autenticacao com credenciais invalidas")
    void deveImpedirAutenticacaoComCredenciaisInvalidas() throws Exception {
        String email = registerUser(SENHA_VALIDA);

        String body = objectMapper.writeValueAsString(Map.of(
                "email", email,
                "password", "senha-incorreta"
        ));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }
}
