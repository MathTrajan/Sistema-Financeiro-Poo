# Sistema Financeiro POO
---

## 1. Tecnologias utilizadas

### 1.1 Java 17
- Linguagem base de todo o backend.
- Versão LTS, com suporte a **records**, **var**, **switch expressions** e **text blocks**.
- Toda a aplicação dos pilares POO (encapsulamento, herança, polimorfismo, abstração) é feita em Java.

### 1.2 Spring Boot 3.2.3
- Framework que elimina a configuração manual de servidor, banco e serialização JSON.
- Um único `main()` em `FinanceiroApplication.java` sobe a aplicação inteira (servidor Tomcat embutido, contexto Spring, conexão JDBC, JPA).
- Como foi usado: o projeto inteiro depende de anotações para amarrar as camadas.

| Anotação | Onde aparece | Função |
|---|---|---|
| `@SpringBootApplication` | `FinanceiroApplication` | Ponto de entrada e auto-configuração |
| `@RestController` | `LancamentoController`, `TipoMovimentoController` | Expõe endpoints REST (JSON) |
| `@Controller` | `WebController` | Serve a página HTML |
| `@Service` | `LancamentoService`, `TipoMovimentoService` | Camada de regras de negócio |
| `@Repository` | Interfaces em `repository/` | Acesso ao banco via JPA |
| `@Entity` / `@Table` | `Lancamento`, `TipoMovimento` | Mapeia classes para tabelas |
| `@GetMapping` / `@PostMapping` / `@PutMapping` / `@DeleteMapping` | Controllers | Rotas HTTP |
| `@Transactional` | Métodos de service | Garante atomicidade no banco |


### 1.3 Spring Data JPA + Hibernate (ORM)

- **ORM** liga código e banco: objeto vira linha, linha vira objeto — sem SQL escrito à mão.
- **JPA** define o padrão (anotações `@Entity`, `@Id`, `@Column`); **Hibernate** executa esse padrão e produz o SQL.
- Basta declarar uma `interface extends JpaRepository<Entidade, ID>`: o Spring entrega `findAll`, `save`, `deleteById`, `findById` prontos em runtime.
- O nome do método já é a consulta: `existsByNomeIgnoreCase` é convertido em `SELECT ... WHERE LOWER(nome) = LOWER(?)`.
- Com `ddl-auto=update`, o Hibernate compara as `@Entity` com o banco no startup e ajusta o schema — foi assim que a coluna `data_exclusao` surgiu.


### 1.4 PostgreSQL
- Banco de dados relacional onde os dados são persistidos.
- Configuração em `application.properties` (URL, usuário, senha, dialect).
- Estrutura gerada pelo próprio Hibernate:
  - `lancamentos` — todas as movimentações (com FK para `tipos_movimento`).
  - `tipos_movimento` — categorias (Energia, Salário, Lazer...).

### 1.5 Thymeleaf
- Motor de templates do Spring que serve o `index.html` em `http://localhost:8080`.
- No projeto, o uso é minimalista: o Thymeleaf entrega a página única (SPA leve) e todo o restante é feito via **JavaScript + fetch** para a API REST.

### 1.6 Lombok 1.18.36
- Elimina código repetitivo (getters, setters, construtores, equals/hashCode).
- Usado nas entidades e DTOs com anotações como `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`.

### 1.7 Jackson (`jackson-datatype-jsr310`)
- Serializa objetos Java para JSON e vice-versa automaticamente nas requisições REST.

### 1.8 Maven
- Gerenciador de dependências e build (`pom.xml`).
- Comandos usados: `mvn spring-boot:run` (executar) e `mvn test` (rodar testes).

### 1.9 Frontend (HTML + CSS + JavaScript puro)
- Página única em `index.html` com **3 abas** (Lançamentos, Resumo, Relatório).
- Sem framework JS: usa apenas `fetch()` para consumir a API REST.
- Botão **Imprimir** usa `window.print()` nativo do navegador para gerar o relatório em papel/PDF.

---

## 2. Arquitetura — camadas e fluxo

O projeto segue o padrão **MVC em 4 camadas**:

