package br.pucminas.graphtest.application.service.gfc.builder;

import br.pucminas.graphtest.application.domain.gfc.enums.GfcEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gfc.enums.GfcNodeTypeEnum;
import br.pucminas.graphtest.application.domain.gfc.model.GfcEdge;
import br.pucminas.graphtest.application.domain.gfc.model.GfcNode;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.WhileStmt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Builder que transforma statements JavaParser em nos e arestas iniciais do GFC.
 *
 * <p>O suporte a lacos modela a estrutura de repeticao como no {@code LOOP} e usa
 * arestas especificas para entrada no corpo, saida, retorno e fluxos de {@code break}
 * e {@code continue}. Inicializacao/update de {@code for} ainda permanecem apenas no
 * rotulo textual do no.</p>
 */
public class GfcBuilder {

    private static final String START_NODE_CODE = "N0";
    private static final String END_NODE_CODE = "N_END";
    private static final int MAX_LABEL_LENGTH = 180;

    private final List<GfcNode> nodes = new ArrayList<>();
    private final List<GfcEdge> edges = new ArrayList<>();
    private int nodeCounter = 1;

    /**
     * Constroi os elementos centrais do GFC para o metodo informado.
     *
     * @param method metodo JavaParser com corpo
     * @return resultado contendo nos e arestas gerados
     */
    public GfcControlFlowBuildResult build(MethodDeclaration method) {
        reset();

        addNode(GfcNode.start(UUID.randomUUID(), START_NODE_CODE, "Inicio"));
        addNode(GfcNode.end(UUID.randomUUID(), END_NODE_CODE, "Fim"));

        Collection<PendingEdge> exits = processStatements(
                method.getBody().orElseThrow().getStatements(),
                List.of(new PendingEdge(START_NODE_CODE, GfcEdgeTypeEnum.SEQUENTIAL, null))
        );
        connectPendingEdges(exits, END_NODE_CODE);

        return new GfcControlFlowBuildResult(List.copyOf(nodes), List.copyOf(edges));
    }

    private void reset() {
        nodes.clear();
        edges.clear();
        nodeCounter = 1;
    }

    private void addNode(GfcNode node) {
        nodes.add(node);
    }

    private Collection<PendingEdge> processStatements(Collection<Statement> statements, Collection<PendingEdge> incomingEdges) {
        Collection<PendingEdge> currentExits = incomingEdges;
        for (Statement statement : statements) {
            Collection<PendingEdge> controlFlowExits = controlFlowExits(currentExits);
            Collection<PendingEdge> normalExits = normalExits(currentExits);
            if (normalExits.isEmpty()) {
                currentExits = controlFlowExits;
                continue;
            }

            List<PendingEdge> nextExits = new ArrayList<>(controlFlowExits);
            nextExits.addAll(processStatement(statement, normalExits));
            currentExits = nextExits;
        }
        return currentExits;
    }

    private Collection<PendingEdge> processStatement(Statement statement, Collection<PendingEdge> incomingEdges) {
        if (statement.isBlockStmt()) {
            return processStatements(statement.asBlockStmt().getStatements(), incomingEdges);
        }
        if (statement.isIfStmt()) {
            return processIf(statement.asIfStmt(), incomingEdges);
        }
        if (statement.isWhileStmt()) {
            return processWhile(statement.asWhileStmt(), incomingEdges);
        }
        if (statement.isForStmt()) {
            return processFor(statement.asForStmt(), incomingEdges);
        }
        if (statement.isForEachStmt()) {
            return processForEach(statement.asForEachStmt(), incomingEdges);
        }
        if (statement.isDoStmt()) {
            return processDo(statement.asDoStmt(), incomingEdges);
        }
        if (statement.isReturnStmt()) {
            return processReturn(statement, incomingEdges);
        }
        if (statement.isBreakStmt()) {
            return processBreak(statement, incomingEdges);
        }
        if (statement.isContinueStmt()) {
            return processContinue(statement, incomingEdges);
        }
        return processSimpleStatement(statement, incomingEdges);
    }

    private Collection<PendingEdge> processIf(IfStmt statement, Collection<PendingEdge> incomingEdges) {
        GfcNode decision = createNode(GfcNodeTypeEnum.DECISION, labelForIf(statement), statement);
        connectPendingEdges(incomingEdges, decision.getCode());

        Collection<PendingEdge> thenExits = processBranch(
                statement.getThenStmt(),
                new PendingEdge(decision.getCode(), GfcEdgeTypeEnum.TRUE_BRANCH, "true")
        );
        Collection<PendingEdge> elseExits = statement.getElseStmt()
                .map(elseStatement -> processBranch(
                        elseStatement,
                        new PendingEdge(decision.getCode(), GfcEdgeTypeEnum.FALSE_BRANCH, "false")
                ))
                .orElse(List.of(new PendingEdge(decision.getCode(), GfcEdgeTypeEnum.FALSE_BRANCH, "false")));

        List<PendingEdge> exits = new ArrayList<>();
        exits.addAll(thenExits);
        exits.addAll(elseExits);
        return exits;
    }

