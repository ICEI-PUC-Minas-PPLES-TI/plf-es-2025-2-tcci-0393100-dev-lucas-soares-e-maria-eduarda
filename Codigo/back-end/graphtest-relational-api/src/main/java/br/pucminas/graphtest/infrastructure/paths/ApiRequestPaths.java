package br.pucminas.graphtest.infrastructure.paths;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ApiRequestPaths {

    public static final String USUARIO = "/usuario";

    public static final String PROJETO = "/projeto";

    public static final String ID = "/{id}";

    public static final String USUARIO_SENHA = "/{id}/senha";

    public static final String USUARIO_VERIFICAR_TOKEN = "/verificar-token";

    public static final String PROJETO_MEUS = "/meus";

}
