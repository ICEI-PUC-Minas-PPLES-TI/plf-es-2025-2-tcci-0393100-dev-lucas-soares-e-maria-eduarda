package br.pucminas.graphtest.application.service.interfaces;

import br.pucminas.graphtest.application.domain.Project;

import java.util.UUID;

public interface ProjectAccessService {
    Project findAuthorizedProject(UUID projectId);
}
