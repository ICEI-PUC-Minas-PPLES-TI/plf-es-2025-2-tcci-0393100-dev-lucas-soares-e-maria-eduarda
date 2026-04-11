package br.pucminas.graphtest.application.service.gce;

import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gce.model.GceEdge;
import br.pucminas.graphtest.application.domain.gce.model.GceNode;
import br.pucminas.graphtest.application.domain.gce.model.GceRestriction;
import br.pucminas.graphtest.application.domain.gce.enums.GceEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceNodeTypeEnum;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.exception.InvalidGceModelException;
import br.pucminas.graphtest.application.port.input.gce.records.GceEdgeInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceNodeInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceRestrictionInput;
import br.pucminas.graphtest.application.port.input.gce.records.ValidationGceOutput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.gce.interfaces.GceMutationService;
import br.pucminas.graphtest.application.service.gce.interfaces.GceValidationResultService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Implementacao concreta do servico de apoio para mutacoes do GCE.
 *
 * <p>Este servico concentra operacoes reutilizaveis que aparecem em varios
 * casos de uso do agregado {@link Gce}. Entre suas responsabilidades estao:</p>
 *
 * <ul>
 *     <li>carregar um grafo e validar o acesso ao projeto associado;</li>
 *     <li>converter payloads de entrada em entidades de dominio;</li>
 *     <li>gerar arestas automaticas a partir do contrato de conexoes dos nos;</li>
 *     <li>recalcular o {@code label} dos nos operadores;</li>
 *     <li>executar a validacao completa do modelo e interromper o fluxo quando
 *     o grafo estiver invalido.</li>
 * </ul>
 *
 * <p>Na pratica, a classe funciona como uma camada de orquestracao entre os
 * casos de uso e o dominio, evitando duplicacao da logica de montagem e
 * revalidacao do GCE.</p>
 */
public class GceMutationServiceImpl implements GceMutationService {

