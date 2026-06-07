package br.pucminas.graphtest.application.port.input.gce.records;

import java.util.UUID;

public record DeleteGceInput(UUID projectId, UUID id) {}
