## Purpose

This document gives a high‑level architectural overview of the virtual assistant system so that another coding agent can reuse the same structure as a reference implementation.

It intentionally describes **what exists today** (not improvements) and focuses on **components, boundaries, and responsibilities**.

## System Purpose

The system is a **virtual assistant platform** for banking use cases:

- **Customer chat** in web and embedded widgets.
- **Admin console** to manage documents, content, conversations and QA.
- **LLM‑powered question answering** over a bank knowledge base.
- **Guardrails / PI filtering / evaluation** for safe LLM usage.
- **DevOps bot** in Microsoft Teams for deployments, health checks and status.

## Major Subsystems

- **Frontend (Angular)**
  - `client` SPA: customer‑facing chat UI.
  - `webcomponent` SPA: embeddable chat widget.
  - `admin` SPA: admin & operations UI.
  - Shared Angular library with services, chat components, configuration and generated OpenAPI client.

- **Backend (Spring Boot, modular monolith)**
  - Single deployable service under `backend/backend-service`.
  - Provides REST APIs and WebSocket endpoints.
  - Hosts conversation engine, RAG tools, document management, QA, suggestion and logging.

- **Python Service (FastAPI)**
  - LLM‑adjacent microservice for:
    - Guardrails (input/output/topic, PI filter).
    - Keyword / semantic search helpers and BM25 indexes.
    - LLM‑based document chunking.
    - Testing & evaluation pipelines.
  - Exposed via HTTP (OpenAPI) and message queue.

- **API Specifications (`api` project)**
  - OpenAPI documents for:
    - Backend REST API (`openapi-backend.yml`).
    - Web‑client subset (`openapi-web.yml`).
    - Python integration API (`integration/pythonservice/pythonservice.yml`).
  - Node tooling to merge partial specs and generate unified YAMLs.

- **DevOps Bot (Spring Boot)**
  - Teams bot that:
    - Checks system status via `/actuator/health` endpoints.
    - Triggers GitLab pipelines (deploy/release).
    - Talks to Jira.
    - Uses OpenAI for friendly error messages.

- **Infrastructure & Runtime**
  - PostgreSQL with **pgvector** extension for embeddings.
  - ActiveMQ Artemis for JMS.
  - OAuth2/JWT resource server (Keycloak) for auth.
  - Docker images for backend, frontend, python_service, and bot.

## Architectural Style

- **Backend**: layered, but organized by **feature packages**:
  - `techcore`: cross‑cutting infrastructure (auth, logging, i18n, error handling, MQ, workflow, websocket).
  - `feature`: vertical business features (chat, conversation, tools, suggestion, spam, admin QA, document management, etc.).
  - `integration`: outbound integrations (OpenAI, python_service, doc generation, etc.).
- **Frontends**: monorepo Angular workspace with multiple projects + shared library.
- **Python Service**: microservice responsible for LLM‑adjacent workloads and heavy NLP.
- **APIs**: strongly **OpenAPI‑driven**, with generated clients in Java and TypeScript.

## Repository Structure (High Level)

This is the minimum structure a new project should mirror.

```text
root/
  backend/
    backend-service/
      src/main/java/io/company/project/
        techcore/                # shared infrastructure & cross‑cutting concerns
        feature/                 # business & chatbot features
        integration/            # external service integrations
      src/main/resources/
        application*.yml        # Spring config, profiles
        tool-configuration.yml  # tool & prompt configuration
        dynamic-prompt-context.yml
        semantic-routing/
        suggestion-routing/
        knowledge-management/
        db/changelog/           # Liquibase SQL & master YAML
        embedding/documents/    # markdown docs for RAG
      pom.xml
    docker/Dockerfile
    README.md

  frontend/
    package.json
    projects/
      admin/                    # admin SPA
      client/                   # customer SPA
      webcomponent/             # embeddable widget
    shared/
      services/                 # auth, chat, navigation, etc.
      services/chat/            # chat websocket + helpers
      chat-components/          # reusable chat UI widgets
      configs/                  # environment & socket configs
      openapi/                  # generated TS client
      ...
    docker/Dockerfile

  python_service/
    app/
      features/                 # FastAPI routers per feature
      techcore/                 # logging, NLP, LLM, STOMP, etc.
      middlewares/
      routing.py                # mounts feature routers
      properties.py             # YAML + env configuration
      config.yaml               # LLM & guardrails configuration
      subapis.py, stomp_client.py, ...
    pyproject.toml
    Dockerfile
    README.md
    main.py                     # uvicorn entrypoint

  api/
    openapi-backend.yml         # backend REST API
    openapi-web.yml             # web‑client API
    common/meta/meta-data.yml
    integration/pythonservice/pythonservice.yml
    generate/
      package.json              # OpenAPI merge scripts
      *.yml

  bot/
    src/main/java/.../bot/      # Teams bot
    src/main/resources/
      application.yml
    pom.xml
    Dockerfile
```

## How the Subsystems Interact

- **Frontends → Backend**
  - REST calls to `/api/...` and `/public/api/...` according to `openapi-web.yml`.
  - WebSocket connection to backend for real‑time chat, using `ChatMessageFromClient` / `ChatMessageFromServer` JSON schema.

- **Backend → Python Service**
  - HTTP via generated Java client from `pythonservice.yml`, used for:
    - Keyword search / BM25.
    - Guardrails (input/output/topic, PI filter).
    - LLM‑based document chunking.
    - Testing & evaluation helpers.
  - STOMP / MQ integration for some asynchronous flows (e.g. testing pipelines).

- **Backend → OpenAI (Java)**
  - Encapsulated in `integration.openai` using LangChain4j.
  - Used mainly by `AnswerService` and some tool services.

- **Python Service → LLM Providers**
  - Uses Python SDKs for OpenAI, Google GenAI, Azure AI, configured via `config.yaml` and environment variables.

- **Bot → Backend & Tooling**
  - Polls `/actuator/health` endpoints.
  - Interacts with GitLab, Jira, and OpenAI.

Use this overview as the **entry point**: each of the following documents in this folder zooms into a specific aspect (backend pipeline, python_service, data model, APIs, implementation rules).

