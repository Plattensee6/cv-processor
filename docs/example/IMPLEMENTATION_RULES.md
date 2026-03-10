## Cél

Ez a fájl **konkrét architekturális szabályokat és konvenciókat** gyűjt össze, amelyeket egy másik projektben is kötelező követni, ha ezt a rendszert szeretnénk mintaként újraépíteni.

Nem redesignt javasol, hanem a jelenlegi megoldás **lezárt szabályrendszerét** rögzíti.

---

## Rétegezés és csomagolás

1. **Backend Java projekt**
   - A kódot **három fő package‑szintre** kell struktúrálni:
     - `techcore` – keretréteg: auth, logging, i18n, hibakezelés, MQ, websocket, workflow, utility.
     - `feature` – függőleges funkcionális modulok (chat, conversation, tools, suggestion, spam, admin QA, dokumentumkezelés, stb.).
     - `integration` – külső szolgáltatások (OpenAI, python_service, dokumentum generátorok, stb.).
   - **Szabály**: `feature` réteg csak `techcore` + `integration` csomagokat hívhat; `techcore` nem ismerheti a `feature` szintet.

2. **Angular frontend workspace**
   - Több projekt (admin, client, webcomponent) + `shared` könyvtár.
   - Generated OpenAPI kliens a `shared/openapi` alatt.
   - Chat logika: `shared/services/chat` + `shared/chat-components`.

3. **Python service**
   - `features` (routerenkénti modulok) + `techcore` (NLP, LLM, logger, STOMP, util) + `middlewares` + központi `routing.py`.
   - Konfiguráció YAML+env alapokon, Pydantic modelszel olvasva.

---

## Chat pipeline invariánsai

1. **Chat üzenetek mindig WebSocketen menjenek**
   - DTO‑k: `ChatMessageFromClient` és `ChatMessageFromServer` – schema OpenAPI‑ban rögzítve.
   - A REST API nem küld és nem fogad chat üzeneteket, csak meta‑információkat (history, feedback stb.).

2. **Egyetlen orchestrator szolgálja ki a beszélgetést**
   - Minden user kérdés a backendben **`ConversationOrchestratorService`‑en** (vagy annak megfelelőjén) menjen át.
   - A pipeline lépéseit sosem szabad megkerülni:
     1. bemenet validálás,
     2. történet frissítés,
     3. topic extraction,
     4. semantic routing,
     5. guardrails (input),
     6. graph/hybrid/keyword search (+ crossencoder),
     7. question reform,
     8. PI filter + újra‑guardrail,
     9. `Conversation` implementáció, amely az `AnswerService`‑t hívja.

3. **LLM válasz generálást mindig dedikált „answer tool” végezze**
   - `AnswerService` (vagy ekvivalense) a kizárólagos felelős:
     - rendszer prompt összeállításáért (`SystemPromptService`),
     - korábbi Q&A történet összeválogatásáért,
     - LLM hívás felépítéséért/streameléséért,
     - token használat rögzítéséért.

4. **UI felé csak `ChatMessageService` közvetíthet**
   - A WebSocketen kimenő üzeneteket **mindig** a `ChatMessageService` állítja elő.
   - Minden más komponens ezen keresztül kommunikál a klienssel (APPEND, UPDATE, END, ERROR, TOOL_CALL, stb.).

---

## Prompt kezelés és konfiguráció

1. **Nagy promptok soha ne legyenek kódban hard‑code‑olva**
   - Minden hosszabb rendszer prompt:
     - Java oldalon: `tool-configuration.yml`, `dynamic-prompt-context.yml`, további YAML fájlok.
     - Python oldalon: `config.yaml` + külön system prompt YAML, amelyet a `ConfigSystemPrompt` és társai töltenek be.

2. **Promptok mindig konfig osztályon keresztül érhetők el**
   - Java: `OpenAIProperties` (`project.openai` prefix), tool‑specifikus properties osztályok; `SystemPromptService` olvassa be.
   - Python: `properties.py` Pydantic modelljei, `Properties` objektum, amely `config.yaml` + env alapján töltődik.

3. **Dinamikus prompt context támogatása**
   - Termék‑ / témaspecifikus extra prompt tartalmakat külön kulcsok alatt kell tartani (`dynamic-prompt-context.yml`).
   - A prompt generator feladata, hogy ezeket az aktuális témakör alapján fűzze be.

---

## OpenAPI‑vezérelt fejlesztés

