package br.pucminas.graphtest.application.port.input.gfc.records;

import java.util.UUID;

/**
 * Entrada para geracao de pre-visualizacao de um GFC.
 *
 * @param projectId identificador do projeto associado
 * @param name nome do grafo
 * @param description descricao opcional
 * @param sourceCode codigo-fonte Java
 * @param methodSignature assinatura do metodo selecionado; quando vazia, usa o primeiro metodo disponivel
 */
public record PreviewGfcInput(
        UUID projectId,
        String name,
        String description,
        String sourceCode,
        String methodSignature
) {
}
