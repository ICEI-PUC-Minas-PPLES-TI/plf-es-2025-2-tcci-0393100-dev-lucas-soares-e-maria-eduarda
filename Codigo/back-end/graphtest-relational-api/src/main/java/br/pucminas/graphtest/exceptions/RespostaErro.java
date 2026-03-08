package br.pucminas.graphtest.exceptions;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import static java.util.Objects.isNull;

@Data
public class RespostaErro {

    private final int status;
    private final String mensagem;
    private String stackTrace = "";
    private List<ErroValidacao> erros;

    /**
     * Adiciona um novo erro de validação à RespostaErro
     *
     * @param campo    campo do erro
     * @param mensagem mensagem de erro
     */
    public void addErroValidacao(String campo, String mensagem) {
        if (isNull(erros))
            this.erros = new ArrayList<>();
        this.erros.add(new ErroValidacao(campo, mensagem));
    }

    /**
     * Converte RespostaErro para JSON
     *
     * @return RespostaErro em formato JSON
     */
    public String toJson() {
        return "{\"status\": " + getStatus() + ", " +
                "\"message\": \"" + getMensagem() + "\"}";
    }
}