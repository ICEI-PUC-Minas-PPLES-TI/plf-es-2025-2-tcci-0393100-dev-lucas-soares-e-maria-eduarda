package br.pucminas.graphtest.infrastructure.paths;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ApiRequestPaths {

    public static final String USUARIO = "/usuario";

    public static final String PROJETO = "/projeto";

    public static final String GCE = "/grafo-de-causa-efeito";

    public static final String GFC = "/grafo-de-fluxo-de-controle";

    public static final String GFC_SOURCE = "/source";

    public static final String GFC_SOURCE_METHODS = "/source/methods";

    public static final String GFC_PREVIEW = "/preview";

    public static final String DECISION_TABLE = "/tabela-de-decisao";

    public static final String DECISION_TABLE_BY_GCE = "/gce/{gceId}";

    public static final String DECISION_TABLE_GENERATE = "/a-partir-do-gce/{gceId}";

    public static final String DECISION_TABLE_PREVIEW = "/pre-visualizar/a-partir-do-gce/{gceId}";

    public static final String DECISION_TABLE_REFRESH = "/sincronizar/a-partir-do-gce/{gceId}";

    public static final String ID = "/{id}";

    public static final String DECISION_TABLE_STATUS = "/{id}/status";

    public static final String USUARIO_SENHA = "/{id}/senha";

    public static final String USUARIO_VERIFICAR_TOKEN = "/verificar-token";

    public static final String PROJETO_MEUS = "/meus";

    public static final String GCE_VALIDAR = "/validar";

    public static final String GCE_PROJETO = "/projeto/{projectId}";

    public static final String GCE_NODES = "/{id}/nos";

    public static final String GCE_NODE = "/{id}/nos/{nodeCode}";

    public static final String GCE_EDGE_TOGGLE = "/{id}/aresta/{edgeId}/inverter-aresta";

    public static final String DECISION_TABLE_PROJECT = "/projeto/{projectId}";

}
