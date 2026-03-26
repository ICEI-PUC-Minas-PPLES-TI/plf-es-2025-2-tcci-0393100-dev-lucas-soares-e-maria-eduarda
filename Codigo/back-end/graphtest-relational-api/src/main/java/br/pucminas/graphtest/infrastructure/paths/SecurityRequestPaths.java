package br.pucminas.graphtest.infrastructure.paths;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class SecurityRequestPaths {

    public static final String[] CAMINHOS_PUBLICOS = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    public static final String[] CAMINHOS_PUBLICOS_POST = {
            "/login",
            ApiRequestPaths.USUARIO
    };

}
