-- =====================================================================
-- SCHEMA: GraphTest
-- =====================================================================

CREATE DATABASE IF NOT EXISTS graphtest
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE graphtest;

-- =====================================================================
-- USUARIO
-- =====================================================================

CREATE TABLE usuario (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  nome VARCHAR(120) NOT NULL,
  email VARCHAR(160) NOT NULL UNIQUE,
  senha_hash VARCHAR(255) NOT NULL
);

-- =====================================================================
-- PROJETO
-- =====================================================================

CREATE TABLE projeto (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  usuario_id BIGINT NOT NULL,
  nome VARCHAR(150) NOT NULL,
  descricao TEXT,
  CONSTRAINT fk_proj_user
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
      ON DELETE CASCADE ON UPDATE CASCADE
);

-- =====================================================================
-- ARTEFATO DE CÓDIGO
-- =====================================================================

-- ArquivoJava (subclasse)
CREATE TABLE arquivo_java (
  id BIGINT PRIMARY KEY,
  projeto_id BIGINT NOT NULL,
  nome VARCHAR(150) NOT NULL,
  conteudo_codigo MEDIUMTEXT NOT NULL,
   CONSTRAINT fk_arquivo_java_projeto
    FOREIGN KEY (projeto_id) REFERENCES projeto(id)
      ON DELETE CASCADE ON UPDATE CASCADE
);

-- =====================================================================
-- AST
-- =====================================================================

CREATE TABLE ast (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  arquivo_java_id BIGINT NOT NULL,
  linguagem VARCHAR(60) NOT NULL,
  estrutura_serializada LONGTEXT NOT NULL,
  fonte_hash VARCHAR(64) NOT NULL,
  CONSTRAINT fk_ast_arq
    FOREIGN KEY (arquivo_java_id) REFERENCES arquivo_java(id)
      ON DELETE CASCADE ON UPDATE CASCADE
);

-- =====================================================================
-- CLASSES E MÉTODOS
-- =====================================================================

