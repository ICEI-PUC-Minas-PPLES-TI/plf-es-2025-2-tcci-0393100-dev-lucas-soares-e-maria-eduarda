package br.pucminas.graphtest.application.port.output.repositories;

import br.pucminas.graphtest.application.domain.gfc.model.GfcSourceFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Porta de saida responsavel por persistir arquivos-fonte Java da feature GFC.
 */
public interface GfcSourceFileRepositoryPort {

    GfcSourceFile save(GfcSourceFile sourceFile);

    Optional<GfcSourceFile> findById(UUID id);

    List<GfcSourceFile> findAllByProjectId(UUID projectId);

    void deleteById(UUID id);

    void deleteAllByProjectId(UUID projectId);
}
