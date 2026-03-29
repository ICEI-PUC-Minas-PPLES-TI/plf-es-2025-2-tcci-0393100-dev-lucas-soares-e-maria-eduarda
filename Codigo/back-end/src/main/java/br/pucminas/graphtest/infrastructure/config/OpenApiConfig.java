package br.pucminas.graphtest.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "GraphTest API",
                version = "1.0.0",
                description = "API para gerenciamento e análise de grafos de teste de software",
                contact = @Contact(
                        name = "Lucas S. & Maria Eduarda"
                )
        )
)
public class OpenApiConfig {

}