CREATE TABLE classe_java (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  arquivo_java_id BIGINT NOT NULL,
  nome VARCHAR(180) NOT NULL,
  nome_qualificado VARCHAR(255),
  CONSTRAINT fk_classe_arq
    FOREIGN KEY (arquivo_java_id) REFERENCES arquivo_java(id)
      ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE metodo_java (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  classe_id BIGINT NOT NULL,
  nome VARCHAR(180) NOT NULL,
  modificador VARCHAR(40),
  tipo_retorno VARCHAR(120),
  linha_inicio INT,
  linha_fim INT,
  assinatura VARCHAR(400),
  CONSTRAINT fk_met_classe
    FOREIGN KEY (classe_id) REFERENCES classe_java(id)
      ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE parametro_metodo (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  metodo_id BIGINT NOT NULL,
  nome VARCHAR(120) NOT NULL,
  tipo VARCHAR(120) NOT NULL,
  posicao INT NOT NULL,
  CONSTRAINT fk_param_method
    FOREIGN KEY (metodo_id) REFERENCES metodo_java(id)
      ON DELETE CASCADE ON UPDATE CASCADE
);

-- =====================================================================
-- ASSINATURA DE TESTE
-- =====================================================================

CREATE TABLE assinatura_teste (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  projeto_id BIGINT NOT NULL,
  nome_metodo_teste VARCHAR(160) NOT NULL,
  framework_test VARCHAR(160) NOT NULL,
  conteudo_codigo VARCHAR(200) NOT NULL,
  tipo ENUM('Teste Estrutural','Teste Funcional') NOT NULL,
  descricao VARCHAR(200),

  CONSTRAINT fk_ass_proj
    FOREIGN KEY (projeto_id) REFERENCES projeto(id)
      ON DELETE CASCADE ON UPDATE CASCADE
);

-- =====================================================================
-- GRAFO DE FLUXO DE CONTROLE (GFC)
-- =====================================================================

CREATE TABLE gfc (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  projeto_id BIGINT NOT NULL,
  assinatura_teste_id BIGINT NOT NULL,
  apontador BIGINT NOT NULL,

  CONSTRAINT fk_gfc_proj
    FOREIGN KEY (projeto_id) REFERENCES projeto(id)
      ON DELETE CASCADE ON UPDATE CASCADE,

  CONSTRAINT fk_gfc_ass
    FOREIGN KEY (assinatura_teste_id) REFERENCES assinatura_teste(id)
      ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE gfc_no (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  gfc_id BIGINT NOT NULL,
  tipo ENUM('Assinatura','Comando','Decisao','Laco','Retorno') NOT NULL,
  numero_linha INT,
  descricao VARCHAR(255),
  CONSTRAINT fk_gfcno_gfc
    FOREIGN KEY (gfc_id) REFERENCES gfc(id)
      ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE gfc_aresta (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  gfc_id BIGINT NOT NULL,
  no_origem_id BIGINT NOT NULL,
  no_destino_id BIGINT NOT NULL,
  tipo ENUM('Sequencia','Condicao','Repeticao') NOT NULL,
  valor_condicional ENUM('Verdadeiro','Falso') NULL,

  CONSTRAINT fk_gfca_gfc
    FOREIGN KEY (gfc_id) REFERENCES gfc(id)
      ON DELETE CASCADE ON UPDATE CASCADE,

  CONSTRAINT fk_gfca_origem
    FOREIGN KEY (no_origem_id) REFERENCES gfc_no(id)
      ON DELETE CASCADE ON UPDATE CASCADE,

  CONSTRAINT fk_gfca_destino
    FOREIGN KEY (no_destino_id) REFERENCES gfc_no(id)
      ON DELETE CASCADE ON UPDATE CASCADE
);

-- =====================================================================
-- GRAFO DE CAUSA E EFEITO (GCE)
-- =====================================================================

CREATE TABLE gce (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  projeto_id BIGINT NOT NULL,
  assinatura_teste_id BIGINT NOT NULL,
  rotulo VARCHAR(160) NOT NULL,
  apontador INT NOT NULL,
  gce_status ENUM('Normal','Inconsistente') NOT NULL,

  CONSTRAINT fk_gce_proj
    FOREIGN KEY (projeto_id) REFERENCES projeto(id)
      ON DELETE CASCADE ON UPDATE CASCADE,

  CONSTRAINT fk_gce_ass
    FOREIGN KEY (assinatura_teste_id) REFERENCES assinatura_teste(id)
      ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE gce_no (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  gce_id BIGINT NOT NULL,
  rotulo VARCHAR(180),
  tipo ENUM('Causa','Efeito','Operador Lógico') NOT NULL,
  tipo_operador ENUM('AND','OR','NOT'),

  CONSTRAINT fk_gce_no_gce
    FOREIGN KEY (gce_id) REFERENCES gce(id)
      ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE gce_aresta (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  gce_id BIGINT NOT NULL,
  no_origem_id BIGINT NOT NULL,
  no_destino_id BIGINT NOT NULL,
  tipo ENUM('Normal','Negado','Identidade') NOT NULL,
  rotulo VARCHAR(120),

  CONSTRAINT fk_gcea_gce
    FOREIGN KEY (gce_id) REFERENCES gce(id)
      ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_gcea_origem
    FOREIGN KEY (no_origem_id) REFERENCES gce_no(id)
      ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_gcea_destino
    FOREIGN KEY (no_destino_id) REFERENCES gce_no(id)
      ON DELETE CASCADE ON UPDATE CASCADE
);

-- =====================================================================
-- RESTRIÇÃO DO GCE
-- =====================================================================

CREATE TABLE restricao (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  gce_id BIGINT NOT NULL,
  tipo ENUM('E','I','R','O','M') NOT NULL,
  expressao VARCHAR(255),

  CONSTRAINT fk_rest_gce
    FOREIGN KEY (gce_id) REFERENCES gce(id)
      ON DELETE CASCADE ON UPDATE CASCADE
);

-- =====================================================================
-- TABELA DE DECISÃO
-- =====================================================================

CREATE TABLE tabela_decisao (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  gce_id BIGINT NOT NULL,
  politica ENUM('Qualquer','Primeira','Prioridade') NOT NULL,
  descricao VARCHAR(255),

  CONSTRAINT fk_td_gce
    FOREIGN KEY (gce_id) REFERENCES gce(id)
      ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE condicao (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  tabela_id BIGINT NOT NULL,
  texto VARCHAR(255) NOT NULL,
  ordem INT NOT NULL,

  CONSTRAINT fk_cond_td
    FOREIGN KEY (tabela_id) REFERENCES tabela_decisao(id)
      ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE acao (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  tabela_id BIGINT NOT NULL,
  texto VARCHAR(255) NOT NULL,
  ordem INT NOT NULL,
  unidade VARCHAR(60),

  CONSTRAINT fk_acao_td
    FOREIGN KEY (tabela_id) REFERENCES tabela_decisao(id)
      ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE regra (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  tabela_id BIGINT NOT NULL,
  indice INT NOT NULL,
  prioridade INT NOT NULL,
  descricao VARCHAR(255),

  CONSTRAINT fk_regra_td
    FOREIGN KEY (tabela_id) REFERENCES tabela_decisao(id)
      ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE celula_condicao (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  regra_id BIGINT NOT NULL,
  condicao_id BIGINT NOT NULL,
  valor ENUM('S','N') NOT NULL,

  CONSTRAINT fk_cc_regra FOREIGN KEY (regra_id) REFERENCES regra(id),
  CONSTRAINT fk_cc_cond  FOREIGN KEY (condicao_id) REFERENCES condicao(id),

  UNIQUE KEY uq_cc (regra_id, condicao_id)
);

CREATE TABLE celula_acao (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  regra_id BIGINT NOT NULL,
  acao_id BIGINT NOT NULL,
  valor VARCHAR(255) NOT NULL,

  CONSTRAINT fk_ca_regra FOREIGN KEY (regra_id) REFERENCES regra(id),
  CONSTRAINT fk_ca_acao  FOREIGN KEY (acao_id) REFERENCES acao(id),

  UNIQUE KEY uq_ca (regra_id, acao_id)
);
