package br.pucminas.graphtest.application.port.input.gfc;

import br.pucminas.graphtest.application.port.input.gfc.records.GfcSummaryOutput;

import java.util.List;
import java.util.UUID;

/**
 * Porta de entrada do caso de uso responsavel por listar GFCs de um projeto.
 */
public interface ListGfcByProjectUseCasePort {

    List<GfcSummaryOutput> execute(UUID projectId);
}
