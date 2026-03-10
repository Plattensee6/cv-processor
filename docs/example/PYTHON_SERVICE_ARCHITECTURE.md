## Cél

Ez a jegyzet a `python_service` komponens architektúráját írja le úgy, hogy egy másik coding agent önállóan újra tudja építeni ugyanazt a szerepkört egy másik projektben.

## Fő szerep

`python_service` egy **FastAPI alapú LLM‑segéd mikroszolgáltatás**, amely:

- Guardrails funkciókat nyújt (input/output/topic, PI filter).
- Kulcsszó‑ és BM25 alapú keresést támogat.
- LLM‑alapú dokumentum feldarabolást végez (document chunking).
- Tesztelési és kiértékelési (LLM eval) pipeline‑okat valósít meg.
- STOMP (MQ) integrációt biztosít egyes aszinkron folyamatokhoz.

Ezt a szolgáltatást a Java backend **HTTP + OpenAPI kliensen** és időnként **STOMP üzenetküldésen** keresztül éri el.

## Könyvtárstruktúra

```text
python_service/
  app/
    config.yaml          # LLM, guardrails, model és log konfiguráció
    properties.py        # Pydantic Settings + YamlConfigSettingsSource wrapper
    routing.py           # összeaggregálja a FastAPI routereket
    subapis.py           # dokumentáció, sub‑app beállítás
    stomp_client.py      # STOMP MQ kliens és endpoint leírások
    middlewares/
      request_context.py
      id_context.py
      ...
    features/
      health/            # egészség ellenőrző endpoint
      example/
      keywordsearch/
      guardrails/
      math/
      llm_based_documentchunking/
      store_files/
      testing/
      ...
    techcore/
      logger/            # logging setup, GELF/Graylog integráció
      utils/             # általános segédfüggvények
      nlp/               # spaCy, tokenizálás, lemmatizálás, mondatképzés, stopwords
      spacy/             # spaCy model spec / loader
      token_count/       # token számlálás
      temp_file_storage/
      stomp/             # STOMP endpoint konverzió & listener
      llm_service/       # LLM kliens absztrakciók (OpenAI stb.)
      gemini_client/
      azure_client/
      llmevalka/         # LLM‑eval keretrendszer (metrikák, promptok)
    tests/
    tests_manual/
  pyproject.toml         # projekt meta + függőségek
  Dockerfile             # build + runtime image
  README.md
  main.py                # uvicorn entrypoint
```

## Futtatási modell

- **Belépési pont**: `main.py`

```python
uvicorn.run("app:application", host="127.0.0.1", port=8000)
```

- **FastAPI app**: `app/__init__.py`
  - `application = FastAPI(lifespan=lifespan)`
  - Lifespan:
    - `stomp_client.build_listener(app)` és `stomp_client.connect()` induláskor.
    - Takarítás leállításkor.
  - Indulás után:
    - `set_up_routing(application)` – feature routerek regisztrálása.
    - `set_up_error_handling(application)` – globális hibakezelők.
    - `set_up_middlewares(application)` – middleware lánc.
    - `set_up_subapis(application)` + `configure_root_docs_redirect(application)` – dokumentáció és al‑appok.

## Routing és feature réteg

### `routing.py`

```python
def set_up_routing(app: FastAPI) -> None:
    app.include_router(health.router)
    app.include_router(example.router)
    app.include_router(reranking.router)
    app.include_router(keywordsearch_router)
    app.include_router(pi_filter.router)
    app.include_router(guardrails_router)
    app.include_router(llm_auto_chunking_api.router)
    app.include_router(testing_router)
    app.include_router(calculate_api.router)
    app.include_router(store_files_router)
```

**Következmény**: minden feature **saját FastAPI routerrel** rendelkezik, amelyet a központi `set_up_routing` köt a fő alkalmazáshoz.

### Jellegzetes feature‑k

- `features.health`
  - Egyszerű `/health` endpoint a Java `PythonServiceApi.healthcheck()` hívásaihoz.

- `features.keywordsearch`
  - BM25‑alapú keresés.
  - Modellek, index útvonalak konfigurációja `config.yaml` → `KeywordSearchProperties`.