    private Collection<PendingEdge> processWhile(WhileStmt statement, Collection<PendingEdge> incomingEdges) {
        return processLoop(statement, labelForWhile(statement), statement.getBody(), incomingEdges);
    }

    private Collection<PendingEdge> processFor(ForStmt statement, Collection<PendingEdge> incomingEdges) {
        return processLoop(statement, labelForFor(statement), statement.getBody(), incomingEdges);
    }

    private Collection<PendingEdge> processForEach(ForEachStmt statement, Collection<PendingEdge> incomingEdges) {
        return processLoop(statement, labelForForEach(statement), statement.getBody(), incomingEdges);
    }

    private Collection<PendingEdge> processDo(DoStmt statement, Collection<PendingEdge> incomingEdges) {
        int firstBodyNodeIndex = nodes.size();
        Collection<PendingEdge> bodyExits = processBranchBody(statement.getBody(), incomingEdges);
        String bodyEntryNodeCode = firstCreatedNodeCode(firstBodyNodeIndex);

        GfcNode loop = createNode(GfcNodeTypeEnum.LOOP, labelForDo(statement), statement);
        connectLoopBackEdges(normalExits(bodyExits), loop.getCode());
        connectContinueEdges(bodyExits, loop.getCode());

        String loopBodyTargetNodeCode = bodyEntryNodeCode == null ? loop.getCode() : bodyEntryNodeCode;
        addEdge(loop.getCode(), loopBodyTargetNodeCode, GfcEdgeTypeEnum.LOOP_BODY, "body");

        List<PendingEdge> exits = new ArrayList<>();
        exits.add(new PendingEdge(loop.getCode(), GfcEdgeTypeEnum.LOOP_EXIT, "exit"));
        exits.addAll(breakExitsAsNormal(bodyExits));
        return exits;
    }

    private Collection<PendingEdge> processLoop(Node source, String label, Statement body, Collection<PendingEdge> incomingEdges) {
        GfcNode loop = createNode(GfcNodeTypeEnum.LOOP, label, source);
        connectPendingEdges(incomingEdges, loop.getCode());

        Collection<PendingEdge> bodyExits = processBranch(
                body,
                new PendingEdge(loop.getCode(), GfcEdgeTypeEnum.LOOP_BODY, "body")
        );
        connectLoopBackEdges(normalExits(bodyExits), loop.getCode());
        connectContinueEdges(bodyExits, loop.getCode());

        List<PendingEdge> exits = new ArrayList<>();
        exits.add(new PendingEdge(loop.getCode(), GfcEdgeTypeEnum.LOOP_EXIT, "exit"));
        exits.addAll(breakExitsAsNormal(bodyExits));
        return exits;
    }

    private Collection<PendingEdge> processReturn(Statement statement, Collection<PendingEdge> incomingEdges) {
        GfcNode node = createNode(GfcNodeTypeEnum.RETURN, compactLabel(statement.toString()), statement);
        connectPendingEdges(incomingEdges, node.getCode());
        addEdge(node.getCode(), END_NODE_CODE, GfcEdgeTypeEnum.SEQUENTIAL, null);
        return List.of();
    }

    private Collection<PendingEdge> processBreak(Statement statement, Collection<PendingEdge> incomingEdges) {
        GfcNode node = createNode(GfcNodeTypeEnum.BREAK, compactLabel(statement.toString()), statement);
        connectPendingEdges(incomingEdges, node.getCode());
        return List.of(new PendingEdge(node.getCode(), GfcEdgeTypeEnum.BREAK_FLOW, "break", PendingEdgeKind.BREAK));
    }

    private Collection<PendingEdge> processContinue(Statement statement, Collection<PendingEdge> incomingEdges) {
        GfcNode node = createNode(GfcNodeTypeEnum.CONTINUE, compactLabel(statement.toString()), statement);
        connectPendingEdges(incomingEdges, node.getCode());
        return List.of(new PendingEdge(node.getCode(), GfcEdgeTypeEnum.CONTINUE_FLOW, "continue", PendingEdgeKind.CONTINUE));
    }

    private Collection<PendingEdge> processSimpleStatement(Statement statement, Collection<PendingEdge> incomingEdges) {
        GfcNode node = createNode(GfcNodeTypeEnum.STATEMENT, compactLabel(statement.toString()), statement);
        connectPendingEdges(incomingEdges, node.getCode());
        return List.of(new PendingEdge(node.getCode(), GfcEdgeTypeEnum.SEQUENTIAL, null));
    }

    private Collection<PendingEdge> processBranch(Statement statement, PendingEdge incomingEdge) {
        return processStatement(statement, List.of(incomingEdge));
    }

