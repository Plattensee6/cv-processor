## Cél

Ez a dokumentum a rendszer **adatmodelljét** (DB szinten) és a fő **REST / WebSocket / Python integrációs API‑kat** foglalja össze olyan módon, hogy egy másik projektben reprodukálható legyen ugyanaz a felépítés.

---

## Adatmodell – fő entitások

A sémát Liquibase kezeli a `backend/backend-service/src/main/resources/db/changelog` könyvtárban. Itt csak a chatbot‑hoz és LLM‑hez kapcsolódó legfontosabb táblákat listájuk.

### Chat naplózás és történet

**`conversation_history`**

- Forrás: `1.0.0/script/create_conversation_history_table.sql` + későbbi módosítások.
- Fő mezők:
  - `id` – elsődleges kulcs (IDENTITY).
  - `session_id` – kliens munkamenet azonosító.
  - `message_group_id` – egy „üzenetcsoport” (kliens kérdés + asszisztens válasz) azonosítója.
  - `role` – szerep (user / assistant / tool, stb.).
  - `content` – üzenet tartalom.
  - `name`, `args` – tool‑hívásoknál a tool neve és argumentumok.
  - `tokens`, `run_time` – régi mezők, részben kiváltva `llm_usage`‑szel.
  - `created_at` – időbélyeg.
  - `llm_usage_id` (FK) – hivatkozás az LLM token használati rekordra.

**`conversation_session_history`**

- Forrás: `4.0.0/script/create_conversation_session_history.sql`.
- Mezők:
  - `id` – elsődleges kulcs (BIGSERIAL).
  - `session_id` – egyedi, a `conversation_history.session_id` mezőből származik.

Kapcsolat:

- Egy `conversation_session_history` rekordhoz több `conversation_history` sor tartozik (1‑N).

### RAG – embeddingek és chunk használat

**`embedding`**

- Forrás: `1.0.0/script/create_embedding_table.sql`.
- Mezők:
  - `id` – elsődleges kulcs (IDENTITY).
  - `content` – eredeti szöveg (chunk).
  - `source` – dokumentum / forrás azonosító.
  - `type_tag` – kategória (pl. termék típus).
  - `token_size` – tokenek száma.
  - `embedding` – `vector(3072)` (pgvector).

**`chunk_history`**

- Forrás: `1.15.0/script/create_chunk_history.sql`.
- Mezők:
  - `id` – elsődleges kulcs.
  - `content`, `content_hash`, `source`.
  - Egyedi constraint: `content_hash`.
- Trigger:
  - `copy_embedding_content_to_chunk_history_trigger`:
    - Minden új `embedding` rekordot bemásol `chunk_history`‑ba (hash alapján deduplikál).

**`conversation_chunk_history`**

- Forrás: `1.15.0/script/create_chunk_history.sql`.
- Mezők:
  - `chunk_history_id` (FK → `chunk_history.id`).
  - `conversation_history_id` (FK → `conversation_history.id`).
  - `relevance` – relevancia kategória (pl. HIGH/MEDIUM/LOW).
  - `tool_type` – melyik tool használta (`SEMANTIC_SEARCH`, `HYBRID_SEARCH`, stb.).
  - `relevance_score` – numerikus pontszám.
  - `sql_query` – opcionális SQL, amellyel a chunk előkerült.
- PK: `(chunk_history_id, conversation_history_id)`.

### LLM használat

**`llm_usage`**

- Forrás: `3.0.0/script/create_llm_usage.sql`.
- Mezők:
  - `id` – elsődleges kulcs.
  - `prompt_tokens`, `completion_tokens`, `total_tokens`, `cached_tokens`.
  - `created_at`.
- Kapcsolatok:
  - `conversation_history.llm_usage_id`.
  - `document_management.qa_generate_llm_usage_id`, `chunking_llm_usage_id`, `create_embeddings_llm_usage_id`.
  - `test_scenario.test_evaluate_llm_usage_id`.

### Dokumentumkezelés

**`document_management`**

- Forrás: `1.0.0/script/create_document_management_table.sql` + későbbi LLM usage Fk‑k.
- Mezők:
  - `id` – elsődleges kulcs.
  - `filename`, `upload_date`.
  - `file_store_id` – FK a `file_store` táblára.
  - Több LLM usage FK (QA generálás, chunking, embedding készítés).

