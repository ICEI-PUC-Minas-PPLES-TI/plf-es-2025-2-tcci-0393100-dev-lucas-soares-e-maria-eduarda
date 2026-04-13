package br.pucminas.graphtest.adapters.inbound.exception;

import br.pucminas.graphtest.adapters.inbound.error.ErrorResponse;
import br.pucminas.graphtest.application.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import java.io.IOException;
import static br.pucminas.graphtest.adapters.inbound.util.ValidatorErrorConstantsUtil.MSG_ERRO_USUARIO_SENHA;
import static br.pucminas.graphtest.adapters.inbound.util.ValidatorErrorConstantsUtil.MSG_ERRO_VALIDACAO;
import static br.pucminas.graphtest.adapters.inbound.util.SecurityHttpConstantsUtil.CONTENT_TYPE;
import static br.pucminas.graphtest.shared.LogTopicsUtil.INTERCEPTADOR_EXCECOES;


/**
 * Componente responsável por capturar exceções lançadas durante o processamento de uma requisição HTTP e
 * convertê-las em respostas HTTP padronizadas.
 */
@Slf4j(topic = INTERCEPTADOR_EXCECOES)
@RestControllerAdvice
public class GlobalExceptionHandler implements AuthenticationFailureHandler {

    @Value("${spring.web.error.include-exception}")
    private boolean imprimirStackTrace;


    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        @NotNull HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        int status = HttpStatus.UNAUTHORIZED.value();

        ErrorResponse erro = new ErrorResponse(status, MSG_ERRO_USUARIO_SENHA);

        response.setStatus(status);
        response.setContentType(CONTENT_TYPE);
        response.getWriter().append(erro.toJson());
    }

    /**
     * Validação de DTO
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException e) {

        ErrorResponse erro = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                MSG_ERRO_VALIDACAO
        );

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            erro.addErroValidacao(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );
        }

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(erro);
    }

    /**
     * Entidade não encontrada
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFound(NotFoundException e) {

        log.error("[ERRO] NotFoundException: {}", e.getMessage());

        return construirMsgErro(e, HttpStatus.NOT_FOUND);
    }

    /**
     * Conflitos de regra de negócio
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Object> handleConflict(ConflictException e) {

        log.error("[ERRO] ConflictException: {}", e.getMessage());

        return construirMsgErro(e, HttpStatus.CONFLICT);
    }

    /**
     * Erro de autorização
     */
    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<Object> handleAuthorization(AuthorizationException e) {

        log.error("[ERRO] AuthorizationException: {}", e.getMessage());

        return construirMsgErro(e, HttpStatus.FORBIDDEN);
    }

    /**
     * Erros de conversão
     */
    @ExceptionHandler({
            IllegalArgumentException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<Object> handleBadRequest(Exception e) {

        log.error("[ERRO] {}: {}", e.getClass().getSimpleName(), e.getMessage());

        return construirMsgErro(e, HttpStatus.BAD_REQUEST);
    }

    /**
     * Falhas de integridade do banco
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDatabase(DataIntegrityViolationException e) {

        log.error("[ERRO] DataIntegrityViolationException: {}", e.getMessage());

        return construirMsgErro(e, HttpStatus.CONFLICT);
    }

    /**
     * Falha interna ao construir resposta JSON
     */
    @ExceptionHandler(JsonResponseBuilderException.class)
    public ResponseEntity<Object> handleJsonBuilder(JsonResponseBuilderException e) {

        log.error("[ERRO] JsonResponseBuilderException: {}", e.getMessage());

        return construirMsgErro(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Endpoint inexistente
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Object> handleNoResource(NoResourceFoundException e) {

        log.error("[ERRO] Recurso não encontrado: {}", e.getMessage());

        return construirMsgErro(e, HttpStatus.NOT_FOUND);
    }

    /**
     * Handler genérico
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneric(Exception e) {

        log.error("[ERRO] Exception: {}", e.getMessage(), e);

        return construirMsgErro(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Constrói resposta de erro
     */
    private ResponseEntity<Object> construirMsgErro(Exception e, HttpStatus status) {

        ErrorResponse erro = new ErrorResponse(status.value(), e.getMessage());

        if (imprimirStackTrace) {
            erro.setStackTrace(ExceptionUtils.getStackTrace(e));
        }

        return ResponseEntity
                .status(status)
                .body(erro);
    }

}
