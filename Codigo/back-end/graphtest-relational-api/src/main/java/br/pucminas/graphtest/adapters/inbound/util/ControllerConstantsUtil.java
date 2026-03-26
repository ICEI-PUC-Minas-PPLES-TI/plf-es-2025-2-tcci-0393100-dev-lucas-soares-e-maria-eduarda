package br.pucminas.graphtest.adapters.inbound.util;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Contem as constantes relacionadas aos controllers HTTP.
 */
@UtilityClass
public class ControllerConstantsUtil {

    public static final String ENDPOINT_USUARIO = "/usuario";

    public static final String ENDPOINT_PROJETO = "/projeto";

    public static final List<String> CHAVES_USUARIO_CONTROLLER =
            new ArrayList<>(asList("status", "mensagem", "id_usuario"));

    public static final List<String> CHAVES_PROJETO_CONTROLLER =
            new ArrayList<>(asList("status", "mensagem", "id_projeto"));

    public static final String MSG_USUARIO_CRIADO = "usuario criado com sucesso";

    public static final String MSG_USUARIO_ATUALIZADO = "usuario atualizado com sucesso";

    public static final String MSG_USUARIO_DELETADO = "usuario deletado com sucesso";

    public static final String MSG_USUARIO_SENHA = "senha atualizada com sucesso";

    public static final String MSG_PROJETO_CRIADO = "projeto criado com sucesso";

    public static final String MSG_PROJETO_ATUALIZADO = "projeto atualizado com sucesso";

    public static final String MSG_PROJETO_DELETADO = "projeto deletado com sucesso";
}
