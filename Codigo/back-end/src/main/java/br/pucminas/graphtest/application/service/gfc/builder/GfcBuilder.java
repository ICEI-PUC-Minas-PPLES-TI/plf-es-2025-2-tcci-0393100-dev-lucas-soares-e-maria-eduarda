package br.pucminas.graphtest.application.service.gfc.builder;

import br.pucminas.graphtest.application.domain.gfc.enums.GfcEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gfc.enums.GfcNodeTypeEnum;
import br.pucminas.graphtest.application.domain.gfc.model.GfcEdge;
import br.pucminas.graphtest.application.domain.gfc.model.GfcNode;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
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
    private final Deque<ExceptionFlowContext> exceptionContexts = new ArrayDeque<>();
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
        exceptionContexts.clear();
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
        if (statement.isSwitchStmt()) {
            return processSwitch(statement.asSwitchStmt(), incomingEdges);
        }
        if (statement.isTryStmt()) {
            return processTry(statement.asTryStmt(), incomingEdges);
        }
        if (containsTernary(statement)) {
            return processTernaryStatement(statement, incomingEdges);
        }
        if (statement.isReturnStmt()) {
            return processReturn(statement, incomingEdges);
        }
        if (statement.isThrowStmt()) {
            return processThrow(statement, incomingEdges);
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

    private Collection<PendingEdge> processSwitch(SwitchStmt statement, Collection<PendingEdge> incomingEdges) {
        GfcNode switchNode = createNode(GfcNodeTypeEnum.SWITCH, labelForSwitch(statement), statement);
        connectPendingEdges(incomingEdges, switchNode.getCode());

        List<PendingEdge> breaks = new ArrayList<>();
        List<PendingEdge> propagatedControlExits = new ArrayList<>();
        Collection<PendingEdge> fallThroughExits = List.of();
        boolean hasDefault = false;
        List<String> pendingEmptyBranchLabels = new ArrayList<>();

        for (SwitchEntry entry : statement.getEntries()) {
            hasDefault = hasDefault || entry.getLabels().isEmpty();
            List<String> entryBranchLabels = branchLabelsForSwitchEntry(entry);
            if (entry.getStatements().isEmpty()) {
                pendingEmptyBranchLabels.addAll(entryBranchLabels);
                continue;
            }

            List<String> branchLabels = new ArrayList<>(pendingEmptyBranchLabels);
            branchLabels.addAll(entryBranchLabels);
            pendingEmptyBranchLabels.clear();

            GfcNode caseBlockNode = createNode(GfcNodeTypeEnum.CASE_BLOCK, labelForCaseBlock(entry), entry);
            for (String branchLabel : branchLabels) {
                connectSwitchCaseBranch(switchNode, branchLabel, caseBlockNode);
            }
            connectPendingEdges(normalExits(fallThroughExits), caseBlockNode.getCode());

            Collection<PendingEdge> caseExits = processCaseBlock(entry, caseBlockNode);
            breaks.addAll(breakExits(caseExits));
            propagatedControlExits.addAll(nonBreakControlFlowExits(caseExits));
            fallThroughExits = normalExits(caseExits);
        }

        List<PendingEdge> exits = new ArrayList<>();
        if (!hasDefault) {
            exits.add(new PendingEdge(switchNode.getCode(), GfcEdgeTypeEnum.SEQUENTIAL, null));
        }
        exits.addAll(normalExits(fallThroughExits));
        exits.addAll(breakExitsAsNormal(breaks));
        exits.addAll(propagatedControlExits);
        return exits;
    }

    private void connectSwitchCaseBranch(GfcNode switchNode, String branchLabel, GfcNode caseBlockNode) {
        addEdge(
                switchNode.getCode(),
                caseBlockNode.getCode(),
                branchLabel.equals("default") ? GfcEdgeTypeEnum.DEFAULT_BRANCH : GfcEdgeTypeEnum.CASE_BRANCH,
                branchLabel
        );
    }

    private Collection<PendingEdge> processCaseBlock(SwitchEntry entry, GfcNode caseBlockNode) {
        if (isSimpleCaseBlock(entry)) {
            if (hasTerminalBreak(entry)) {
                return List.of(new PendingEdge(caseBlockNode.getCode(), GfcEdgeTypeEnum.BREAK_FLOW, "break", PendingEdgeKind.BREAK));
            }
            return List.of(new PendingEdge(caseBlockNode.getCode(), GfcEdgeTypeEnum.SEQUENTIAL, null));
        }

        return processStatements(
                entry.getStatements(),
                List.of(new PendingEdge(caseBlockNode.getCode(), GfcEdgeTypeEnum.SEQUENTIAL, null))
        );
    }

    private boolean isSimpleCaseBlock(SwitchEntry entry) {
        return entry.getStatements().stream()
                .allMatch(statement -> statement.isExpressionStmt() || statement.isBreakStmt());
    }

    private boolean hasTerminalBreak(SwitchEntry entry) {
        List<Statement> statements = entry.getStatements();
        return !statements.isEmpty() && statements.getLast().isBreakStmt();
    }

    private Collection<PendingEdge> processTry(TryStmt statement, Collection<PendingEdge> incomingEdges) {
        GfcNode tryNode = createNode(GfcNodeTypeEnum.TRY, labelForTry(), statement);
        connectPendingEdges(incomingEdges, tryNode.getCode());

        List<GfcNode> catchNodes = statement.getCatchClauses().stream()
                .map(catchClause -> createNode(GfcNodeTypeEnum.CATCH, labelForCatch(catchClause), catchClause))
                .toList();
        for (GfcNode catchNode : catchNodes) {
            addEdge(tryNode.getCode(), catchNode.getCode(), GfcEdgeTypeEnum.CATCH_BRANCH, catchNode.getLabel());
        }

        GfcNode finallyNode = statement.getFinallyBlock()
                .map(finallyBlock -> createNode(GfcNodeTypeEnum.FINALLY, labelForFinally(), finallyBlock))
                .orElse(null);

        ExceptionFlowContext context = new ExceptionFlowContext(
                catchNodes.isEmpty() ? null : catchNodes.getFirst().getCode(),
                finallyNode == null ? null : finallyNode.getCode()
        );
        exceptionContexts.push(context);
        Collection<PendingEdge> tryExits = processStatements(
                statement.getTryBlock().getStatements(),
                List.of(new PendingEdge(tryNode.getCode(), GfcEdgeTypeEnum.TRY_BRANCH, "try"))
        );
        exceptionContexts.pop();

        List<PendingEdge> catchExits = new ArrayList<>();
        for (int index = 0; index < statement.getCatchClauses().size(); index++) {
            GfcNode catchNode = catchNodes.get(index);
            ExceptionFlowContext catchContext = new ExceptionFlowContext(
                    null,
                    finallyNode == null ? null : finallyNode.getCode()
            );
            exceptionContexts.push(catchContext);
            catchExits.addAll(processStatements(
                    statement.getCatchClauses().get(index).getBody().getStatements(),
                    List.of(new PendingEdge(catchNode.getCode(), GfcEdgeTypeEnum.SEQUENTIAL, null))
            ));
            exceptionContexts.pop();
            if (catchContext.hasExceptionalFlowToFinally()) {
                context.markExceptionalFlowToFinally();
            }
        }

        List<PendingEdge> exits = new ArrayList<>();
        if (finallyNode == null) {
            exits.addAll(tryExits);
            exits.addAll(catchExits);
            return exits;
        }

        List<PendingEdge> normalFinallyIncoming = new ArrayList<>();
        normalFinallyIncoming.addAll(normalExits(tryExits));
        normalFinallyIncoming.addAll(normalExits(catchExits));
        connectPendingEdges(normalFinallyIncoming, finallyNode.getCode(), GfcEdgeTypeEnum.FINALLY_BRANCH, "finally");

        if (!normalFinallyIncoming.isEmpty()) {
            Collection<PendingEdge> finallyExits = processStatements(
                    statement.getFinallyBlock().orElseThrow().getStatements(),
                    List.of(new PendingEdge(finallyNode.getCode(), GfcEdgeTypeEnum.SEQUENTIAL, null))
            );
            if (context.hasExceptionalFlowToFinally()) {
                connectPendingEdges(normalExits(finallyExits), END_NODE_CODE, GfcEdgeTypeEnum.SEQUENTIAL, null);
            }
            exits.addAll(finallyExits);
        } else if (context.hasExceptionalFlowToFinally()) {
            processExceptionalFinally(statement, finallyNode.getCode());
        }
        exits.addAll(controlFlowExits(tryExits));
        exits.addAll(controlFlowExits(catchExits));
        return exits;
    }

    private void processExceptionalFinally(TryStmt statement, String exceptionalFinallyNodeCode) {
        Collection<PendingEdge> exceptionalFinallyExits = processStatements(
                statement.getFinallyBlock().orElseThrow().getStatements(),
                List.of(new PendingEdge(exceptionalFinallyNodeCode, GfcEdgeTypeEnum.SEQUENTIAL, null))
        );
        connectPendingEdges(normalExits(exceptionalFinallyExits), END_NODE_CODE, GfcEdgeTypeEnum.SEQUENTIAL, null);
    }

    private Collection<PendingEdge> processReturn(Statement statement, Collection<PendingEdge> incomingEdges) {
        GfcNode node = createNode(GfcNodeTypeEnum.RETURN, labelWithoutComments(statement), statement);
        connectPendingEdges(incomingEdges, node.getCode());
        addEdge(node.getCode(), END_NODE_CODE, GfcEdgeTypeEnum.SEQUENTIAL, null);
        return List.of();
    }

    private Collection<PendingEdge> processTernaryStatement(Statement statement, Collection<PendingEdge> incomingEdges) {
        ConditionalExpr ternary = firstTernary(statement);
        if (statement.isReturnStmt()) {
            return processTernaryExpression(
                    ternary,
                    incomingEdges,
                    branchExpression -> "return " + branchExpression + ";",
                    true
            );
        }

        String statementLabel = codeWithoutComments(statement);
        String ternaryLabel = codeWithoutComments(ternary);
        return processTernaryExpression(
                ternary,
                incomingEdges,
                branchExpression -> replaceFirstExpression(statementLabel, ternaryLabel, branchExpression),
                false
        );
    }

    private Collection<PendingEdge> processTernaryExpression(ConditionalExpr expression,
                                                             Collection<PendingEdge> incomingEdges,
                                                             Function<String, String> branchLabelFactory,
                                                             boolean terminalReturn) {
        GfcNode ternaryNode = createNode(GfcNodeTypeEnum.TERNARY, labelWithoutComments(expression.getCondition()), expression);
        connectPendingEdges(incomingEdges, ternaryNode.getCode());

        List<PendingEdge> exits = new ArrayList<>();
        exits.addAll(processTernaryBranch(
                expression.getThenExpr(),
                new PendingEdge(ternaryNode.getCode(), GfcEdgeTypeEnum.TRUE_BRANCH, "true"),
                branchLabelFactory,
                terminalReturn
        ));
        exits.addAll(processTernaryBranch(
                expression.getElseExpr(),
                new PendingEdge(ternaryNode.getCode(), GfcEdgeTypeEnum.FALSE_BRANCH, "false"),
                branchLabelFactory,
                terminalReturn
        ));
        return exits;
    }

    private Collection<PendingEdge> processTernaryBranch(Expression branchExpression,
                                                         PendingEdge incomingEdge,
                                                         Function<String, String> branchLabelFactory,
                                                         boolean terminalReturn) {
        if (branchExpression.isConditionalExpr()) {
            return processTernaryExpression(
                    branchExpression.asConditionalExpr(),
                    List.of(incomingEdge),
                    branchLabelFactory,
                    terminalReturn
            );
        }

        GfcNodeTypeEnum nodeType = terminalReturn ? GfcNodeTypeEnum.RETURN : GfcNodeTypeEnum.STATEMENT;
        GfcNode branchNode = createNode(nodeType, compactLabel(branchLabelFactory.apply(codeWithoutComments(branchExpression))), branchExpression);
        connectPendingEdges(List.of(incomingEdge), branchNode.getCode());
        if (terminalReturn) {
            addEdge(branchNode.getCode(), END_NODE_CODE, GfcEdgeTypeEnum.SEQUENTIAL, null);
            return List.of();
        }
        return List.of(new PendingEdge(branchNode.getCode(), GfcEdgeTypeEnum.SEQUENTIAL, null));
    }

    private Collection<PendingEdge> processThrow(Statement statement, Collection<PendingEdge> incomingEdges) {
        GfcNode node = createNode(GfcNodeTypeEnum.THROW, labelWithoutComments(statement), statement);
        connectPendingEdges(incomingEdges, node.getCode());
        addEdge(node.getCode(), targetForThrow(), GfcEdgeTypeEnum.THROW_FLOW, "throw");
        return List.of();
    }

    private String targetForThrow() {
        if (exceptionContexts.isEmpty()) {
            return END_NODE_CODE;
        }

        ExceptionFlowContext context = exceptionContexts.peek();
        if (context.hasCatch()) {
            return context.catchNodeCode();
        }
        if (context.hasFinally()) {
            context.markExceptionalFlowToFinally();
            return context.finallyNodeCode();
        }
        return END_NODE_CODE;
    }

    private Collection<PendingEdge> processBreak(Statement statement, Collection<PendingEdge> incomingEdges) {
        GfcNode node = createNode(GfcNodeTypeEnum.BREAK, labelWithoutComments(statement), statement);
        connectPendingEdges(incomingEdges, node.getCode());
        return List.of(new PendingEdge(node.getCode(), GfcEdgeTypeEnum.BREAK_FLOW, "break", PendingEdgeKind.BREAK));
    }

    private Collection<PendingEdge> processContinue(Statement statement, Collection<PendingEdge> incomingEdges) {
        GfcNode node = createNode(GfcNodeTypeEnum.CONTINUE, labelWithoutComments(statement), statement);
        connectPendingEdges(incomingEdges, node.getCode());
        return List.of(new PendingEdge(node.getCode(), GfcEdgeTypeEnum.CONTINUE_FLOW, "continue", PendingEdgeKind.CONTINUE));
    }

    private Collection<PendingEdge> processSimpleStatement(Statement statement, Collection<PendingEdge> incomingEdges) {
        GfcNode node = createNode(GfcNodeTypeEnum.STATEMENT, labelWithoutComments(statement), statement);
        connectPendingEdges(incomingEdges, node.getCode());
        return List.of(new PendingEdge(node.getCode(), GfcEdgeTypeEnum.SEQUENTIAL, null));
    }

    private boolean containsTernary(Node node) {
        return node.findFirst(ConditionalExpr.class).isPresent();
    }

    private ConditionalExpr firstTernary(Node node) {
        return node.findFirst(ConditionalExpr.class).orElseThrow();
    }

    private String replaceFirstExpression(String source, String targetExpression, String replacementExpression) {
        int index = source.indexOf(targetExpression);
        if (index < 0) {
            return source;
        }
        return source.substring(0, index)
                + replacementExpression
                + source.substring(index + targetExpression.length());
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

    private Collection<PendingEdge> nonBreakControlFlowExits(Collection<PendingEdge> pendingEdges) {
        return pendingEdges.stream()
                .filter(pendingEdge -> pendingEdge.kind() != PendingEdgeKind.NORMAL)
                .filter(pendingEdge -> pendingEdge.kind() != PendingEdgeKind.BREAK)
                .toList();
    }

    private Collection<PendingEdge> breakExits(Collection<PendingEdge> pendingEdges) {
        return pendingEdges.stream()
                .filter(pendingEdge -> pendingEdge.kind() == PendingEdgeKind.BREAK)
                .toList();
    }

    private Collection<PendingEdge> breakExitsAsNormal(Collection<PendingEdge> pendingEdges) {
        return pendingEdges.stream()
                .filter(pendingEdge -> pendingEdge.kind() == PendingEdgeKind.BREAK)
                .map(pendingEdge -> new PendingEdge(pendingEdge.sourceNodeCode(), GfcEdgeTypeEnum.BREAK_FLOW, "break"))
                .toList();
    }

    private void addEdge(String sourceNodeCode, String targetNodeCode, GfcEdgeTypeEnum type, String label) {
        if (edges.stream().anyMatch(edge -> edge.getSourceNodeCode().equals(sourceNodeCode)
                && edge.getTargetNodeCode().equals(targetNodeCode)
                && edge.getType() == type
                && ((edge.getLabel() == null && label == null) || (edge.getLabel() != null && edge.getLabel().equals(label))))) {
            return;
        }
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
        return compactLabel("if (" + codeWithoutComments(statement.getCondition()) + ")");
    }

    private String labelForWhile(WhileStmt statement) {
        return compactLabel("while (" + codeWithoutComments(statement.getCondition()) + ")");
    }

    private String labelForFor(ForStmt statement) {
        String initialization = statement.getInitialization().stream()
                .map(this::codeWithoutComments)
                .collect(Collectors.joining(", "));
        String compare = statement.getCompare().map(this::codeWithoutComments).orElse("");
        String update = statement.getUpdate().stream()
                .map(this::codeWithoutComments)
                .collect(Collectors.joining(", "));
        return compactLabel("for (" + initialization + "; " + compare + "; " + update + ")");
    }

    private String labelForForEach(ForEachStmt statement) {
        return compactLabel("for (" + codeWithoutComments(statement.getVariable()) + " : " + codeWithoutComments(statement.getIterable()) + ")");
    }

    private String labelForDo(DoStmt statement) {
        return compactLabel("while (" + codeWithoutComments(statement.getCondition()) + ")");
    }

    private String labelForSwitch(SwitchStmt statement) {
        return compactLabel("switch (" + codeWithoutComments(statement.getSelector()) + ")");
    }

    private String labelForSwitchEntry(SwitchEntry entry) {
        if (entry.getLabels().isEmpty()) {
            return "default";
        }
        return compactLabel("case " + entry.getLabels().stream()
                .map(this::codeWithoutComments)
                .collect(Collectors.joining(", ")));
    }

    private List<String> branchLabelsForSwitchEntry(SwitchEntry entry) {
        if (entry.getLabels().isEmpty()) {
            return List.of("default");
        }
        return entry.getLabels().stream()
                .map(label -> compactLabel("case " + codeWithoutComments(label)))
                .toList();
    }

    private String labelForCaseBlock(SwitchEntry entry) {
        return compactLabel(entry.getStatements().stream()
                .map(this::codeWithoutComments)
                .collect(Collectors.joining(System.lineSeparator())));
    }

    private String labelForTry() {
        return "try";
    }

    private String labelForCatch(com.github.javaparser.ast.stmt.CatchClause catchClause) {
        return compactLabel("catch (" + codeWithoutComments(catchClause.getParameter()) + ")");
    }

    private String labelForFinally() {
        return "finally";
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

    private String labelWithoutComments(Node node) {
        return compactLabel(codeWithoutComments(node));
    }

    private String codeWithoutComments(Node node) {
        Node clone = node.clone();
        clone.getAllContainedComments().forEach(Comment::remove);
        clone.removeComment();
        return clone.toString();
    }
}
