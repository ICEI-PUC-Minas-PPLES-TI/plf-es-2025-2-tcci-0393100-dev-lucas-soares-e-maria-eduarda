package br.pucminas.graphtest.infrastructure.paths;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ApiRequestPaths {

    public static final String USUARIO = "/usuario";

    public static final String PROJETO = "/projeto";

    public static final String GCE = "/grafo-de-causa-efeito";

    public static final String GFC = "/grafo-de-fluxo-de-controle";

    public static final String GFC_SOURCE_FILE = "/source-file";

    public static final String GFC_SOURCE_FILE_ID = "/source-file/{sourceFileId}";

    public static final String GFC_SOURCE_FILE_PROJECT = "/source-file/projeto/{projectId}";

    public static final String GFC_SOURCE_FILE_SOURCE_CODE = "/source-file/{sourceFileId}/source-code";

    public static final String GFC_SOURCE_FILE_METHODS = "/source-file/{sourceFileId}/methods";

    public static final String GFC_SOURCE_FILE_METHOD_DETAILS = "/source-file/{sourceFileId}/method";

    public static final String GFC_PREVIEW = "/preview";

    public static final String GFC_CYCLOMATIC_COMPLEXITY = "/{gfcId}/complexidade-ciclomatica";

    public static final String GFC_STRUCTURAL_TEST_SIGNATURE = "/{gfcId}/assinatura-teste-estrutural";

    public static final String GFC_PROJECT = "/projeto/{projectId}";

    public static final String DECISION_TABLE = "/tabela-de-decisao";

    public static final String DECISION_TABLE_BY_GCE = "/gce/{gceId}";

    public static final String DECISION_TABLE_GENERATE = "/a-partir-do-gce/{gceId}";

    public static final String DECISION_TABLE_PREVIEW = "/pre-visualizar/a-partir-do-gce/{gceId}";

    public static final String DECISION_TABLE_REFRESH = "/sincronizar/a-partir-do-gce/{gceId}";

    public static final String ID = "/{id}";

    public static final String DECISION_TABLE_STATUS = "/{id}/status";

    public static final String DECISION_TABLE_FUNCTIONAL_TEST_SIGNATURE = "/{decisionTableId}/assinatura-teste-funcional";

    public static final String USUARIO_SENHA = "/{id}/senha";

    public static final String USUARIO_VERIFICAR_TOKEN = "/verificar-token";

    public static final String PROJETO_MEUS = "/meus";

    public static final String PROJETO_ARTEFATOS = "/{projectId}/artefatos";

    public static final String GCE_VALIDAR = "/validar";

    public static final String GCE_PROJETO = "/projeto/{projectId}";

    public static final String GCE_NODES = "/{id}/nos";

    public static final String GCE_NODE = "/{id}/nos/{nodeCode}";

    public static final String GCE_EDGE_TOGGLE = "/{id}/aresta/{edgeId}/inverter-aresta";

    public static final String DECISION_TABLE_PROJECT = "/projeto/{projectId}";

}
