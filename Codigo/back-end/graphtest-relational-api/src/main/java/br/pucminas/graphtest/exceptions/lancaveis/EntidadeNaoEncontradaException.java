package br.pucminas.graphtest.exceptions.lancaveis;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntidadeNaoEncontradaException extends EntityNotFoundException {

    public EntidadeNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
