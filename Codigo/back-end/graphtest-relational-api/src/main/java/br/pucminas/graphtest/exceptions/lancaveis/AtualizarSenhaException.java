package br.pucminas.graphtest.exceptions.lancaveis;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AtualizarSenhaException extends DataIntegrityViolationException {

    public AtualizarSenhaException(String mensagem) {
        super(mensagem);
    }
}