**`document_extracted_field`**

- Forrás: `4.0.0/script/create_document_extracted_field_table.sql`.
- Struktúrált mezők kivonata a dokumentumokból (pl. űrlap mezők).

### Chat visszajelzés és QA

**`chat_feedback`**

- Forrás: `1.0.0/script/create_chat_feedback_table.sql`.
- Mezők:
  - `id` – elsődleges kulcs.
  - `session_id`, `message_group_id`.
  - `type` – pozitív/negatív stb.
  - `description`, `reason`.
  - `created_at`.

**`conversation_sample` és kapcsolódó táblák**

- Forrás: `4.0.0/script/create_conversation_sample.sql` + `create_conversation_session_history.sql`.

Táblák:

- `conversation_sample` – mintagyűjtemény meta:
  - `id`, `filer_snapshot`, `created_by`, `created_at`.
- `conversation_sample_item`:
  - `id`, `conversation_sample_id`, `conversation_session_history_id`.
  - Kapcsolat: egy sample több sessionhöz is tartozhat.
- `conversation_sample_item_type_tags`, `conversation_sample_item_subtype_tags`:
  - `(sample_item_id, type_tag)` illetve `(sample_item_id, sub_type_tag)` – N‑N kapcsolat a type/subtype tag‑ekhez.

### Egyéb fontos táblák (röviden)

- **Autotest / QA táblák**:
  - `test_scenario`, `test_dataset`, `test_chunk`, `test_evaluation`, `test_query_evaluation_detail`, `test_score`, `admin_qa_test_runs`, `admin_qa_test_results`.
  - Az LLM‑es tesztelés és QA panel adatait tárolják.

- **Graph keresés táblák**:
  - `graph_node`, `graph_relation`, `graph_relation_documents`.
  - Entitás‑kapcsolat graf a dokumentumok fölött (pl. entitások és relációk).

- **Lock táblák**:
  - `instance_lock`, `traininglock`.
  - Hosszabb futású folyamatok (pl. embedding újrafeldolgozás) kizárására.

---

## WebSocket és REST API-k (backend)

### WebSocket – Chat

**Üzenet sémák** (OpenAPI: `api/openapi-backend.yml` komponensek):

- `ChatMessageFromClient`
  - `clientName: string | null`
  - `content: string`
  - `messageGroupId: string`
  - `requestType: ChatClientRequestType`
  - Opcionálisan: `predefinedQuery`, `parentPredefinedQuery`, stb. (javaslatokhoz).

- `ChatMessageFromServer`
  - `messageType: ChatMessageType`
  - `content?: string`
  - `messageGroupId?: string`
  - `sessionId?: string`
  - `rateable?: boolean`
  - `toolCall?`, `productInfoSearchResults?`, `sources?`, stb.

**Fontos enumok**:

- `ChatClientRequestType`: `Question`, `Suggestion`, `Reset`, `Close`, `Keepalive`, `UserProfile`, stb.
- `ChatMessageType`: `Append`, `Update`, `End`, `Clear`, `Preload`, `PreloadEnd`, `Error`, `Spam`, `Suggestion`, `Sources`, `ProductInfo`, `CalculationResult`, `Redirect`, `Dial`, `ToolCall`, `Keepalive`.

### REST API – fő végpontok (részleges lista)

Minden REST végpont OpenAPI‑ban definiált (`api/openapi-backend.yml`, `api/openapi-web.yml`). Itt csak a chatbot‑hoz fontosabbakat foglaljuk össze.

| Endpoint | Metódus | Címke (tag) | Cél | Auth |
| --- | --- | --- | --- | --- |
| `/public/api/WEB/{applicationType}/feedback` | POST | Chat feedback | `ChatFeedbackRequest` beküldése, 204 válasz. | Public (applicationType param) |
| `/api/{channelType}/{applicationType}/suggestions` | GET | Suggestions | Előre definiált kérdések / javaslatok listázása. | JWT |
| `/api/{channelType}/{applicationType}/conversation-history` | GET | Admin conversation history management | Admin nézethez konverzációk listázása, szűrés/paging. | JWT, admin role |
| `/api/{channelType}/{applicationType}/conversation-history/export` | GET | Admin conversation history management | Export (ált. Excel/CSV). | JWT, admin |
| `/api/{channelType}/{applicationType}/product-info` | GET | Product Info Search | Termékinfó keresés (RAG, elődefiniált DTO). | JWT |
| `/api/{channelType}/{applicationType}/documents` | CRUD | Admin document management | Tudásbázis dokumentumok kezelése. | JWT, admin |
| `/api/{channelType}/{applicationType}/benchmark/*` | GET/POST | Benchmark | Kulcsszó/semantikus keresés benchmarkok. | JWT, admin |
| `/api/{channelType}/{applicationType}/admin-qa/*` | GET/POST | Admin excel QA evaluation | QA teszt futtatás, eredmények. | JWT, admin |
| `/public/api/...` (admin/client activation stb.) | GET/POST | Admin/Client activation | Aktivációs linkek kezelése. | Public |

