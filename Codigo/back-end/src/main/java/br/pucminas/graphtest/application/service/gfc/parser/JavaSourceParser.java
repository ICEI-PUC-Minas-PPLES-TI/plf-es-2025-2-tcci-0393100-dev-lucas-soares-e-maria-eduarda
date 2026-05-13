package br.pucminas.graphtest.application.service.gfc.parser;

import br.pucminas.graphtest.application.exception.EmptyJavaSourceCodeException;
import br.pucminas.graphtest.application.exception.GfcMethodNotFoundException;
import br.pucminas.graphtest.application.exception.InvalidJavaSourceCodeException;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceMethodOutput;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Parser de codigo-fonte Java para a geracao inicial de GFC.
 *
 * <p>O parser aceita uma unidade Java completa ou um trecho simples de corpo de metodo.
 * Quando o codigo informado nao possui metodo com corpo, o trecho e encapsulado em uma
 * classe e metodo temporarios para viabilizar o preview sem exigir uma classe completa.</p>
 */
public class JavaSourceParser {

    private static final String INVALID_SOURCE_MESSAGE = "Codigo-fonte Java invalido para geracao do GFC.";
    private static final String EMPTY_SOURCE_MESSAGE = "O codigo-fonte e obrigatorio para gerar a pre-visualizacao do GFC.";
    private static final String NO_METHOD_MESSAGE = "Nenhum metodo ou trecho Java valido foi encontrado para geracao do GFC.";

    /**
     * Localiza o primeiro metodo com corpo no codigo-fonte informado.
     *
     * @param sourceCode codigo-fonte Java completo ou trecho de corpo de metodo
     * @return primeiro metodo com corpo encontrado
     * @throws EmptyJavaSourceCodeException quando o codigo esta vazio
     * @throws InvalidJavaSourceCodeException quando o codigo esta invalido
     * @throws GfcMethodNotFoundException quando nao ha metodo ou trecho valido
     */
    public MethodDeclaration parseFirstMethodWithBody(String sourceCode) {
        String requiredSourceCode = requireSourceCode(sourceCode);
        ParseAttempt directAttempt = tryParseFirstMethodWithBody(requiredSourceCode);
        if (directAttempt.method() != null) {
            return directAttempt.method();
        }

        ParseAttempt wrappedAttempt = tryParseFirstMethodWithBody(wrapAsTemporaryMethod(requiredSourceCode));
        if (wrappedAttempt.method() != null) {
            return wrappedAttempt.method();
        }

        if (!directAttempt.successful() && !wrappedAttempt.successful()) {
            throw new InvalidJavaSourceCodeException(INVALID_SOURCE_MESSAGE);
        }

        throw new GfcMethodNotFoundException(NO_METHOD_MESSAGE);
    }

    /**
     * Localiza o metodo com corpo correspondente a assinatura informada.
     *
     * <p>Quando a assinatura vem nula ou em branco, preserva o comportamento de selecionar
     * o primeiro metodo com corpo disponivel.</p>
     *
     * @param sourceCode codigo-fonte Java completo ou trecho de corpo de metodo
     * @param methodSignature assinatura retornada por {@link #listMethods(String)}
     * @return metodo encontrado
     * @throws GfcMethodNotFoundException quando a assinatura informada nao existe no codigo-fonte
     */
    public MethodDeclaration parseMethodBySignature(String sourceCode, String methodSignature) {
        if (methodSignature == null || methodSignature.isBlank()) {
            return parseFirstMethodWithBody(sourceCode);
        }

        String requiredSourceCode = requireSourceCode(sourceCode);
        List<MethodDeclaration> methods = parseMethods(requiredSourceCode, true);
        return methods.stream()
                .filter(method -> signatureOf(method).equals(methodSignature.trim()))
                .findFirst()
                .orElseThrow(() -> new GfcMethodNotFoundException("Metodo informado nao foi encontrado no codigo-fonte Java."));
    }

    /**
     * Lista os metodos com corpo encontrados em uma unidade Java.
     *
     * @param sourceCode codigo-fonte Java
     * @return metadados dos metodos disponiveis
     */
    public List<GfcSourceMethodOutput> listMethods(String sourceCode) {
        String requiredSourceCode = requireSourceCode(sourceCode);
        return parseCompilationUnitMethods(requiredSourceCode).stream()
                .map(method -> new GfcSourceMethodOutput(
                        method.getNameAsString(),
                        signatureOf(method),
                        startLine(method),
                        endLine(method)
                ))
                .toList();
    }

    private List<MethodDeclaration> parseCompilationUnitMethods(String sourceCode) {
        return parseMethods(sourceCode, true);
    }

    private List<MethodDeclaration> parseMethods(String sourceCode, boolean requireValidCompilationUnit) {
        ParseResult<CompilationUnit> result;
        try {
            result = new JavaParser().parse(sourceCode);
        } catch (RuntimeException exception) {
            throw new InvalidJavaSourceCodeException(INVALID_SOURCE_MESSAGE);
        }

        if (!result.isSuccessful() || result.getResult().isEmpty()) {
            if (requireValidCompilationUnit) {
                throw new InvalidJavaSourceCodeException(INVALID_SOURCE_MESSAGE);
            }
            return List.of();
        }

        return result.getResult().orElseThrow()
                .findAll(MethodDeclaration.class).stream()
                .filter(method -> method.getBody().isPresent())
                .sorted(Comparator.comparing(this::startLine))
                .toList();
    }

    private ParseAttempt tryParseFirstMethodWithBody(String sourceCode) {
        ParseResult<CompilationUnit> result;
        try {
            result = new JavaParser().parse(sourceCode);
        } catch (RuntimeException exception) {
            return new ParseAttempt(false, null);
        }

        if (!result.isSuccessful() || result.getResult().isEmpty()) {
            return new ParseAttempt(false, null);
        }

        MethodDeclaration method = result.getResult().orElseThrow()
                .findAll(MethodDeclaration.class).stream()
                .filter(candidate -> candidate.getBody().isPresent())
                .findFirst()
                .orElse(null);

        return new ParseAttempt(true, method);
    }

    private String requireSourceCode(String sourceCode) {
        if (sourceCode == null || sourceCode.isBlank()) {
            throw new EmptyJavaSourceCodeException(EMPTY_SOURCE_MESSAGE);
        }
        return sourceCode;
    }

    private String wrapAsTemporaryMethod(String sourceCode) {
        return """
                class TempGfcClass {
                    Object tempGfcMethod() {
                        %s
                    }
                }
                """.formatted(sourceCode);
    }

    public String signatureOf(MethodDeclaration method) {
        String parameters = method.getParameters().stream()
                .map(this::parameterSignature)
                .collect(Collectors.joining(", "));
        return method.getType().asString() + " " + method.getNameAsString() + "(" + parameters + ")";
    }

    private String parameterSignature(Parameter parameter) {
        return parameter.getType().asString() + " " + parameter.getNameAsString();
    }

    public Integer startLine(MethodDeclaration method) {
        return method.getRange().map(range -> range.begin.line).orElse(null);
    }

    public Integer endLine(MethodDeclaration method) {
        return method.getRange().map(range -> range.end.line).orElse(null);
    }

    private record ParseAttempt(boolean successful, MethodDeclaration method) {
    }
}
