package br.pucminas.graphtest.adapters.inbound.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ConstrutorRespostaJsonException extends RuntimeException {

    public ConstrutorRespostaJsonException(String mensagem) {

        super(mensagem);
    }
}