    private Collection<PendingEdge> processBranchBody(Statement statement, Collection<PendingEdge> incomingEdges) {
        return processStatement(statement, incomingEdges);
    }

    private String firstCreatedNodeCode(int firstNodeIndex) {
        if (nodes.size() <= firstNodeIndex) {
            return null;
        }
        return nodes.get(firstNodeIndex).getCode();
    }

    private void connectPendingEdges(Collection<PendingEdge> pendingEdges, String targetNodeCode) {
        for (PendingEdge pendingEdge : pendingEdges) {
            addEdge(pendingEdge.sourceNodeCode(), targetNodeCode, pendingEdge.type(), pendingEdge.label());
        }
    }

    private void connectPendingEdges(Collection<PendingEdge> pendingEdges,
                                     String targetNodeCode,
                                     GfcEdgeTypeEnum type,
                                     String label) {
        for (PendingEdge pendingEdge : pendingEdges) {
            addEdge(pendingEdge.sourceNodeCode(), targetNodeCode, type, label);
        }
    }

    private void connectLoopBackEdges(Collection<PendingEdge> pendingEdges, String loopNodeCode) {
        for (PendingEdge pendingEdge : pendingEdges) {
            if (pendingEdge.type() == GfcEdgeTypeEnum.BREAK_FLOW) {
                addEdge(pendingEdge.sourceNodeCode(), loopNodeCode, GfcEdgeTypeEnum.BREAK_FLOW, pendingEdge.label());
                continue;
            }
            addEdge(pendingEdge.sourceNodeCode(), loopNodeCode, GfcEdgeTypeEnum.LOOP_BACK, "loop");
        }
    }

    private void connectContinueEdges(Collection<PendingEdge> pendingEdges, String loopNodeCode) {
        for (PendingEdge pendingEdge : pendingEdges) {
            if (pendingEdge.kind() == PendingEdgeKind.CONTINUE) {
                addEdge(pendingEdge.sourceNodeCode(), loopNodeCode, GfcEdgeTypeEnum.CONTINUE_FLOW, "continue");
            }
        }
    }

    private Collection<PendingEdge> normalExits(Collection<PendingEdge> pendingEdges) {
        return pendingEdges.stream()
                .filter(pendingEdge -> pendingEdge.kind() == PendingEdgeKind.NORMAL)
                .toList();
    }

    private Collection<PendingEdge> controlFlowExits(Collection<PendingEdge> pendingEdges) {
        return pendingEdges.stream()
                .filter(pendingEdge -> pendingEdge.kind() != PendingEdgeKind.NORMAL)
                .toList();
    }

    private Collection<PendingEdge> breakExitsAsNormal(Collection<PendingEdge> pendingEdges) {
        return pendingEdges.stream()
                .filter(pendingEdge -> pendingEdge.kind() == PendingEdgeKind.BREAK)
                .map(pendingEdge -> new PendingEdge(pendingEdge.sourceNodeCode(), GfcEdgeTypeEnum.BREAK_FLOW, "break"))
                .toList();
    }

    private void addEdge(String sourceNodeCode, String targetNodeCode, GfcEdgeTypeEnum type, String label) {
        edges.add(new GfcEdge(UUID.randomUUID(), sourceNodeCode, targetNodeCode, type, label));
    }

    private GfcNode createNode(GfcNodeTypeEnum type, String label, Node source) {
        String code = "N" + nodeCounter++;
        GfcNode node = new GfcNode(UUID.randomUUID(), code, label, type, startLine(source), endLine(source));
        addNode(node);
        return node;
    }

    private Integer startLine(Node node) {
        return node.getRange().map(range -> range.begin.line).orElse(1);
    }

    private Integer endLine(Node node) {
        return node.getRange().map(range -> range.end.line).orElse(startLine(node));
    }

    private String labelForIf(IfStmt statement) {
        return compactLabel("if (" + statement.getCondition() + ")");
    }

    private String labelForWhile(WhileStmt statement) {
        return compactLabel("while (" + statement.getCondition() + ")");
    }

    private String labelForFor(ForStmt statement) {
        String initialization = statement.getInitialization().stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        String compare = statement.getCompare().map(Object::toString).orElse("");
        String update = statement.getUpdate().stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        return compactLabel("for (" + initialization + "; " + compare + "; " + update + ")");
    }

    private String labelForForEach(ForEachStmt statement) {
        return compactLabel("for (" + statement.getVariable() + " : " + statement.getIterable() + ")");
    }

    private String labelForDo(DoStmt statement) {
        return compactLabel("while (" + statement.getCondition() + ")");
    }

    private String compactLabel(String value) {
        String compacted = value.lines()
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .collect(Collectors.joining(" "));

        if (compacted.length() <= MAX_LABEL_LENGTH) {
            return compacted;
        }
        return compacted.substring(0, MAX_LABEL_LENGTH - 3) + "...";
    }
}