- `features.guardrails`
  - Input/output/topic guardrails és PI filter.
  - Konfiguráció + rendszer promptok: `config.yaml` + külön system prompt YAML fájl.

- `features.llm_based_documentchunking`
  - Dokumentum előfeldolgozó (`custom_preprocessor.py`) és chunkoló (`custom_chunker.py`) modulok.
  - HTTP endpointok, melyeket a Java backend `DocumentChunkingApi` generált kliensen keresztül hív.

- `features.testing`
  - LLM tesztelés, auto‑evaluation pipeline-ok.
  - Sok esetben MQ alapú integrációt használ (STOMP).

## Konfigurációs modell

### `config.yaml`

Példa fontos részekre:

- **Logging**
  - Fájl helye, szint, Graylog/GELF beállítások.

- **Modellek**
  - `models.crossencoder`: cross‑encoder modell neve.
  - `models.spacy_specs.*`: spaCy modellek metaadatai (nyelv, verzió, pipeline stb.).

- **Keyword search**
  - `bm25_pkls_path`: BM25 indexek útvonala.
  - `models`: melyik nyelvhez melyik spaCy spec tartozik.

- **Szolgáltatások**
  - `services.openai.api_key`, `services.gemini.api_key`, `services.azure.api_key` + `endpoint`.

- **Guardrails**
  - Blacklist fájlok, küszöbértékek, LLM modellek (pl. `gpt-4.1-mini-2025-04-14`).
  - Input/output/topic guardrails konfigurációk (LLM beállítások, küszöbök, substitution prompt kulcsok).

### `properties.py` (Pydantic settings)

Fő felelősségek:

- `PROJECT_ROOT` meghatározása.
- Régi env változó nevek átirányítása újra (`_ENV_REMAP`).
- `SelfResolvingPath` típus:
  - Relatív fájl elérési utakat projekt‑gyökérhöz képest abszolúttá alakítja.
- `ConfigSystemPrompt` + `RecursiveConfigSystemPromptLoadingModel`:
  - A konfigurációs modellekben szereplő rendszer‑prompt mezők valójában **kulcsok** (pl. `"output.security.hacking"`), nem nyers szöveg.
  - A modell inicializálás után beolvassa a `system_prompts_yaml_path` alatt lévő YAML‑t, és a kulcsokból valós prompt szöveget csinál.

További Pydantic modellek:

- `LoggingProperties`, `ModelProperties`, `KeywordSearchProperties`, `ServicesProperties`, `Guardrails*Properties`, stb.
- Egy felső szintű `Properties` modell (`properties` változó) fogja össze és teszi elérhetővé a teljes konfigurációt.

## LLM és NLP réteg

### NLP stack (spaCy + társai)

`app/techcore/nlp` + `app/techcore/spacy`:

- Tokenizálás, mondatképzés, lemmatizálás:
  - `tokenizer/base.py`, `sentencizer/base.py`, `lemmatizer/base.py` és spaCy‑s implementációk.
- Stopwords:
  - Nyelvi speciális stopwords fájlok (pl. `stopwords/hu.txt`).
- SpaCy spec koncepció:
  - `SpacySpecProperties` különböző nyelvi modellekhez.
  - Letöltési mód (pl. `huspacy` vs `spacy`), pipeline komponensek (lemmatizer, sentencizer).

### LLM‑szolgáltatás absztrakciók

- `techcore.llm_service.base` + `openai.py`:
  - Minimális interfész LLM hívásokhoz (modell, temperature, reasoning_effort).
  - Konfiguráció `LlmProperties` illetve a `Properties` modellből.

- `techcore.llmevalka`:
  - Promptkészlet és metrikák:
    - `metrics/*.py` (faithfulness, retrieval metrics, context relevancy stb.).
    - `evaluation.py`, `_prompts.py`.
  - Ezt használják a `features.testing` alatti modulok automatikus minőség‑méréshez.

## STOMP / MQ integráció

### `stomp_client.py`

Feladata:

- **STOMP kapcsolat** létrehozása az MQ brokerhez (`stomp.Connection`).
- Endpoint descriptorok definiálása (`EndpointDescriptor` TypedDict), amelyek:
  - `destination` (queue),
  - HTTP endpoint (`("/another-example/sleep", "POST")` vagy `Endpoint`),
  - request modell,
  - reply destination,
  - reply builder függvény.

