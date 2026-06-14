[![Open in Codespaces](https://classroom.github.com/assets/launch-codespace-2972f46106e565e64193e422d61a12cf1da4916b45550586e14ef0a7c637dd04.svg)](https://classroom.github.com/open-in-codespaces?assignment_repo_id=20616310)

<div align="center">

# GraphTest

**Plataforma de apoio à modelagem e análise de grafos para teste de software**

<img src="./Artefatos/imagens/graphtest-logo.svg" alt="Logo GraphTest" width="700"/>

[Sobre](#sobre) |
[Funcionalidades](#funcionalidades) |
[Tecnologias](#tecnologias) |
[Instruções de utilização](#instruções-de-utilização) |
[Testes](#testes) |
[Equipe](#equipe) |
[Licença](#licença)

</div>

## Sobre

O **GraphTest** é uma aplicação web desenvolvida para apoiar o ensino e a prática de testes de software por meio da criação, visualização e análise de grafos usados em testes funcionais e estruturais.

A plataforma permite trabalhar com **Grafos de Fluxo de Controle (GFC)**, voltados ao teste estrutural, e **Grafos de Causa e Efeito (GCE)**, voltados ao teste funcional. A partir desses modelos, o sistema auxilia na geração de tabelas de decisão, no cálculo de complexidade ciclomática e na produção de assinaturas de testes.

O projeto foi desenvolvido no contexto acadêmico da PUC Minas, integrando documentação, protótipos, diagramas, código-fonte e artefatos de acompanhamento do Trabalho de Conclusão de Curso.

## Funcionalidades

- Autenticação e cadastro de usuários.
- Criação e gerenciamento de projetos.
- Importação de código-fonte Java para geração de GFC.
- Visualização interativa de grafos estruturais.
- Cálculo de complexidade ciclomática.
- Geração de assinaturas de testes estruturais.
- Modelagem visual de GCE com causas, efeitos, operadores lógicos e restrições.
- Validação de consistência de modelos de causa e efeito.
- Geração e edição de tabelas de decisão.
- Geração de assinaturas de testes funcionais.
- Organização dos artefatos por projeto.

## Tecnologias

### Front-end

- React 19
- TypeScript
- Vite
- React Router
- Axios
- React Flow
- CodeMirror
- Tailwind CSS
- Lucide React

### Back-end

- Java 25
- Spring Boot 4.0.3
- Spring Web MVC
- Spring Security
- Spring Data JPA
- Spring Data Neo4j
- Springdoc OpenAPI
- JavaParser
- MapStruct
- Lombok
- Maven

### Bancos de dados

- PostgreSQL para dados relacionais.
- Neo4j para persistência dos grafos.

## Arquitetura

O back-end segue uma organização inspirada em **Arquitetura Hexagonal (Ports and Adapters)**:

- `application`: regras de negócio, casos de uso, portas e modelos de domínio.
- `adapters/inbound`: controllers, DTOs, validações, tratamento de erros e segurança HTTP.
- `adapters/outbound`: persistência JPA, persistência Neo4j, mapeadores e serviços externos.
- `src/test`: testes automatizados de domínio, serviços, casos de uso, controllers, conversores e repositórios.

O front-end está organizado por funcionalidades:

- `features/auth`: autenticação e cadastro.
- `features/home`: tela inicial autenticada e projetos.
- `features/projects`: gerenciamento e visão de projeto.
- `features/graph`: visualização e manipulação de GFC.
- `features/gce`: editor visual de GCE.
- `features/decision-table`: tabela de decisão e assinatura funcional.
- `services`: clientes HTTP para comunicação com a API.
- `routes`: rotas públicas e protegidas da aplicação.

## Estrutura do repositório

```text
.
|-- Artefatos/       # Diagramas, protótipos, vídeos, memoriais e PDFs de entrega
|-- Codigo/
|   |-- back-end/    # API Spring Boot
|   `-- front-end/   # Aplicação React + Vite
|-- Divulgacao/      # Materiais de divulgação
|-- Documentacao/    # Documentos acadêmicos do projeto
|-- CITATION.cff
|-- LICENSE
`-- README.md
```

## Instruções de utilização

### Pré-requisitos

- Java 25+
- Maven 3.9+
- Node.js 20+
- npm
- PostgreSQL
- Neo4j

### Back-end

Configure as propriedades esperadas pela aplicação. O arquivo principal é:

```text
Codigo/back-end/src/main/resources/application.properties
```

Ele espera as seguintes configurações:

```properties
DB_URL=jdbc:postgresql://localhost:5432/graphtest
DB_USERNAME=postgres
DB_PASSWORD=sua_senha

NEO4J_URI=neo4j://localhost:7687
NEO4J_USERNAME=neo4j
NEO4J_PASSWORD=sua_senha
NEO4J_DATABASE=neo4j

JWT_SEGREDO=uma_chave_segura_para_assinatura
JWT_TEMPO_EXPIRACAO=86400000
```

Para ambiente local, essas propriedades podem ser definidas em:

```text
Codigo/back-end/src/main/resources/application-local.properties
```

Execute a API:

```bash
cd Codigo/back-end
./mvnw spring-boot:run
```

No Windows PowerShell:

```powershell
cd Codigo\back-end
.\mvnw.cmd spring-boot:run
```

Por padrão, a API Spring Boot fica disponível em:

```text
http://localhost:8080
```

Com a aplicação em execução, a documentação OpenAPI pode ser acessada em:

```text
http://localhost:8080/swagger-ui.html
```

### Front-end

Crie um arquivo `.env` em `Codigo/front-end` apontando para a API:

```env
VITE_API_BASE_URL=http://localhost:8080
```

Instale as dependências e inicie o servidor de desenvolvimento:

```bash
cd Codigo/front-end
npm install
npm run dev
```

O Vite informará a URL local da aplicação, normalmente:

```text
http://localhost:5173
```

## Testes

### Back-end

```bash
cd Codigo/back-end
./mvnw test
```

No Windows PowerShell:

```powershell
cd Codigo\back-end
.\mvnw.cmd test
```

### Front-end

O front-end possui verificação de lint configurada:

```bash
cd Codigo/front-end
npm run lint
```

Para gerar uma build de produção:

```bash
cd Codigo/front-end
npm run build
```

## Artefatos acadêmicos

Os principais materiais de apoio ao projeto estão em `Artefatos/`, incluindo:

- Diagramas de arquitetura, classe, componente, caso de uso, sequência, estados, comunicação, implantação e entidade-relacionamento.
- Protótipos de interface.
- Vídeos de entrega.
- Memoriais individuais.
- PDFs das entregas de pré-banca e versão final.

## Padrão de commits

O projeto adota o padrão [**Conventional Commits**](https://www.conventionalcommits.org/en/v1.0.0/):

```text
<type>[optional scope]: <description>
```

Exemplos:

```bash
feat(gfc): adicionar geração de assinatura estrutural
fix(auth): corrigir validação do token jwt
docs(readme): atualizar instruções de execução
```

Tipos comuns:

- `feat`
- `fix`
- `docs`
- `refactor`
- `test`
- `style`
- `perf`
- `build`
- `ci`

## Equipe

### Alunos

- Lucas Cabral Soares
- Maria Eduarda Amaral Muniz

### Professor responsável

- Cleiton Silva Tavares
- Danilo de Quadros Maia Filho
- Leonardo Vilela Cardoso
- Raphael Ramos Dias Costa

## Licença

Este projeto está licenciado sob a **Creative Commons Attribution 4.0 International**. Consulte o arquivo [LICENSE](LICENSE) para mais detalhes.
