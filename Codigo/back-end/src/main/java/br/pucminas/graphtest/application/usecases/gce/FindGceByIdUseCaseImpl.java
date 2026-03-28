package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.domain.Gce;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.gce.FindGceByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.records.FindGceByIdInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;

/**
 * Caso de uso responsavel por localizar um GCE por id.
 */
public class FindGceByIdUseCaseImpl implements FindGceByIdUseCasePort {

    private final GceRepositoryPort gceRepository;

    /**
     * Cria o caso de uso com a dependencia necessaria para recuperar o GCE.
     *
     * @param gceRepository repositorio responsavel pela busca do agregado
     */
    public FindGceByIdUseCaseImpl(GceRepositoryPort gceRepository) {
        this.gceRepository = gceRepository;
    }

    /**
     * Busca o GCE identificado na entrada.
     *
     * @param input dados contendo o identificador do grafo
     * @return representacao do GCE encontrado
     */
    @Override
    public GceOutput execute(FindGceByIdInput input) {
        Gce graph = gceRepository.findById(input.id())
                .orElseThrow(() -> new EntityNotFoundException("GCE nao encontrado"));

        return GceOutput.from(graph);
    }
}
