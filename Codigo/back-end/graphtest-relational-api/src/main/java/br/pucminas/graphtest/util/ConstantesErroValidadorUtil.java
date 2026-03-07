package br.pucminas.graphtest.util;

import lombok.experimental.UtilityClass;

/**
 * Contém as constantes relacionadas aos erros de validação de uma requisição
 */
@UtilityClass
public class ConstantesErroValidadorUtil {

    public static final String MSG_ERRO_USUARIO_SENHA = "nome de usuário ou senha inválidos";

    public static final String MSG_ERRO_VALIDACAO = "erro de validação, verifique o log para detalhes";

    public static final String MSG_ERRO_CPF = "formato esperado: 000.000.000-00";

    public static final String MSG_ERRO_EMAIL = "formato esperado: usuario@email.com.br ou usuario@email.com";

    public static final String MSG_ERRO_SENHA = "a senha deve conter no mínimo 6 caracteres";

    public static final String MSG_ERRO_TELEFONE = "formato esperado: 00 00000-0000";

    public static final String MSG_ERRO_CEP = "formato esperado: 00000-000";
}