Lényeges osztályok/fogalmak:

- `DispatchListener` + `MessageFrameHandler` (a `techcore.stomp` csomagban):
  - Egy bejövő MQ üzenetet HTTP hívássá alakít egy FastAPI endpoint felé.
  - A választ visszakódolja MQ üzenetté.

- `endpoint_reply_builder_factory`:
  - Közös `MessagingResponseStatus` + `errorMessage` + `response` + `id` mező formátumot állít elő.
  - Ez biztosítja, hogy a Java oldal uniform választ kapjon (id alapú összerendelés).

### Java oldalról

- A backend `backend-service` generált PythonService kliens kódja mellett STOMP‑alapú adaptereket is használ bizonyos tesztelési / chunking / QA folyamatoknál.
- A protokoll:
  - Java oldalon üzenet küldése egy queue‑ra.
  - Python oldalon FastAPI endpoint hívás + válasz MQ‑n.
  - `MessagingResponseStatus.SUCCESS` / `FAILED` és opcionális `errorMessage`.

## OpenAPI és Java integráció

### OpenAPI definíció

- Fájl: `api/integration/pythonservice/pythonservice.yml`.
- Ez alapján generálja a backend:
  - Kliens kódot a `backend-service/target/generated-sources/openapi/.../integration/pythonservice` csomagba.
  - API osztályokat, pl.:
    - `PythonServiceApi` (health check).
    - `KeywordSearchApi`, `InputGuardrailsApi`, `OutputGuardrailsApi`, `DocumentChunkingApi`, `CrossEncoderApi`, `CalculationApi`, stb.

### Generált Java kliens példa

- `PythonServiceApi.healthcheck()`:
  - GET `/health` endpoint hívása.
  - `HealthCheckResponse` DTO‑t ad vissza.

Más API‑k hasonló mintát követnek, csak más útvonalakkal és DTO‑kkal.

## Docker és futtatás

### Dockerfile (röviden)

- **Build stage**
  - Base image: `registry.gitlab.com/example-org/base-images/python-3120-slim:latest`.
  - Teljes projekt másolása `/app` alá.
  - `pixi` letöltése (conda‑szerű környezet manager).
  - `./pixi install --frozen -e prod -e prod-api`.
  - API DTO generálás: `./pixi run -e prod-api generate-api`.
  - Cache tisztítás.

- **Runtime stage**
  - Ugyanaz a base image.
  - `/app` átmásolása build stageről.
  - LibreOffice telepítése DOCX → PDF konverzióhoz.
  - `EXPOSE 8000`.
  - Entrypoint: `shell-hook.sh` + `uvicorn app:application --host 0.0.0.0 --port 8000 --proxy-headers`.

## Reimplementációs irányelvek

Ha egy másik projektben szeretnéd újraépíteni ezt az architektúrát:

1. **Tartsd külön a Python LLM/guardrails szolgáltatást** a Java backendtől:
   - Önálló FastAPI app saját Docker image‑gel.
   - OpenAPI specifikációval dokumentált HTTP API.

2. **Használj YAML‑alapú konfigurációt Pydantic settings‑szel**:
   - `config.yaml` + `properties.py` mintájára.
   - Rendszer promptokat külön YAML‑ben tárold.

3. **A funkciók legyenek külön routerekben (`features.*`)**:
   - Minden logikailag önálló modul: saját router + saját DTO‑k.
   - Egy központi `set_up_routing` függvény regisztrálja őket.

4. **Guardrails, keresés, chunking, evaluation legyenek újrafelhasználható modulok**:
   - Techcore réteg (LLM/NLP/util) ne függjön a konkrét banki domain‑től.
   - Feature modulok használják ezeket a szolgáltatásokat.

5. **Biztosíts OpenAPI‑alapú generált klienset a Java backend felé**:
   - A szerződés `pythonservice.yml`‑hez hasonlóan legyen egyetlen forrás.
   - A Java kliens legyen generált (ne kézzel írt), és az integration package‑ben éljen.

