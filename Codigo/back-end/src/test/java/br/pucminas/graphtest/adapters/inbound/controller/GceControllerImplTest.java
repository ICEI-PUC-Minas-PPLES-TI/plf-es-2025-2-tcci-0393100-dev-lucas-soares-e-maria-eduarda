package br.pucminas.graphtest.adapters.inbound.controller;

import br.pucminas.graphtest.adapters.inbound.dto.gce.AddGceNodeDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gce.GceDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gce.GceInputDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gce.UpdateGceDetailsDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gce.UpdateGceNodeDTO;
import br.pucminas.graphtest.adapters.inbound.dto.gce.ValidationGceDTO;
import br.pucminas.graphtest.application.domain.gce.enums.GceNodeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceOperatorTypeEnum;
import br.pucminas.graphtest.application.port.input.gce.AddNodeToGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.CreateGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.DeleteGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.FindGceByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.ListGcesByProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.PatchGceDetailsUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.ToggleGceEdgeUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.UpdateGceNodeUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.UpdateGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.ValidateGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.records.AddNodeToGceInput;
import br.pucminas.graphtest.application.port.input.gce.records.CreateGceInput;
import br.pucminas.graphtest.application.port.input.gce.records.DeleteGceInput;
import br.pucminas.graphtest.application.port.input.gce.records.FindGceByIdInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.input.gce.records.ListGcesByProjectInput;
import br.pucminas.graphtest.application.port.input.gce.records.ToggleGceEdgeInput;
import br.pucminas.graphtest.application.port.input.gce.records.UpdateGceDetailsInput;
import br.pucminas.graphtest.application.port.input.gce.records.UpdateGceInput;
import br.pucminas.graphtest.application.port.input.gce.records.UpdateGceNodeInput;
import br.pucminas.graphtest.application.port.input.gce.records.ValidateGceInput;
import br.pucminas.graphtest.application.port.input.gce.records.ValidationGceOutput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class GceControllerImplTest {

    @Mock
    private CreateGceUseCasePort createGceUseCasePort;
    @Mock
    private DeleteGceUseCasePort deleteGceUseCasePort;
    @Mock
    private FindGceByIdUseCasePort findGceByIdUseCasePort;
    @Mock
    private ListGcesByProjectUseCasePort listGcesByProjectUseCasePort;
    @Mock
    private ValidateGceUseCasePort validateGceUseCasePort;
    @Mock
    private PatchGceDetailsUseCasePort patchGceDetailsUseCasePort;
    @Mock
    private UpdateGceUseCasePort updateGceUseCasePort;
    @Mock
    private AddNodeToGceUseCasePort addNodeToGceUseCasePort;
    @Mock
    private UpdateGceNodeUseCasePort updateGceNodeUseCasePort;
    @Mock
    private ToggleGceEdgeUseCasePort toggleGceEdgeUseCasePort;

    @InjectMocks
    private GceControllerImpl controller;

    private GceOutput gceOutput(UUID id, UUID projectId) {
        return new GceOutput(id, projectId, "GCE", "Descricao", false, null, null, List.of(), List.of(), List.of());
    }

    private GceInputDTO gceInputDto() {
        return GceInputDTO.builder().name("GCE").description("Descricao").selected(false).build();
    }

    @Test
    void shouldCreateGceAndReturnLocationHeader() {
        UUID projectId = UUID.randomUUID();
        UUID gceId = UUID.randomUUID();
        when(createGceUseCasePort.execute(new CreateGceInput(projectId, "GCE", "Descricao", false, List.of(), List.of(), List.of())))
                .thenReturn(gceOutput(gceId, projectId));

        ResponseEntity<Map<String, Object>> response = controller.create(projectId, gceInputDto());

        assertEquals(CREATED, response.getStatusCode());
        assertTrue(response.getHeaders().getLocation().toString().endsWith("/projeto/" + projectId + "/gce/" + gceId));
    }

    @Test
    void shouldFindGceById() {
        UUID projectId = UUID.randomUUID();
        UUID gceId = UUID.randomUUID();
        when(findGceByIdUseCasePort.execute(new FindGceByIdInput(projectId, gceId))).thenReturn(gceOutput(gceId, projectId));

        ResponseEntity<GceDTO> response = controller.findById(projectId, gceId);

        assertEquals(OK, response.getStatusCode());
        assertEquals(gceId, response.getBody().id());
    }

    @Test
    void shouldListGcesByProject() {
        UUID projectId = UUID.randomUUID();
        when(listGcesByProjectUseCasePort.execute(new ListGcesByProjectInput(projectId)))
                .thenReturn(List.of(gceOutput(UUID.randomUUID(), projectId)));

        ResponseEntity<List<GceDTO>> response = controller.listByProject(projectId);

        assertEquals(OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void shouldDeleteGce() {
        UUID projectId = UUID.randomUUID();
        UUID gceId = UUID.randomUUID();

        ResponseEntity<Map<String, Object>> response = controller.delete(projectId, gceId);

        assertEquals(OK, response.getStatusCode());
        verify(deleteGceUseCasePort).execute(new DeleteGceInput(projectId, gceId));
    }

    @Test
    void shouldUpdateGce() {
        UUID projectId = UUID.randomUUID();
        UUID gceId = UUID.randomUUID();
        when(updateGceUseCasePort.execute(new UpdateGceInput(gceId, projectId, "GCE", "Descricao", false, List.of(), List.of(), List.of())))
                .thenReturn(gceOutput(gceId, projectId));

        ResponseEntity<GceDTO> response = controller.update(projectId, gceId, gceInputDto());

        assertEquals(OK, response.getStatusCode());
        assertEquals(gceId, response.getBody().id());
    }

    @Test
    void shouldPatchGceDetails() {
        UUID projectId = UUID.randomUUID();
        UUID gceId = UUID.randomUUID();
        UpdateGceDetailsDTO detailsDTO = new UpdateGceDetailsDTO("Novo nome", "Nova descricao");
        when(patchGceDetailsUseCasePort.execute(new UpdateGceDetailsInput(projectId, gceId, "Novo nome", "Nova descricao")))
                .thenReturn(gceOutput(gceId, projectId));

        ResponseEntity<GceDTO> response = controller.patchDetails(projectId, gceId, detailsDTO);

        assertEquals(OK, response.getStatusCode());
    }

    @Test
    void shouldValidateGce() {
        UUID projectId = UUID.randomUUID();
        when(validateGceUseCasePort.execute(new ValidateGceInput(projectId, "GCE", "Descricao", false, List.of(), List.of(), List.of())))
                .thenReturn(new ValidationGceOutput(List.of(), List.of()));

        ResponseEntity<ValidationGceDTO> response = controller.validate(projectId, gceInputDto());

        assertEquals(OK, response.getStatusCode());
        assertTrue(response.getBody().valid());
    }

    @Test
    void shouldAddNodeToGce() {
        UUID projectId = UUID.randomUUID();
        UUID gceId = UUID.randomUUID();
        AddGceNodeDTO nodeDTO = new AddGceNodeDTO("C1", "Causa", GceNodeTypeEnum.CAUSE, null, List.of(), List.of());
        when(addNodeToGceUseCasePort.execute(new AddNodeToGceInput(projectId, gceId, "C1", "Causa", GceNodeTypeEnum.CAUSE, null, List.of(), List.of())))
                .thenReturn(gceOutput(gceId, projectId));

        ResponseEntity<GceDTO> response = controller.addNode(projectId, gceId, nodeDTO);

        assertEquals(OK, response.getStatusCode());
    }

    @Test
    void shouldUpdateGceNode() {
        UUID projectId = UUID.randomUUID();
        UUID gceId = UUID.randomUUID();
        UpdateGceNodeDTO nodeDTO = new UpdateGceNodeDTO("Novo rotulo", GceOperatorTypeEnum.AND);
        when(updateGceNodeUseCasePort.execute(new UpdateGceNodeInput(projectId, gceId, "C1", "Novo rotulo", GceOperatorTypeEnum.AND)))
                .thenReturn(gceOutput(gceId, projectId));

        ResponseEntity<GceDTO> response = controller.updateNode(projectId, gceId, "C1", nodeDTO);

        assertEquals(OK, response.getStatusCode());
    }

    @Test
    void shouldToggleGceEdge() {
        UUID projectId = UUID.randomUUID();
        UUID gceId = UUID.randomUUID();
        UUID edgeId = UUID.randomUUID();
        when(toggleGceEdgeUseCasePort.execute(new ToggleGceEdgeInput(projectId, gceId, edgeId)))
                .thenReturn(gceOutput(gceId, projectId));

        ResponseEntity<GceDTO> response = controller.toggleEdge(projectId, gceId, edgeId);

        assertEquals(OK, response.getStatusCode());
    }
}