```
Browser (HTML/JS)
      │  fetch JSON
      ▼
Controller  (recebe HTTP, valida, devolve JSON)
      │
      ▼
Service     (regras de negócio, cálculos, transações)
      │
      ▼
Repository  (interface JPA — gera SQL automático)
      │
      ▼
PostgreSQL
```

**Por que separar:** cada camada tem uma responsabilidade única (princípio SRP). É possível trocar o frontend, o banco ou o framework web sem reescrever as regras de negócio.

---

## 3. Estrutura de pastas

```
Financeiro POO/
│
├── pom.xml                                  → Dependências Maven
├── Sistema.md                               → Este documento
│
├── src/main/java/com/financeiro/
│   │
│   ├── FinanceiroApplication.java           → Ponto de entrada (main) +
│   │                                          CommandLineRunner que insere as
│   │                                          14 categorias padrão na 1ª execução
│   │
│   ├── model/                               → Entidades e objetos de domínio
│   │   ├── Lancamento.java                  → @Entity → tabela "lancamentos"
│   │   ├── TipoMovimento.java               → @Entity → tabela "tipos_movimento"
│   │   ├── FormaPagamento.java              → ENUM (Dinheiro, Cartão, PIX, Boleto…)
│   │   └── ResumoFinanceiro.java            → Value Object imutável (totais do período)
│   │
│   ├── dto/
│   │   └── LancamentoDTO.java               → Desacopla a entidade JPA do JSON
│   │                                          trafegado entre frontend e backend
│   │
│   ├── repository/                          → Acesso a dados (interfaces JPA)
│   │   ├── LancamentoRepository.java        → CRUD + queries por data/tipo
│   │   └── TipoMovimentoRepository.java     → CRUD + existsByNomeIgnoreCase
│   │
│   ├── service/                             → Regras de negócio
│   │   ├── LancamentoService.java           → salvar, listar, calcular resumo,
│   │   │                                      marcar debitado, filtrar por período
│   │   │                                      (usa Stream + Lambda + ArrayList)
│   │   └── TipoMovimentoService.java        → listar categorias
│   │
│   └── controller/                          → Endpoints HTTP
│       ├── LancamentoController.java        → /api/lancamentos (CRUD + relatório)
│       ├── TipoMovimentoController.java     → /api/tipos
│       └── WebController.java               → "/" → entrega index.html
│
├── src/main/resources/
│   ├── application.properties               → URL do banco, ddl-auto, dialeto
│   └── templates/
│       └── index.html                       → Frontend completo (HTML + CSS + JS)
│
└── src/test/java/com/financeiro/
    └── FinanceiroApplicationTests.java      → 12 testes JUnit dos requisitos POO
```

---

## 4. Tópicos de importância para a apresentação

1. **POO aplicada de verdade** — não é só sintaxe: ENUM (`FormaPagamento`), Value Object imutável (`ResumoFinanceiro`), encapsulamento via DTO, polimorfismo via interfaces JPA.
2. **Banco gerado pelo código** — nenhuma tabela foi criada manualmente; o Hibernate lê as entidades e monta o schema.
3. **Separação em camadas (MVC)** — Controller → Service → Repository → DB. Cada arquivo tem uma responsabilidade.
4. **API REST + frontend independente** — o backend só fala JSON; o frontend poderia ser trocado por um app mobile sem alterar uma linha do Java.
5. **Stream e Lambda no cálculo do resumo** — `lancamentos.stream().filter(...).map(...).reduce(...)` em `LancamentoService`, prova prática de programação funcional dentro de POO.
6. **Persistência de dados real** — PostgreSQL com JPA/Hibernate, transações `@Transactional`, queries derivadas por nome de método.
7. **Inicialização inteligente** — `CommandLineRunner` cria as 14 categorias padrão só se não existirem (`existsByNomeIgnoreCase`), evitando duplicação.
8. **Relatório imprimível** — função `gerarRelatorio()` no front + endpoint `/api/lancamentos/relatorio` no back + `window.print()` para gerar PDF nativo.
9. **Testes unitários** — 12 testes JUnit que rodam sem banco, validando as regras de negócio puras.
10. **Build padronizado com Maven** — `mvn spring-boot:run` sobe tudo, sem instalar Tomcat ou configurar servidor.
