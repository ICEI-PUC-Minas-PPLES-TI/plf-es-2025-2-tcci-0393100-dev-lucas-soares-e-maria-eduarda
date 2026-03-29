package br.pucminas.graphtest.adapters.inbound.dto;

import lombok.Builder;

import java.util.List;

/**
 * DTO de saida da validacao de um GCE.
 */
@Builder
public record ValidationGceDTO(
        boolean valid,
        List<ValidationGceMessageDTO> errors,
        List<ValidationGceMessageDTO> warnings
) {

    /**
     * DTO de mensagem de validacao do GCE.
     */
    public record ValidationGceMessageDTO(
            String code,
            String message
    ) {
    }
}
