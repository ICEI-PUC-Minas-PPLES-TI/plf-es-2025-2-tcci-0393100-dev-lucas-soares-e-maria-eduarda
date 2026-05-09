package br.pucminas.graphtest.application.domain.gfc.model;

import br.pucminas.graphtest.application.domain.gfc.enums.GfcNodeTypeEnum;
import br.pucminas.graphtest.application.domain.shared.model.BaseEntity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa um vertice do Grafo de Fluxo de Controle.
 */
public class GfcNode extends BaseEntity {

    private String code;
    private String label;
    private GfcNodeTypeEnum type;
    private Integer startLine;
    private Integer endLine;

    public GfcNode(UUID id, String code, String label, GfcNodeTypeEnum type, Integer startLine, Integer endLine) {
        this(id, code, label, type, startLine, endLine, null, null);
    }

    public GfcNode(UUID id,
                   String code,
                   String label,
                   GfcNodeTypeEnum type,
                   Integer startLine,
                   Integer endLine,
                   LocalDateTime createdAt,
                   LocalDateTime updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.code = requireText(code, "code");
        this.label = requireText(label, "label");
        this.type = Objects.requireNonNull(type, "O tipo do no e obrigatorio.");
        this.startLine = startLine;
        this.endLine = endLine;
        validateLines();
    }

    public static GfcNode start(UUID id, String code, String label) {
        return new GfcNode(id, code, label, GfcNodeTypeEnum.START, null, null);
    }

    public static GfcNode start(UUID id, String code, String label, Integer startLine, Integer endLine) {
        return new GfcNode(id, code, label, GfcNodeTypeEnum.START, startLine, endLine);
    }

    public static GfcNode end(UUID id, String code, String label) {
        return new GfcNode(id, code, label, GfcNodeTypeEnum.END, null, null);
    }

    public static GfcNode end(UUID id, String code, String label, Integer startLine, Integer endLine) {
        return new GfcNode(id, code, label, GfcNodeTypeEnum.END, startLine, endLine);
    }

    public static GfcNode statement(UUID id, String code, String label, Integer startLine, Integer endLine) {
        return new GfcNode(id, code, label, GfcNodeTypeEnum.STATEMENT, startLine, endLine);
    }

    public static GfcNode decision(UUID id, String code, String label, Integer startLine, Integer endLine) {
        return new GfcNode(id, code, label, GfcNodeTypeEnum.DECISION, startLine, endLine);
    }

    public static GfcNode returnNode(UUID id, String code, String label, Integer startLine, Integer endLine) {
        return new GfcNode(id, code, label, GfcNodeTypeEnum.RETURN, startLine, endLine);
    }

    private String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(displayFieldName(field) + " e obrigatorio.");
        }
        return value.trim();
    }

    private Integer requirePositiveLine(Integer value, String field) {
        if (value == null) {
            throw new IllegalArgumentException(displayFieldName(field) + " e obrigatoria para nos de codigo-fonte.");
        }
        if (value < 1) {
            throw new IllegalArgumentException(displayFieldName(field) + " deve ser maior que zero.");
        }
        return value;
    }

    private String displayFieldName(String field) {
        return switch (field) {
            case "code" -> "O codigo do no";
            case "label" -> "O rotulo do no";
            case "startLine" -> "A linha inicial";
            case "endLine" -> "A linha final";
            default -> field;
        };
    }

    private void validateLines() {
        if (isStart() || isEnd()) {
            validateOptionalArtificialNodeLines();
            return;
        }

        this.startLine = requirePositiveLine(startLine, "startLine");
        this.endLine = requirePositiveLine(endLine, "endLine");
        validateLineRange();
    }

    private void validateOptionalArtificialNodeLines() {
        if (startLine != null) {
            this.startLine = requirePositiveLine(startLine, "startLine");
        }
        if (endLine != null) {
            this.endLine = requirePositiveLine(endLine, "endLine");
        }
        if (startLine != null && endLine != null) {
            validateLineRange();
        }
    }

    private void validateLineRange() {
        if (endLine < startLine) {
            throw new IllegalArgumentException("A linha final nao pode ser menor que a linha inicial.");
        }
    }

    public boolean isStart() {
        return type == GfcNodeTypeEnum.START;
    }

    public boolean isEnd() {
        return type == GfcNodeTypeEnum.END;
    }

    public boolean isDecision() {
        return type == GfcNodeTypeEnum.DECISION;
    }

    public boolean isReturn() {
        return type == GfcNodeTypeEnum.RETURN;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public GfcNodeTypeEnum getType() {
        return type;
    }

    public Integer getStartLine() {
        return startLine;
    }

    public Integer getEndLine() {
        return endLine;
    }
}
