package br.pucminas.graphtest.application.port.input.project.records;

import java.util.UUID;

public record UpdateProjectInput(
   UUID id,
   String name,
   String description
) {}
