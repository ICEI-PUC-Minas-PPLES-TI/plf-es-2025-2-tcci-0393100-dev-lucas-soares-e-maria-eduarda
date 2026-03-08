package br.pucminas.graphtest.exceptions.lancaveis;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EntidadesRelacionadasException extends RuntimeException{

    public EntidadesRelacionadasException(String message) {
        super(message);
    }
}