    /**
     * Carrega um GCE persistido e garante que o usuario atual tenha acesso ao
     * projeto ao qual esse grafo pertence.
     *
     * @param id identificador do GCE
     * @param gceRepository repositorio usado para recuperar o agregado
     * @param projectAccessService servico usado para validar acesso ao projeto
     * @return grafo encontrado e autorizado
     * @throws EntityNotFoundException quando o GCE nao existe
     */
    @Override
    public Gce loadAuthorizedGraph(UUID id,
                                   GceRepositoryPort gceRepository,
                                   ProjectAccessService projectAccessService) {
        Gce graph = gceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("GCE nao encontrado"));
        projectAccessService.findAuthorizedProject(graph.getProjectId());
        return graph;
    }

    /**
     * Executa a validacao completa do grafo informado e lanca excecao quando o
     * resultado for invalido.
     *
     * @param graph agregado a ser validado
     * @param validationService servico responsavel pela validacao do modelo
     * @throws InvalidGceModelException quando o grafo possuir erros de validacao
     */
    @Override
    public void validateAndThrow(Gce graph, GceValidationResultService validationService) {
        ValidationGceOutput validation = validationService.validate(graph);
        if (!validation.valid()) {
            throw new InvalidGceModelException("GCE invalido: " + validation.errors());
        }
    }

    /**
     * Converte os nos de entrada para entidades de dominio.
     *
     * <p>Cada no convertido recebe audit fields de criacao e ja passa pela
     * validacao do contrato de conexoes automaticas.</p>
     *
     * @param nodes lista de nos de entrada
     * @return colecao convertida para dominio; lista vazia quando a entrada for nula
     */
    @Override
    public Collection<GceNode> toNodes(List<GceNodeInput> nodes) {
        if (nodes == null) {
            return List.of();
        }

        return nodes.stream()
                .map(this::toNode)
                .toList();
    }

    /**
     * Converte as arestas do payload em arestas de dominio.
     *
     * <p>O metodo combina duas fontes:</p>
     *
     * <ul>
     *     <li>arestas automaticas derivadas de {@code sourceNodeCodes} e
     *     {@code targetNodeCodes} definidos nos nos de entrada;</li>
     *     <li>arestas explicitas enviadas diretamente no payload.</li>
     * </ul>
     *
     * @param nodes nos do payload, usados para derivar conexoes automaticas
     * @param explicitEdges arestas explicitamente enviadas
     * @return colecao consolidada de arestas de dominio
     */
    @Override
    public Collection<GceEdge> toEdges(List<GceNodeInput> nodes, List<GceEdgeInput> explicitEdges) {
        List<GceEdge> edges = new ArrayList<>();
        if (nodes != null) {
            for (GceNodeInput node : nodes) {
                edges.addAll(buildAutomaticEdges(node));
            }
        }

        if (explicitEdges != null) {
            explicitEdges.stream()
                    .map(edge -> markAsCreated(new GceEdge(
                            UUID.randomUUID(),
                            edge.sourceNodeCode(),
                            edge.targetNodeCode(),
                            edge.type()
                    )))
                    .forEach(edges::add);
        }

        return edges;
    }

    /**
     * Converte as restricoes do payload em entidades de dominio.
     *
     * @param restrictions lista de restricoes de entrada
     * @return colecao convertida para dominio; lista vazia quando a entrada for nula
     */
    @Override
    public Collection<GceRestriction> toRestrictions(List<GceRestrictionInput> restrictions) {
        if (restrictions == null) {
            return List.of();
        }

        return restrictions.stream()
                .map(restriction -> markAsCreated(new GceRestriction(
                        null,
                        restriction.type(),
                        restriction.nodeCodes()
                )))
                .toList();
    }

    /**
     * Adiciona um novo no ao grafo e, quando o payload define conexoes
     * automaticas, cria tambem as arestas correspondentes.
     *
     * @param graph agregado que sera mutado
     * @param nodeInput definicao do no a ser inserido
     */
    @Override
    public void addNodeWithAutomaticEdges(Gce graph, GceNodeInput nodeInput) {
        graph.addNode(toNode(nodeInput));
        for (GceEdge edge : buildAutomaticEdges(nodeInput)) {
            graph.addEdge(edge);
        }
    }

    /**
     * Recalcula o {@code label} de todos os operadores do grafo a partir da
     * expressao logica formada por suas entradas.
     *
     * <p>Nos nao operadores nao tem o label recalculado. Para operadores, o
     * texto final considera:</p>
     *
     * <ul>
     *     <li>os codigos dos nos de entrada;</li>
     *     <li>o tipo do operador ({@code AND} ou {@code OR});</li>
     *     <li>a negacao das arestas de entrada, quando existir.</li>
     * </ul>
     *
     * @param graph agregado cujo conjunto de operadores sera atualizado
     */
    @Override
    public void refreshOperatorLabels(Gce graph) {
        Map<String, String> expressionByNodeCode = new HashMap<>();

        for (GceNode operatorNode : graph.getOperatorNodes()) {
            String generatedLabel = buildNodeExpression(graph, operatorNode.getCode(), expressionByNodeCode, new HashSet<>());
            GceNode updatedOperatorNode = new GceNode(
                    operatorNode.getId(),
                    operatorNode.getCode(),
                    generatedLabel,
                    operatorNode.getType(),
                    operatorNode.getOperatorType()
            );
            updatedOperatorNode.restoreAuditFields(operatorNode.getCreatedAt(), operatorNode.getUpdatedAt());
            if (!generatedLabel.equals(operatorNode.getLabel())) {
                updatedOperatorNode.markUpdatedNow();
            }
            graph.replaceNode(updatedOperatorNode);
        }
    }

    /**
     * Converte um no de entrada para a entidade de dominio correspondente.
     *
     * @param node definicao do no recebida pelo caso de uso
     * @return no de dominio com audit fields de criacao preenchidos
     */
    private GceNode toNode(GceNodeInput node) {
        Objects.requireNonNull(node, "node e obrigatorio.");
        validateNodeConnectionContract(node);
        return markAsCreated(new GceNode(null, node.code(), resolveInitialLabel(node), node.type(), node.operatorType()));
    }

    /**
     * Resolve o label inicial do no no momento da conversao.
     *
     * <p>Para operadores, o label inicial e o proprio {@code code}, pois o
     * valor final sera recalculado depois. Para causa e efeito, o label vem do
     * payload.</p>
     *
     * @param node definicao do no recebida no payload
     * @return label inicial do no
     */
    private String resolveInitialLabel(GceNodeInput node) {
        if (node.type() == GceNodeTypeEnum.OPERATOR) {
            return node.code();
        }
        return node.label();
    }

    /**
     * Gera automaticamente as arestas implícitas descritas no payload do no.
     *
     * <p>O metodo transforma:</p>
     *
     * <ul>
     *     <li>{@code sourceNodeCodes} em arestas {@code origem -> node.code()}</li>
     *     <li>{@code targetNodeCodes} em arestas {@code node.code() -> destino}</li>
     * </ul>
     *
     * <p>Todas as arestas geradas aqui sao do tipo {@link GceEdgeTypeEnum#IDENTITY}.</p>
     *
     * @param node definicao do no recebido no payload
     * @return lista de arestas automaticas derivadas do contrato de conexao
     */
    private List<GceEdge> buildAutomaticEdges(GceNodeInput node) {
        validateNodeConnectionContract(node);

        List<GceEdge> edges = new ArrayList<>();
        for (String sourceNodeCode : normalizeNodeCodes(node.sourceNodeCodes())) {
            edges.add(markAsCreated(new GceEdge(UUID.randomUUID(), sourceNodeCode, node.code(), GceEdgeTypeEnum.IDENTITY)));
        }
        for (String targetNodeCode : normalizeNodeCodes(node.targetNodeCodes())) {
            edges.add(markAsCreated(new GceEdge(UUID.randomUUID(), node.code(), targetNodeCode, GceEdgeTypeEnum.IDENTITY)));
        }
        return edges;
    }

    /**
     * Marca uma entidade de dominio como criada no instante atual.
     *
     * @param entity entidade a ser anotada com audit fields de criacao
     * @param <T> tipo concreto da entidade
     * @return a propria entidade recebida
     */
    private <T extends br.pucminas.graphtest.application.domain.shared.model.BaseEntity> T markAsCreated(T entity) {
        entity.markCreatedNow();
        return entity;
    }

    /**
     * Valida o contrato de conexoes automaticas aceito para cada tipo de no.
     *
     * <p>As regras atuais sao:</p>
     *
     * <ul>
     *     <li>{@code CAUSE}: nao pode informar {@code sourceNodeCodes};</li>
     *     <li>{@code EFFECT}: deve informar exatamente um {@code sourceNodeCode}
     *     e nao pode informar {@code targetNodeCodes};</li>
     *     <li>{@code OPERATOR}: deve informar exatamente dois
     *     {@code sourceNodeCodes} e exatamente um {@code targetNodeCode}.</li>
     * </ul>
     *
     * <p>Quando nenhum codigo de conexao e informado, o contrato automatico nao
     * e aplicado.</p>
     *
     * @param node definicao do no recebida no payload
     */
    private void validateNodeConnectionContract(GceNodeInput node) {
        List<String> sourceNodeCodes = normalizeNodeCodes(node.sourceNodeCodes());
        List<String> targetNodeCodes = normalizeNodeCodes(node.targetNodeCodes());
        boolean hasAutomaticConnections = !sourceNodeCodes.isEmpty() || !targetNodeCodes.isEmpty();

        if (!hasAutomaticConnections) {
            return;
        }

        if (node.type() == GceNodeTypeEnum.CAUSE) {
            if (!sourceNodeCodes.isEmpty()) {
                throw new IllegalArgumentException("No CAUSE nao pode receber sourceNodeCodes.");
            }
            return;
        }

        if (node.type() == GceNodeTypeEnum.EFFECT) {
            if (sourceNodeCodes.size() != 1) {
                throw new IllegalArgumentException("No EFFECT deve informar exatamente 1 sourceNodeCode.");
            }
            if (!targetNodeCodes.isEmpty()) {
                throw new IllegalArgumentException("No EFFECT nao pode informar targetNodeCodes.");
            }
            return;
        }

        if (sourceNodeCodes.size() != 2) {
            throw new IllegalArgumentException("No OPERATOR deve informar exatamente 2 sourceNodeCodes.");
        }
        if (targetNodeCodes.size() != 1) {
            throw new IllegalArgumentException("No OPERATOR deve informar exatamente 1 targetNodeCode.");
        }
    }

    /**
     * Normaliza listas de codigos de no vindas do payload.
     *
     * <p>O processo remove:</p>
     *
     * <ul>
     *     <li>listas nulas, convertendo-as para lista vazia;</li>
     *     <li>valores nulos;</li>
     *     <li>espacos excedentes no inicio e no fim;</li>
     *     <li>strings vazias ou em branco.</li>
     * </ul>
     *
     * @param nodeCodes lista recebida no payload
     * @return lista normalizada de codigos
     */
    private List<String> normalizeNodeCodes(List<String> nodeCodes) {
        if (nodeCodes == null) {
            return List.of();
        }

        return nodeCodes.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(code -> !code.isBlank())
                .toList();
    }

    /**
     * Monta recursivamente a expressao textual de um no do grafo.
     *
     * <p>Para nos comuns, a expressao e o proprio {@code code}. Para
     * operadores, a expressao e composta a partir das entradas ordenadas e do
     * tipo do operador.</p>
     *
     * @param graph agregado em analise
     * @param nodeCode codigo do no cuja expressao sera montada
     * @param expressionByNodeCode cache de expressoes ja calculadas
     * @param visiting conjunto usado para detectar ciclos na recursao
     * @return expressao textual calculada para o no
     */
    private String buildNodeExpression(Gce graph,
                                       String nodeCode,
                                       Map<String, String> expressionByNodeCode,
                                       Set<String> visiting) {
        String cachedExpression = expressionByNodeCode.get(nodeCode);
        if (cachedExpression != null) {
            return cachedExpression;
        }

        if (!visiting.add(nodeCode)) {
            throw new IllegalArgumentException("Nao foi possivel gerar a expressao do operador devido a ciclo envolvendo o no " + nodeCode);
        }

        GceNode node = graph.findNode(nodeCode)
                .orElseThrow(() -> new IllegalArgumentException("No inexistente: " + nodeCode));

        String expression;
        if (!node.isOperator()) {
            expression = node.getCode();
        } else {
            List<String> operands = graph.incomingEdges(nodeCode).stream()
                    .sorted(Comparator.comparing(GceEdge::getSourceNodeCode).thenComparing(edge -> edge.getType().name()))
                    .map(edge -> formatOperandExpression(
                            buildNodeExpression(graph, edge.getSourceNodeCode(), expressionByNodeCode, visiting),
                            edge
                    ))
                    .toList();

            String connector = node.getOperatorType().name();
            expression = operands.size() <= 1
                    ? operands.stream().findFirst().orElse(node.getCode())
                    : "(" + String.join(" " + connector + " ", operands) + ")";
        }

        visiting.remove(nodeCode);
        expressionByNodeCode.put(nodeCode, expression);
        return expression;
    }

    /**
     * Ajusta a expressao textual de um operando conforme o tipo da aresta.
     *
     * @param operandExpression expressao base do operando
     * @param edge aresta que conecta o operando ao operador atual
     * @return expressao final do operando, com negacao quando necessario
     */
    private String formatOperandExpression(String operandExpression, GceEdge edge) {
        if (edge.isNegated()) {
            return "NOT (" + operandExpression + ")";
        }

        return operandExpression;
    }
}
