package br.pucminas.graphtest.infrastructure.paths;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ApiRequestPaths {

    public static final String USUARIO = "/usuario";

    public static final String PROJETO = "/projeto";

    public static final String PROJECT_BASE = "/projeto/{projectId}";

    public static final String PROJECT_GFC = PROJECT_BASE + "/gfc";

    public static final String PROJECT_GCE = PROJECT_BASE + "/gce";

    public static final String PROJECT_DECISION_TABLE = PROJECT_BASE + "/tabela-de-decisao";

    public static final String PROJECT_JAVA_FILE = PROJECT_BASE + "/arquivos-java";

    public static final String GFC_PREVIEW = "/preview";

    public static final String GFC_CYCLOMATIC_COMPLEXITY = "/{gfcId}/complexidade-ciclomatica";

    public static final String GFC_STRUCTURAL_TEST_SIGNATURE = "/{gfcId}/assinatura-teste-estrutural";

    public static final String DECISION_TABLE_BY_GCE = "/gce/{gceId}";

    public static final String DECISION_TABLE_GENERATE = "/a-partir-do-gce/{gceId}";

    public static final String DECISION_TABLE_PREVIEW = "/pre-visualizar/a-partir-do-gce/{gceId}";

    public static final String DECISION_TABLE_REFRESH = "/sincronizar/a-partir-do-gce/{gceId}";

    public static final String ID = "/{id}";

    public static final String DECISION_TABLE_STATUS = "/{decisionTableId}/status";

    public static final String DECISION_TABLE_FUNCTIONAL_TEST_SIGNATURE = "/{decisionTableId}/assinatura-teste-funcional";

    public static final String USUARIO_SENHA = "/{id}/senha";

    public static final String USUARIO_VERIFICAR_TOKEN = "/verificar-token";

    public static final String PROJETO_MEUS = "/meus";

    public static final String PROJETO_ARTEFATOS = "/{projectId}/artefatos";

    public static final String GCE_VALIDAR = "/validar";

    public static final String GCE_NODES = "/{gceId}/nos";

    public static final String GCE_NODE = "/{gceId}/nos/{nodeCode}";

    public static final String GCE_EDGE_TOGGLE = "/{gceId}/aresta/{edgeId}/inverter-aresta";

    public static final String GFC_ID = "/{gfcId}";

    public static final String GCE_ID = "/{gceId}";

    public static final String DECISION_TABLE_ID = "/{decisionTableId}";

    public static final String JAVA_FILE_ID = "/{fileId}";

}
