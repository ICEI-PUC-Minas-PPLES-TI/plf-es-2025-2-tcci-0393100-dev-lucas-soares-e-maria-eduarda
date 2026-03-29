package br.pucminas.graphtest.adapters.inbound.controller.interfaces;

import br.pucminas.graphtest.adapters.inbound.dto.GceDTO;
import br.pucminas.graphtest.adapters.inbound.dto.GceInputDTO;
import br.pucminas.graphtest.adapters.inbound.dto.AddGceNodeDTO;
import br.pucminas.graphtest.adapters.inbound.dto.UpdateGceNodeDTO;
import br.pucminas.graphtest.adapters.inbound.dto.ValidationGceDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GCE_EDGE_TOGGLE;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GCE_NODE;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GCE_NODES;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.GCE_VALIDAR;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.ID;

/**
 * Contrato HTTP do controller de GCE.
 */
@Tag(name = "GCE", description = "Operacoes de cadastro, consulta, validacao e mutacao do Grafo de Causa e Efeito")
public interface GceController {

    @PostMapping
    @Operation(
            summary = "Cadastrar GCE",
            description = "Cria um novo Grafo de Causa e Efeito para um projeto autorizado. "
                    + "O payload pode trazer arestas explicitas e tambem conexoes declaradas em nos operadores, "
                    + "que serao materializadas automaticamente como arestas IDENTITY."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "GCE criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Payload invalido ou modelo de GCE inconsistente"),
            @ApiResponse(responseCode = "403", description = "Usuario sem acesso ao projeto")
    })
    ResponseEntity<java.util.Map<String, Object>> create(@Validated @RequestBody GceInputDTO graph);

    @GetMapping(ID)
    @Operation(
            summary = "Buscar GCE por id",
            description = "Recupera um Grafo de Causa e Efeito ja persistido, desde que o usuario tenha acesso ao projeto ao qual ele pertence."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "GCE encontrado",
                    content = @Content(schema = @Schema(implementation = GceDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "GCE nao encontrado"),
            @ApiResponse(responseCode = "403", description = "Usuario sem acesso ao projeto")
    })
    ResponseEntity<GceDTO> findById(@Parameter(description = "Identificador do GCE") @PathVariable UUID id);

    @GetMapping(GCE_VALIDAR)
    @Operation(
            summary = "Validar GCE salvo",
            description = "Executa a validacao estrutural e semantica de um GCE ja persistido."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Resultado da validacao gerado com sucesso",
                    content = @Content(schema = @Schema(implementation = ValidationGceDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "GCE nao encontrado"),
            @ApiResponse(responseCode = "403", description = "Usuario sem acesso ao projeto")
    })
    ResponseEntity<ValidationGceDTO> validate(@Parameter(description = "Identificador do GCE") @PathVariable UUID id);

    @PutMapping(ID)
    @Operation(
            summary = "Atualizar GCE completo",
            description = "Substitui a representacao completa do GCE, incluindo nos, arestas, restricoes e metadados. "
                    + "Assim como na criacao, o payload pode trazer arestas explicitas e conexoes declaradas em nos operadores."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "GCE atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = GceDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Payload invalido ou modelo de GCE inconsistente"),
            @ApiResponse(responseCode = "404", description = "GCE nao encontrado"),
            @ApiResponse(responseCode = "403", description = "Usuario sem acesso ao projeto")
    })
    ResponseEntity<GceDTO> update(@Parameter(description = "Identificador do GCE") @PathVariable UUID id,
                                  @RequestBody GceInputDTO graph);

    @PostMapping(GCE_NODES)
    @Operation(
            summary = "Adicionar no ao GCE",
            description = "Adiciona um novo no ao grafo. Para nos operadores, o payload deve informar os nos de entrada e o no de destino, "
                    + "permitindo a criacao automatica das arestas IDENTITY."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "No adicionado com sucesso",
                    content = @Content(schema = @Schema(implementation = GceDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Configuracao do no invalida"),
            @ApiResponse(responseCode = "404", description = "GCE nao encontrado"),
            @ApiResponse(responseCode = "403", description = "Usuario sem acesso ao projeto")
    })
    ResponseEntity<GceDTO> addNode(@Parameter(description = "Identificador do GCE") @PathVariable UUID id,
                                   @RequestBody AddGceNodeDTO node);

    @PatchMapping(GCE_NODE)
    @Operation(
            summary = "Atualizar no do GCE",
            description = "Atualiza dados editaveis de um no existente, como rotulo e, no caso de operadores, o operador logico."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "No atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = GceDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados do no invalidos"),
            @ApiResponse(responseCode = "404", description = "GCE ou no nao encontrado"),
            @ApiResponse(responseCode = "403", description = "Usuario sem acesso ao projeto")
    })
    ResponseEntity<GceDTO> updateNode(@Parameter(description = "Identificador do GCE") @PathVariable UUID id,
                                      @Parameter(description = "Codigo do no dentro do GCE") @PathVariable String nodeCode,
                                      @RequestBody UpdateGceNodeDTO node);

    @PatchMapping(GCE_EDGE_TOGGLE)
    @Operation(
            summary = "Inverter aresta do GCE",
            description = "Alterna o tipo de uma aresta entre IDENTITY e NEGATED."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Aresta invertida com sucesso",
                    content = @Content(schema = @Schema(implementation = GceDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "GCE ou aresta nao encontrado"),
            @ApiResponse(responseCode = "403", description = "Usuario sem acesso ao projeto")
    })
    ResponseEntity<GceDTO> toggleEdge(@Parameter(description = "Identificador do GCE") @PathVariable UUID id,
                                      @Parameter(description = "Identificador da aresta") @PathVariable UUID edgeId);
}