1. **REST szerződések elsődleges forrása az OpenAPI**
   - Backend web API:
     - `api/openapi-backend.yml` és `api/openapi-web.yml`.
   - Python integráció:
     - `api/integration/pythonservice/pythonservice.yml`.
   - Ezeket nem szabad csak „dokumentumnak” tekinteni: ezek határozzák meg a kliensek és controllerek szerkezetét.

2. **Generált kliens könyvtárak használata kötelező**
   - Backend:
     - A Java OpenAPI generátor által előállított API osztályok (pl. `PythonServiceApi`, `KeywordSearchApi` stb.) **közvetlenül** legyenek használva.
   - Frontend:
     - `ng-openapi-gen` által generált TypeScript kliensek a `shared/openapi` alatt.

3. **API változások folyamata**
   - Változtatás:
     1. OpenAPI fájl frissítése.
     2. Generálás (backend: Maven profil; frontend: `npm run generate:api`).
     3. Kód adaptálása az új típusokhoz.

---

## Adatmodell és naplózás

1. **LLM hívásoknál mindig legyen explicit usage log**
   - Minden LLM hívás értéket adjon az `llm_usage` táblába.
   - A hívó kontextus (conversation, dokumentum, teszt) egy FK oszlopon keresztül hivatkozzon rá.

2. **Forrás chunkok visszakövethetősége kötelező**
   - Az `embedding` táblából trigger másolja a tartalmat `chunk_history`‑ba.
   - A felhasznált chunkok `conversation_chunk_history` kapcsolótáblán keresztül linkelődnek az `conversation_history` rekordokhoz.
   - Ez biztosítja, hogy később is auditálható legyen, milyen szövegen alapult egy válasz.

3. **Konverzációs történet normalizálása**
   - `conversation_history` csak az üzeneteket tárolja.
   - Session‑szintű metaadatok (egyedi session id) a `conversation_session_history` táblába kerülnek.
   - Admin mintavételezéshez külön `conversation_sample` + `conversation_sample_item` + tag táblák készülnek.

4. **DB migráció kizárólag Liquibase‑szel**
   - Minden séma‑változás új, verziózott changelog fájlban.
   - Kézi `ALTER TABLE` nem része az architektúrának.

---

## Python service specifikus szabályok

1. **Különálló mikroszolgáltatás**
   - A Python service **nem könyvtár**, hanem külön FastAPI app + Docker image.
   - A Java backend csak HTTP + MQ szinten kommunikál vele.

2. **Features vs techcore**
   - Feature routerek (`app/features/**`) csak a `techcore` által biztosított eszközökre támaszkodhatnak (LLM, NLP, logger, STOMP).
   - `techcore` réteg nem tartalmazhat domain‑specifikus (bank‑specifikus) logikát.

3. **Guardrails konfigurálhatósága**
   - Guardrails szabályokat konfigurációs fájl írja le (YAML) – nem kódban fixen.
   - Modellnevek, küszöbök, prompt kulcsok mind konfigból jönnek.

---

## DevOps és futtatás

1. **Minden fő komponens külön konténer**
   - Backend, frontend, python_service, bot külön Docker image.
   - Közös infrastruktúra: PostgreSQL (pgvector), ActiveMQ Artemis, Keycloak (vagy más IdP), monitoring/metrics stack.

2. **Actuator és health check endpointok**
   - Backend: Spring Boot Actuator `health`, `metrics`, `prometheus`.
   - Python service: `/health` FastAPI endpoint (OpenAPI‑ban definiálva).
   - Bot: `application.yml`‑ben konfigurált szolgáltatás listán fut health check.

3. **Konfiguráció profilok/projektek szerint**
   - Spring: `application.yml` + `application-*.yml` profilok.
   - Python: environment‑specifikus Pixi environmentek (`prod`, `prod-api`, `cicd`, stb.).
   - Frontend: környezetfüggő `environment.ts` fájlok (admin/client/webcomponent).

---

## Összefoglaló „do / don’t” lista másik agent számára

**DO**

- Tartsd a **rétegezési és package struktúra** szabályait (techcore / feature / integration).
- Használd a **WebSocket+REST** szétválasztást a chat és metainformációk között.
- Támaszkodj **OpenAPI‑generált kliensekre** (Java + TS).
- Tartsd külön a **Python LLM/guardrails szolgáltatást** mikroszolgáltatásként.
- Minden LLM hívást logolj külön **`llm_usage`** rekorddal.

