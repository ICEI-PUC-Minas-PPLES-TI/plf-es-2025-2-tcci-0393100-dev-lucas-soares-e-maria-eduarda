package br.pucminas.graphtest.adapters.inbound.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Neo4jTestController {

    @Autowired
    private Neo4jClient neo4jClient;

    @GetMapping("/test/neo4j")
    public String testConnection() {
        return neo4jClient
                .query("RETURN 'Neo4j OK' AS message")
                .fetchAs(String.class)
                .one()
                .orElse("Erro");
    }
}
