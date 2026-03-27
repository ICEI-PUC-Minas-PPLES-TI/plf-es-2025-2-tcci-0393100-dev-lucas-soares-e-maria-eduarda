package br.pucminas.graphtest.adapters.inbound.util;

import lombok.experimental.UtilityClass;

/**
 * Contem as constantes relacionadas a HTTP e seguranca no adapter inbound.
 */
@UtilityClass
public class SecurityHttpConstantsUtil {

    public static final String HEADER_AUTORIZACAO = "Authorization";

    public static final String VALOR_HEADER_AUTORIZACAO = "Bearer %s";

    public static final String TIPO_TOKEN = "Bearer";

    public static final String CONTENT_TYPE = "application/json";

    public static final String CHARACTER_ENCODING = "UTF-8";

    public static final String CORPO_RESPOSTA_REQUISICAO = "{"
            + "\n\"token\": \"%s\""
            + "\n}";
}
