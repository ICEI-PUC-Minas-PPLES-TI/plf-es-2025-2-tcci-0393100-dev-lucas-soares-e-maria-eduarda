package br.pucminas.graphtest.application.service.gce;

import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gce.model.GceEdge;
import br.pucminas.graphtest.application.domain.gce.model.GceNode;
import br.pucminas.graphtest.application.domain.gce.model.GceRestriction;
import br.pucminas.graphtest.application.domain.gce.enums.GceOperatorTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.RestrictionTypeEnum;
import br.pucminas.graphtest.application.port.input.gce.records.ValidationGceMessage;
import br.pucminas.graphtest.application.port.input.gce.records.ValidationGceOutput;
import br.pucminas.graphtest.application.service.gce.interfaces.GceValidationResultService;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Implementa a validacao completa de um Grafo de Causa e Efeito.
 *
 * <p>Este servico concentra as verificacoes estruturais e semanticas do modelo
 * de GCE antes de sua persistencia ou quando o grafo salvo precisa ser
 * reavaliado. O processamento e executado em etapas, interrompendo as fases
 * seguintes quando um erro critico torna o restante da avaliacao irrelevante.</p>
 *
 * <p>As validacoes cobrem, entre outros aspectos:</p>
 *
 * <ul>
 *     <li>estrutura minima do grafo, incluindo existencia de causas e efeitos;</li>
 *     <li>cardinalidade e direcao esperada para arestas e operadores;</li>
 *     <li>compatibilidade entre tipos de restricao e os nos referenciados;</li>
 *     <li>ausencia de ciclos e alcancabilidade dos efeitos a partir das causas;</li>
 *     <li>consistencia de influencia logica das causas ao longo do grafo;</li>
 *     <li>avaliacao semantica por enumeracao das combinacoes validas de causas,
 *     respeitando as constraints declaradas.</li>
 * </ul>
 *
 * <p>Para evitar custo exponencial descontrolado, a enumeracao completa das
 * combinacoes de causas e limitada por {@value #MAX_CAUSES_FOR_FULL_ENUMERATION}.
 * Acima desse limite, a validacao estrutural continua sendo executada e a
 * validacao semantica exaustiva passa a emitir apenas aviso.</p>
 */
public class GceValidationResultServiceImpl implements GceValidationResultService {

    private static final int MAX_CAUSES_FOR_FULL_ENUMERATION = 16;

    /**
     * Executa o pipeline completo de validacao do GCE informado.
     *
     * <p>O resultado consolidado retorna erros e avisos. Quando existem erros,
     * o campo {@code valid} da saida sera {@code false}.</p>
     *
     * @param graph agregado de GCE a ser analisado
     * @return consolidado da validacao estrutural e semantica
     */
    @Override
    public ValidationGceOutput validate(Gce graph) {
        List<ValidationGceMessage> errors = new ArrayList<>();
        List<ValidationGceMessage> warnings = new ArrayList<>();

        validateBasicStructure(graph, errors);
        validateNodeTypesAndCardinality(graph, errors, warnings);
        validateRestrictions(graph, errors);

        if (!hasCriticalErrors(errors)) {
            validateAcyclic(graph, errors);
        }

        if (!hasCriticalErrors(errors)) {
            validateReachability(graph, errors);
        }

        if (!hasCriticalErrors(errors)) {
            validateInfluenceConsistency(graph, errors);
        }

        if (!hasCriticalErrors(errors)) {
            validateSemanticConsistency(graph, errors, warnings);
        }

        return new ValidationGceOutput(errors, warnings);
    }

    /**
     * Indica se a etapa atual de validacao ja acumulou erros impeditivos.
     *
     * @param errors colecao de erros acumulados
     * @return {@code true} quando existe ao menos um erro registrado
     */
    private boolean hasCriticalErrors(List<ValidationGceMessage> errors) {
        return !errors.isEmpty();
    }

    /**
     * Verifica a estrutura minima e referencias basicas do grafo.
     *
     * <p>Esta etapa garante a existencia de nos essenciais e detecta arestas que
     * apontam para codigos inexistentes antes de validacoes mais sofisticadas.</p>
     *
     * @param graph agregado de GCE em analise
     * @param errors colecao acumuladora de erros encontrados
     */
    private void validateBasicStructure(Gce graph, List<ValidationGceMessage> errors) {
        if (graph.getNodes().isEmpty()) {
            addError(errors, "GCE_001", "O grafo precisa ter pelo menos um no.");
        }

        if (graph.getCauseNodes().isEmpty()) {
            addError(errors, "GCE_002", "O grafo precisa ter pelo menos uma causa.");
        }

        if (graph.getEffectNodes().isEmpty()) {
            addError(errors, "GCE_003", "O grafo precisa ter pelo menos um efeito.");
        }

        graph.countNodeCodes().forEach((code, count) -> {
            if (count > 1) {
                addError(errors, "GCE_004", "O codigo do no " + code + " esta repetido no grafo.");
            }
        });

        for (GceEdge edge : graph.getEdges()) {
            if (graph.findNode(edge.getSourceNodeCode()).isEmpty()) {
                addError(errors, "GCE_005", "Existe uma aresta saindo de um no que nao existe: " + edge.getSourceNodeCode() + ".");
            }
            if (graph.findNode(edge.getTargetNodeCode()).isEmpty()) {
                addError(errors, "GCE_006", "Existe uma aresta chegando a um no que nao existe: " + edge.getTargetNodeCode() + ".");
            }
        }
    }

    /**
     * Valida regras de cardinalidade conforme o tipo de cada no.
     *
     * <p>Causas nao podem receber entrada, efeitos nao podem propagar saida e
     * operadores precisam respeitar a quantidade minima de entradas, possuir
     * ao menos uma saida e, quando apontarem diretamente para efeitos,
     * no maximo um efeito por operador.</p>
     *
     * @param graph agregado de GCE em analise
     * @param errors colecao acumuladora de erros encontrados
     * @param warnings colecao acumuladora de avisos encontrados
     */
    private void validateNodeTypesAndCardinality(Gce graph, List<ValidationGceMessage> errors, List<ValidationGceMessage> warnings) {
        for (GceNode node : graph.getNodes()) {
            List<GceEdge> incoming = graph.incomingEdges(node.getCode());
            List<GceEdge> outgoing = graph.outgoingEdges(node.getCode());

            if (node.isCause()) {
                if (!incoming.isEmpty()) {
                    addError(errors, "GCE_007", "A causa " + node.getCode() + " nao pode receber entrada de outro no.");
                }
                if (outgoing.isEmpty()) {
                    addWarning(warnings, "GCE_008", "A causa " + node.getCode() + " esta solta e nao influencia nenhum outro no.");
                }
            }

            if (node.isEffect()) {
                if (!outgoing.isEmpty()) {
                    addError(errors, "GCE_009", "O efeito " + node.getCode() + " nao pode apontar para outro no.");
                }
                if (incoming.isEmpty()) {
                    addError(errors, "GCE_010", "O efeito " + node.getCode() + " precisa receber entrada de pelo menos um no.");
                }
            }

            if (node.isOperator()) {
                if (incoming.size() < 2) {
                    addError(errors, "GCE_011", "O operador " + node.getCode() + " precisa receber entrada de dois nos.");
                }
                if (incoming.size() > 2) {
                    addError(errors, "GCE_024", "O operador " + node.getCode() + " recebe entrada de mais de dois nos.");
                }
                if (outgoing.isEmpty()) {
                    addError(errors, "GCE_012", "O operador " + node.getCode() + " nao esta ligado a nenhum destino.");
                }

                long effectOutputs = outgoing.stream()
                        .map(edge -> graph.findNode(edge.getTargetNodeCode()))
                        .flatMap(Optional::stream)
                        .filter(GceNode::isEffect)
                        .count();

                if (effectOutputs > 1) {
                    addError(errors, "GCE_025", "O operador " + node.getCode() + " esta ligado diretamente a mais de um efeito.");
                }
            }
        }
    }

    /**
     * Verifica se cada restricao referencia nos compativeis com seu tipo.
     *
     * @param graph agregado de GCE em analise
     * @param errors colecao acumuladora de erros encontrados
     */
    private void validateRestrictions(Gce graph, List<ValidationGceMessage> errors) {
        for (GceRestriction restriction : graph.getRestrictions()) {
            List<GceNode> nodes = restriction.getNodeCodes().stream()
                    .map(graph::findNode)
                    .flatMap(Optional::stream)
                    .toList();

            if (nodes.size() != restriction.getNodeCodes().size()) {
                addError(errors, "GCE_013", "Existe uma restricao apontando para um no que nao existe.");
                continue;
            }

            switch (restriction.getType()) {
                case EXCLUSIVE, INCLUSIVE, ONE_AND_ONLY_ONE, REQUIRE -> {
                    boolean allCauses = nodes.stream().allMatch(GceNode::isCause);
                    if (!allCauses) {
                        addError(errors, "GCE_014", "As restricoes entre causas so podem ser aplicadas em nos do tipo causa.");
                    }
                }
                case MASKS -> {
                    boolean allEffects = nodes.stream().allMatch(GceNode::isEffect);
                    if (!allEffects) {
                        addError(errors, "GCE_015", "A restricao MASKS so pode ser aplicada em nos do tipo efeito.");
                    }
                }
            }
        }
    }

    /**
     * Detecta ciclos direcionados no grafo.
     *
     * @param graph agregado de GCE em analise
     * @param errors colecao acumuladora de erros encontrados
     */
    private void validateAcyclic(Gce graph, List<ValidationGceMessage> errors) {
        Map<String, List<String>> adjacency = new HashMap<>();
        for (GceNode node : graph.getNodes()) {
            adjacency.put(node.getCode(), new ArrayList<>());
        }

        for (GceEdge edge : graph.getEdges()) {
            adjacency.computeIfAbsent(edge.getSourceNodeCode(), ignored -> new ArrayList<>())
                    .add(edge.getTargetNodeCode());
        }

        Set<String> visited = new HashSet<>();
        Set<String> stack = new HashSet<>();

        for (GceNode node : graph.getNodes()) {
            if (detectCycle(node.getCode(), adjacency, visited, stack)) {
                addError(errors, "GCE_016", "O grafo possui um ciclo. Revise as ligacoes para que o fluxo siga em uma unica direcao.");
                return;
            }
        }
    }

    /**
     * Executa busca em profundidade para identificar ciclos.
     *
     * @param current codigo do no atualmente visitado
     * @param adjacency lista de adjacencia do grafo
     * @param visited conjunto de nos ja processados definitivamente
     * @param stack conjunto de nos no caminho atual de recursao
     * @return {@code true} quando um ciclo e encontrado
     */
    private boolean detectCycle(String current,
                                Map<String, List<String>> adjacency,
                                Set<String> visited,
                                Set<String> stack) {
        if (stack.contains(current)) {
            return true;
        }
        if (visited.contains(current)) {
            return false;
        }

        visited.add(current);
        stack.add(current);

        for (String next : adjacency.getOrDefault(current, List.of())) {
            if (detectCycle(next, adjacency, visited, stack)) {
                return true;
            }
        }

        stack.remove(current);
        return false;
    }

    /**
     * Garante que todo efeito possa ser alcancado a partir de ao menos uma causa.
     *
     * @param graph agregado de GCE em analise
     * @param errors colecao acumuladora de erros encontrados
     */
    private void validateReachability(Gce graph, List<ValidationGceMessage> errors) {
        Set<String> reachable = new HashSet<>();
        Deque<String> queue = new ArrayDeque<>();

        for (GceNode cause : graph.getCauseNodes()) {
            queue.add(cause.getCode());
            reachable.add(cause.getCode());
        }

        while (!queue.isEmpty()) {
            String current = queue.poll();
            for (GceEdge edge : graph.outgoingEdges(current)) {
                if (reachable.add(edge.getTargetNodeCode())) {
                    queue.add(edge.getTargetNodeCode());
                }
            }
        }

        for (GceNode effect : graph.getEffectNodes()) {
            if (!reachable.contains(effect.getCode())) {
                addError(errors, "GCE_017", "O efeito " + effect.getCode() + " nao pode ser alcancado a partir de nenhuma causa.");
            }
        }
    }

    /**
     * Executa a validacao semantica exaustiva do modelo dentro do limite configurado.
     *
     * <p>Somente combinacoes de causas que respeitam as restricoes entre causas
     * sao avaliadas. O metodo falha quando o grafo nao pode ser resolvido para
     * alguma combinacao valida, quando restricoes entre efeitos sao violadas ou
     * quando nenhuma combinacao valida produz avaliacao consistente.</p>
     *
     * @param graph agregado de GCE em analise
     * @param errors colecao acumuladora de erros encontrados
     * @param warnings colecao acumuladora de avisos encontrados
     */
    private void validateSemanticConsistency(Gce graph, List<ValidationGceMessage> errors, List<ValidationGceMessage> warnings) {
        List<GceNode> causes = graph.getCauseNodes();
        if (causes.size() > MAX_CAUSES_FOR_FULL_ENUMERATION) {
            addWarning(warnings, "GCE_018", "O grafo tem muitas causas. Por isso, a validacao completa das combinacoes foi ignorada.");
            return;
        }

        List<Map<String, Boolean>> assignments = enumerateAssignments(causes);
        int validCauseAssignments = 0;
        int validEvaluatedAssignments = 0;

        for (Map<String, Boolean> assignment : assignments) {
            if (!respectsCauseRestrictions(graph, assignment)) {
                continue;
            }

            validCauseAssignments++;

            Map<String, Boolean> allValues = evaluateGraph(graph, assignment);
            if (allValues == null) {
                addError(errors, "GCE_019", "O grafo nao conseguiu ser resolvido para todas as combinacoes validas de causas.");
                return;
            }

            if (!respectsMaskRestrictions(graph, allValues)) {
                addError(errors, "GCE_020", "Existe pelo menos uma combinacao valida de causas que faz dois efeitos mascarados ficarem verdadeiros ao mesmo tempo.");
                return;
            }

            validEvaluatedAssignments++;
        }

        if (validCauseAssignments == 0) {
            addError(
                    errors,
                    "GCE_021",
                    "Nao existe nenhuma combinacao de causas que respeite ao mesmo tempo a estrutura do grafo e as restricoes definidas."
            );
            return;
        }

        if (validEvaluatedAssignments == 0) {
            addError(errors, "GCE_022", "As combinacoes de causas permitidas nao produzem nenhum resultado valido para o grafo.");
        }
    }

    /**
     * Detecta propagacoes contraditorias de uma mesma causa ao longo do grafo.
     *
     * <p>Uma contradicao ocorre quando a mesma causa alcanca um no com duas
     * polaridades distintas, por exemplo por caminhos em que uma aresta mantem
     * o valor e outra o nega. Esse tipo de configuracao torna a interpretacao
     * logica do modelo ambigua.</p>
     *
     * @param graph agregado de GCE em analise
     * @param errors colecao acumuladora de erros encontrados
     */
    private void validateInfluenceConsistency(Gce graph, List<ValidationGceMessage> errors) {
        List<GceNode> ordered = topologicalOrder(graph);
        if (ordered.isEmpty()) {
            return;
        }

        Map<String, Map<String, Set<Boolean>>> influencesByNode = new HashMap<>();
        for (GceNode node : ordered) {
            influencesByNode.put(node.getCode(), new HashMap<>());
            if (node.isCause()) {
                influencesByNode.get(node.getCode()).put(node.getCode(), new HashSet<>(Set.of(Boolean.TRUE)));
            }
        }

        for (GceNode node : ordered) {
            Map<String, Set<Boolean>> currentInfluences = influencesByNode.getOrDefault(node.getCode(), Map.of());
            for (GceEdge edge : graph.outgoingEdges(node.getCode())) {
                Map<String, Set<Boolean>> targetInfluences =
                        influencesByNode.computeIfAbsent(edge.getTargetNodeCode(), ignored -> new HashMap<>());
                boolean negated = edge.isNegated();

                for (Map.Entry<String, Set<Boolean>> entry : currentInfluences.entrySet()) {
                    Set<Boolean> propagatedPolarities =
                            targetInfluences.computeIfAbsent(entry.getKey(), ignored -> new HashSet<>());

                    for (Boolean polarity : entry.getValue()) {
                        propagatedPolarities.add(negated ? !polarity : polarity);
                    }

                    if (propagatedPolarities.size() > 1) {
                        addError(
                                errors,
                                "GCE_023",
                                "A causa " + entry.getKey() + " chega ao no " + edge.getTargetNodeCode()
                                        + " por caminhos que se contradizem: em um caminho ela mantem o valor e em outro ela inverte o valor."
                        );
                        return;
                    }
                }
            }
        }
    }

    /**
     * Registra um erro padronizado no resultado da validacao.
     *
     * @param errors colecao acumuladora de erros
     * @param code codigo identificador da regra violada
     * @param message mensagem descritiva do problema
     */
    private void addError(List<ValidationGceMessage> errors, String code, String message) {
        errors.add(new ValidationGceMessage(code, message));
    }

    /**
     * Registra um aviso padronizado no resultado da validacao.
     *
     * @param warnings colecao acumuladora de avisos
     * @param code codigo identificador do aviso
     * @param message mensagem descritiva da ocorrencia
     */
    private void addWarning(List<ValidationGceMessage> warnings, String code, String message) {
        warnings.add(new ValidationGceMessage(code, message));
    }

    /**
     * Gera todas as atribuicoes booleanas possiveis para as causas do grafo.
     *
     * @param causes lista de nos do tipo causa
     * @return lista contendo uma atribuicao por combinacao possivel
     */
    private List<Map<String, Boolean>> enumerateAssignments(List<GceNode> causes) {
        int size = causes.size();
        int combinations = 1 << size;
        List<Map<String, Boolean>> assignments = new ArrayList<>(combinations);

        for (int mask = 0; mask < combinations; mask++) {
            Map<String, Boolean> assignment = new HashMap<>();
            for (int i = 0; i < size; i++) {
                boolean value = (mask & (1 << i)) != 0;
                assignment.put(causes.get(i).getCode(), value);
            }
            assignments.add(assignment);
        }

        return assignments;
    }

    /**
     * Verifica se uma atribuicao de causas respeita as restricoes entre causas.
     *
     * <p>As restricoes do tipo {@code MASKS} sao ignoradas aqui porque dependem
     * do valor dos efeitos, os quais so podem ser avaliados apos a execucao do
     * grafo.</p>
     *
     * @param graph agregado de GCE em analise
     * @param assignment atribuicao booleana das causas
     * @return {@code true} quando a combinacao respeita as restricoes de causas
     */
    private boolean respectsCauseRestrictions(Gce graph, Map<String, Boolean> assignment) {
        for (GceRestriction restriction : graph.getRestrictions()) {
            if (restriction.getType() == RestrictionTypeEnum.MASKS) {
                continue;
            }

            List<Boolean> values = restriction.getNodeCodes().stream()
                    .map(assignment::get)
                    .filter(Objects::nonNull)
                    .toList();

            switch (restriction.getType()) {
                case EXCLUSIVE -> {
                    long trueCount = values.stream().filter(Boolean::booleanValue).count();
                    if (trueCount > 1) {
                        return false;
                    }
                }
                case INCLUSIVE -> {
                    boolean anyTrue = values.stream().anyMatch(Boolean::booleanValue);
                    if (!anyTrue) {
                        return false;
                    }
                }
                case ONE_AND_ONLY_ONE -> {
                    long trueCount = values.stream().filter(Boolean::booleanValue).count();
                    if (trueCount != 1) {
                        return false;
                    }
                }
                case REQUIRE -> {
                    boolean first = assignment.getOrDefault(restriction.firstNode(), false);
                    boolean second = assignment.getOrDefault(restriction.secondNode(), false);
                    if (first && !second) {
                        return false;
                    }
                }
                case MASKS -> {
                }
            }
        }

        return true;
    }

    /**
     * Verifica se os efeitos avaliados respeitam as restricoes do tipo MASKS.
     *
     * @param graph agregado de GCE em analise
     * @param allValues mapa contendo os valores avaliados de causas, operadores e efeitos
     * @return {@code true} quando nenhuma restricao M e violada
     */
    private boolean respectsMaskRestrictions(Gce graph, Map<String, Boolean> allValues) {
        for (GceRestriction restriction : graph.getRestrictions()) {
            if (restriction.getType() != RestrictionTypeEnum.MASKS) {
                continue;
            }

            boolean masker = allValues.getOrDefault(restriction.firstNode(), false);
            boolean masked = allValues.getOrDefault(restriction.secondNode(), false);

            if (masker && masked) {
                return false;
            }
        }

        return true;
    }

    /**
     * Avalia o grafo a partir de uma atribuicao inicial das causas.
     *
     * <p>Os nos sao processados em ordem topologica. Causas usam os valores ja
     * fornecidos na atribuicao inicial; operadores resolvem sua expressao com
     * base nas entradas recebidas; efeitos propagam o unico valor de entrada
     * esperado.</p>
     *
     * @param graph agregado de GCE em analise
     * @param causeValues valores booleanos atribuidos as causas
     * @return mapa com todos os valores avaliados, ou {@code null} quando a
     * estrutura impede a avaliacao consistente
     */
    private Map<String, Boolean> evaluateGraph(Gce graph, Map<String, Boolean> causeValues) {
        Map<String, Boolean> values = new HashMap<>(causeValues);

        List<GceNode> ordered = topologicalOrder(graph);
        if (ordered.isEmpty()) {
            return null;
        }

        for (GceNode node : ordered) {
            if (node.isCause()) {
                continue;
            }

            List<GceEdge> incoming = graph.incomingEdges(node.getCode());
            List<Boolean> inputs = new ArrayList<>();

            for (GceEdge edge : incoming) {
                Boolean sourceValue = values.get(edge.getSourceNodeCode());
                if (sourceValue == null) {
                    return null;
                }
                inputs.add(edge.getType().apply(sourceValue));
            }

            boolean resolved;
            if (node.isOperator()) {
                resolved = resolveOperator(node.getOperatorType(), inputs);
            } else {
                if (inputs.size() != 1) {
                    return null;
                }
                resolved = inputs.get(0);
            }

            values.put(node.getCode(), resolved);
        }

        return values;
    }

    /**
     * Resolve o valor booleano produzido por um no operador.
     *
     * @param operatorType operador logico configurado no no
     * @param inputs valores de entrada ja propagados para o operador
     * @return resultado da aplicacao do operador sobre as entradas
     */
    private boolean resolveOperator(GceOperatorTypeEnum operatorType, List<Boolean> inputs) {
        return switch (operatorType) {
            case AND -> inputs.stream().allMatch(Boolean::booleanValue);
            case OR -> inputs.stream().anyMatch(Boolean::booleanValue);
        };
    }

    /**
     * Calcula a ordem topologica dos nos do grafo.
     *
     * <p>Quando o grafo contem ciclo, o metodo retorna lista vazia como sinal de
     * que a avaliacao logica ordenada nao pode ser realizada.</p>
     *
     * @param graph agregado de GCE em analise
     * @return lista ordenada topologicamente ou lista vazia quando ha ciclo
     */
    private List<GceNode> topologicalOrder(Gce graph) {
        Map<String, Integer> inDegree = new HashMap<>();
        Map<String, List<String>> adjacency = new HashMap<>();

        for (GceNode node : graph.getNodes()) {
            inDegree.put(node.getCode(), 0);
            adjacency.put(node.getCode(), new ArrayList<>());
        }

        for (GceEdge edge : graph.getEdges()) {
            adjacency.computeIfAbsent(edge.getSourceNodeCode(), ignored -> new ArrayList<>())
                    .add(edge.getTargetNodeCode());
            inDegree.compute(edge.getTargetNodeCode(), (key, value) -> value == null ? 1 : value + 1);
        }

        Deque<String> queue = new ArrayDeque<>();
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        List<GceNode> ordered = new ArrayList<>();
        while (!queue.isEmpty()) {
            String current = queue.poll();
            graph.findNode(current).ifPresent(ordered::add);

            for (String next : adjacency.getOrDefault(current, List.of())) {
                int newValue = inDegree.compute(next, (key, value) -> value - 1);
                if (newValue == 0) {
                    queue.add(next);
                }
            }
        }

        if (ordered.size() != graph.getNodes().size()) {
            return List.of();
        }

        return ordered;
    }
}
