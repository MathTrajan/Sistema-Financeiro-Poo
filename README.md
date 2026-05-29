# 💰 Sistema Financeiro Pessoal

> Aplicativo web de controle de despesas e receitas pessoais. Projeto acadêmico de **Programação Orientada a Objetos (POO)**.

![Java](https://img.shields.io/badge/Java-17-007396?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-6DB33F?style=flat&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-relacional-4169E1?style=flat&logo=postgresql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-build-C71A36?style=flat&logo=apachemaven&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-template-005F0F?style=flat&logo=thymeleaf&logoColor=white)
![Licença](https://img.shields.io/badge/uso-acadêmico-lightgrey?style=flat)

---

## 📋 Sobre

O usuário cadastra **despesas e receitas**, informa a forma de pagamento e acompanha
se os lançamentos já foram debitados da conta. Há uma tela de **relatório** que exibe
os lançamentos de um período com os totais — pronta para impressão/PDF.

A aplicação é **full-stack** em um único projeto: backend Java (API REST) + frontend
HTML/CSS/JavaScript puro, persistindo em PostgreSQL via JPA/Hibernate.

---

## ✅ Pré-requisitos

| Ferramenta | Versão | Verificar com |
|---|---|---|
| **JDK** | 17+ | `java -version` |
| **Maven** | 3.8+ | `mvn -version` |
| **PostgreSQL** | 13+ | `psql --version` |

> O Maven baixa todas as dependências automaticamente. Não é preciso instalar Tomcat — ele já vem embutido no Spring Boot.

---

## 🚀 Como executar

**1. Crie o banco de dados:**
```bash
psql -U postgres -c "CREATE DATABASE financeiro_db;"
```

**2. Configure usuário e senha** em `src/main/resources/application.properties`:
```properties
spring.datasource.username=postgres
spring.datasource.password=SUA_SENHA_AQUI
```

**3. Rode a aplicação:**
```bash
mvn spring-boot:run
```

**4. Acesse no navegador:**
```
http://localhost:8080
```

> ✨ As tabelas são criadas automaticamente pelo Hibernate (`ddl-auto=update`) e, na primeira execução, **14 categorias padrão** são inseridas. Nenhum SQL manual é necessário.

**Rodar os testes:**
```bash
mvn test
```

---

## 🧱 Arquitetura (MVC em 4 camadas)

```
Frontend (index.html + JS)
        │  fetch / JSON
        ▼
Controller  →  recebe HTTP, valida, devolve JSON
        ▼
Service     →  regras de negócio, cálculos, transações
        ▼
Repository  →  interface JPA (gera o SQL automaticamente)
        ▼
PostgreSQL
```

Cada camada tem uma responsabilidade única — é possível trocar frontend, banco ou
framework web sem reescrever as regras de negócio.

---

## 📂 Estrutura do projeto

```
src/main/java/com/financeiro/
├── FinanceiroApplication.java   → Ponto de entrada + carrega categorias iniciais
├── model/                       → Entidades e objetos de domínio
│   ├── Lancamento.java          → @Entity → tabela "lancamentos"
│   ├── TipoMovimento.java       → @Entity → tabela "tipos_movimento"
│   ├── FormaPagamento.java      → ENUM (Dinheiro, Cartão, PIX...)
│   └── ResumoFinanceiro.java    → Value Object imutável (totais do período)
├── dto/LancamentoDTO.java       → Desacopla a entidade JPA do JSON
├── repository/                  → Acesso a dados (interfaces JPA)
├── service/                     → Regras de negócio (Stream + Lambda)
└── controller/                  → Endpoints HTTP (REST + página web)

src/main/resources/
├── application.properties       → Configuração do banco
└── templates/index.html         → Frontend completo (HTML + CSS + JS)

src/test/                        → 12 testes JUnit dos requisitos POO
```

---

## 🎯 Requisitos POO aplicados

| Conceito | Onde está | O que demonstra |
|---|---|---|
| **Enum** | `model/FormaPagamento.java` | Tipo fixo com atributos e métodos — cada forma sabe se exige banco |
| **ArrayList** | `service/LancamentoService.java` | Lista dinâmica retornada explicitamente pelos métodos de listagem |
| **Lambda / Stream** | `service/LancamentoService.java` | `filter` → `map` → `reduce` para filtrar, transformar e somar lançamentos |
| **Value Object** | `model/ResumoFinanceiro.java` | Objeto imutável (campos `final`, sem setters, igualdade por valor) |

> 📖 Explicação detalhada de cada requisito, com trechos de código comentados, em **[`Sistema.md`](./Sistema.md)**.

---

## 🔌 Endpoints da API

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/lancamentos` | Lista todos os lançamentos |
| `POST` | `/api/lancamentos` | Cria um novo lançamento |
| `DELETE` | `/api/lancamentos/{id}` | Remove um lançamento |
| `PATCH` | `/api/lancamentos/{id}/debitado` | Marca como debitado |
| `GET` | `/api/tipos-movimento` | Lista as categorias |
| `GET` | `/api/formas-pagamento` | Lista as formas de pagamento |
| `GET` | `/api/dashboard` | Resumo do mês atual |
| `GET` | `/api/relatorio?dataInicio=X&dataFim=Y` | Relatório de um período |
| `GET` | `/` | Serve a página `index.html` |

---

## 🧪 Testes

`src/test/java/com/financeiro/FinanceiroApplicationTests.java` contém **12 testes
JUnit** que rodam sem banco e validam os 4 requisitos POO (Enum, ArrayList, Lambda
e Value Object). Execute com `mvn test`.

---

## 🛠️ Stack técnica

Java 17 · Spring Boot 3.2.3 · Spring Data JPA / Hibernate · PostgreSQL · Thymeleaf ·
Lombok · Jackson (JSR-310) · Maven · HTML/CSS/JavaScript puro

---

<sub>Desenvolvido por <b>Matheus Trajano Ferreira Alves</b> para a disciplina de Programação Orientada a Objetos.</sub>
