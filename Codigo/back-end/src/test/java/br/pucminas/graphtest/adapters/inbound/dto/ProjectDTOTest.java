package br.pucminas.graphtest.adapters.inbound.dto;

import br.pucminas.graphtest.adapters.inbound.dto.project.ProjectDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectDTOTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldRejectBlankNameOnUpdate() {
        ProjectDTO dto = new ProjectDTO(UUID.randomUUID(), "   ", "Descricao valida");

        Set<ConstraintViolation<ProjectDTO>> violations = validator.validate(dto, ProjectDTO.Update.class);

        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(violation -> "name".equals(violation.getPropertyPath().toString())));
    }

    @Test
    void shouldRejectBlankDescriptionOnUpdate() {
        ProjectDTO dto = new ProjectDTO(UUID.randomUUID(), "Nome valido", "   ");

        Set<ConstraintViolation<ProjectDTO>> violations = validator.validate(dto, ProjectDTO.Update.class);

        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(violation -> "description".equals(violation.getPropertyPath().toString())));
    }
}
