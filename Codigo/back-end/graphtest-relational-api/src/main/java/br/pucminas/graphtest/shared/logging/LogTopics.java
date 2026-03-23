package br.pucminas.graphtest.shared.logging;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LogTopics {

    public static final String USUARIO_SPRING_SECURITY_SERVICE = "USUARIO_SPRING_SECURITY_SERVICE";

    public static final String USUARIO_CONTROLLER = "USUARIO_CONTROLLER";

    public static final String JWT = "JWT";

    public static final String SEGURANCA_CONFIG = "SEGURANCA_CONFIG";

    public static final String INTERCEPTADOR_EXCECOES = "INTERCEPTADOR_EXCECOES";

    public static final String JWT_AUTHENTICATION_FILTER = "JWT_FILTRO_AUTENTICACAO";

    public static final String JWT_AUTHORIZATION_FILTER = "JWT_FILTRO_AUTORIZACAO";

    public static final String CONVERSOR_ENTIDADE_DTO_UTIL = "CONVERSOR_ENTIDADE_DTO_UTIL";
}
