package br.pucminas.graphtest.exceptions.lancaveis;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class TopicoNaoEncontradoException extends RuntimeException{

    public TopicoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
