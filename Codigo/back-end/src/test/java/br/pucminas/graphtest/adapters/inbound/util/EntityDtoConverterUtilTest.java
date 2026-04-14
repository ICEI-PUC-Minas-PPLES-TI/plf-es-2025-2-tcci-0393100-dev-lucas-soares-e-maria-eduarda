package br.pucminas.graphtest.adapters.inbound.util;

import br.pucminas.graphtest.adapters.inbound.dto.project.ProjectDTO;
import br.pucminas.graphtest.adapters.inbound.dto.user.UserDTO;
import br.pucminas.graphtest.application.port.input.project.records.ProjectOutput;
import br.pucminas.graphtest.application.port.input.user.records.UserOutput;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EntityDtoConverterUtilTest {

    @Test
    void shouldConvertUserOutputWithAuditFields() {
        LocalDateTime createdAt = LocalDateTime.now().minusDays(2);
        LocalDateTime updatedAt = LocalDateTime.now().minusHours(3);
        UserOutput output = new UserOutput(
                UUID.randomUUID(),
                "Usuario de teste",
                "teste@exemplo.com",
                2,
                createdAt,
                updatedAt
        );

        UserDTO dto = EntityDtoConverterUtil.toDto(output);

        assertEquals(output.id(), dto.id());
        assertEquals(output.profileCode(), dto.profileUser());
        assertEquals(output.name(), dto.name());
        assertEquals(output.email(), dto.email());
        assertEquals(createdAt, dto.createdAt());
        assertEquals(updatedAt, dto.updatedAt());
        assertNull(dto.password());
    }

    @Test
    void shouldConvertProjectOutputWithAuditFields() {
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now().minusMinutes(30);
        ProjectOutput output = new ProjectOutput(
                UUID.randomUUID(),
                "Projeto teste",
                "Descricao teste",
                UUID.randomUUID(),
                createdAt,
                updatedAt
        );

        ProjectDTO dto = EntityDtoConverterUtil.toDto(output);

        assertEquals(output.id(), dto.id());
        assertEquals(output.name(), dto.name());
        assertEquals(output.description(), dto.description());
        assertEquals(createdAt, dto.createdAt());
        assertEquals(updatedAt, dto.updatedAt());
    }
}