**Authentikációs minta**

- Backend: OAuth2 Resource Server (`spring.security.oauth2.resourceserver.jwt`).
- Frontend: `keycloak-angular` szolgáltatás, `AuthHeaderName` konstans OpenAPI generált modellből.

---

## Python service HTTP API-k

### OpenAPI specifikáció

- Fájl: `api/integration/pythonservice/pythonservice.yml`.
- Generált Java kliens:
  - Csomag: `io.gbsolutions.project.integration.pythonservice.*` a `backend-service/target/generated-sources/openapi` alatt.

**Fő API interfészek** (részleges):

- `PythonServiceApi`
  - `healthcheck()` – GET `/health`.

- `KeywordSearchApi`
  - BM25/keyword search végpontok.

- `InputGuardrailsApi`, `OutputGuardrailsApi`, `PersonalInformationApi`
  - Input/output security guardrails, PI filter.

- `DocumentChunkingApi`
  - LLM‑alapú dokumentum chunking HTTP endpointjai.

- `CrossEncoderApi`
  - Cross‑encoder reranking hívások.

- `CalculationApi`
  - Matematika / kalkulációs funkciók (pl. teszteléshez, scoringhoz).

Az OpenAPI definícióból generált DTO‑k és kliensek jelentik a **kontraktust** a Java backend és Python service között.

---

## Rendszer‑szintű ábrák

### Chat / LLM adatkapcsolatok (ASCII)

```text
conversation_session_history (session_id)
           1
           |
           N
conversation_history (role, content, llm_usage_id, message_group_id)
           |
           | N
           |
        conversation_chunk_history (relevance, tool_type, relevance_score)
           |
           | N
           v
     chunk_history (content, source, content_hash)
           ^
           |
  trigger from embedding (vector)

llm_usage
  ^       ^       ^          ^
  |       |       |          |
conversation_history   document_management   test_scenario  ...

chat_feedback (session_id, message_group_id, type, reason, created_at)
```

### Komponens – API interakciók

```text
Frontend SPA-k
  |  \
  |   \  WebSocket (ChatMessageFromClient/Server)
  |    \
  |     v
  |   Backend WebSocket endpoint
  |       |
  |       v
  |   ConversationOrchestratorService
  |       |
  |       v
  |   Tools & RAG services
  |    /       \
  |   /         \ HTTP (OpenAPI)
  v  v           v
REST controllers  PythonService (FastAPI)
       |
       v
   PostgreSQL (+vector)
```

---

## Reimplementációs irányelvek (adatszint és API-k)

1. **OpenAPI‑first szerződés**  
   - REST és Python integrációs API‑k mindig OpenAPI leírásból induljanak ki.  
   - Generált kliensek (Java, TypeScript) használata kötelező – ne írj kézi klienseket.

2. **Adatszintű transzparencia a LLM körül**  
   - Minden LLM hívás kapjon **egyedi `llm_usage` rekordot**, amelyre a hívás kontextusa (pl. conversation_history, document_management, test_scenario) hivatkozik.

3. **RAG források visszakövethetősége**  
   - A felhasznált embedding chinkek legyenek nyomon követhetők:
     - `embedding` → `chunk_history` (trigger) → `conversation_chunk_history`.

4. **WebSocket és REST szerepek szigorú elválasztása**  
   - Chat üzenetek: mindig WebSocket.  
   - Metainformáció (history, feedback, dokumentumok, QA): REST.

5. **DB migrációk csak Liquibase‑en keresztül**  
   - Új táblák / mezők mindig új changelog fájlba kerüljenek, verziózott struktúrával.

