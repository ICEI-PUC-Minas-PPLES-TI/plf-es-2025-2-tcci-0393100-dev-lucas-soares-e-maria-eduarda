package br.pucminas.graphtest.infrastructure.paths;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ApiRequestPaths {

    public static final String USUARIO = "/usuario";

    public static final String PROJETO = "/projeto";

    public static final String GCE = "/grafo-de-causa-efeito";

    public static final String ID = "/{id}";

    public static final String USUARIO_SENHA = "/{id}/senha";

    public static final String USUARIO_VERIFICAR_TOKEN = "/verificar-token";

    public static final String PROJETO_MEUS = "/meus";

    public static final String GCE_VALIDAR = "/{id}/validar";

    public static final String GCE_PROJETO = "/projeto/{projectId}";

    public static final String GCE_NODES = "/{id}/nos";

    public static final String GCE_NODE = "/{id}/nos/{nodeCode}";

    public static final String GCE_EDGE_TOGGLE = "/{id}/aresta/{edgeId}/inverter-aresta";

}
