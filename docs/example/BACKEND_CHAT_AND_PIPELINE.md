## Scope

This document describes the **backend chat subsystem** in `backend/backend-service` and the **full chatbot pipeline** from WebSocket message intake to LLM answer streaming and persistence.

It is intended for coding agents who need to re‑implement the same flow in another project.

## Key Backend Packages

- `io.company.project.techcore`
  - **Websocket infrastructure** (`websocket` package): context holder, locale resolver, exception handler.
  - **Error handling** (`error`): base exception handler and custom exceptions.
  - **I18n** (`i18n`): message / translation services.
  - **Log history** (`loghistory`): annotation + entities to persist tool execution history.
  - **Auth** (`auth`): user/session/device entities and security configuration.

- `io.company.project.feature.chat`
  - Chat WebSocket message flow and state (`ChatContextHolder`, `ChatMessageService`, `ChatHistoryService`, `MessageProcessingService`, `WebSocketService`).

- `io.company.project.feature.conversation`
  - Conversation orchestration and history (`ConversationOrchestratorService`, `Conversation`, tools under `service.tools`, DTOs and entities).

- `io.company.project.feature.tools`
  - Individual tools used by the pipeline:
    - `answer` – main LLM answer generator (`AnswerService`, `SystemPromptService`).
    - `semanticsearch`, `hybridsearch`, `keywordsearch`, `crossencoder`, `documentchunking`, `datacollection`, `questionreform`, `topicextractor`, etc.

## WebSocket Chat Entry Points

**Frontend side**

- `frontend/shared/services/chat/chat-websocket.service.ts`:
  - Creates WebSocket connection to `env.serverBaseWebSocketUrl + SOCKET_PATH_MAP_CONFIG[appType]`.
  - Sends messages of type `ChatMessageFromClient`:
    - `clientName`
    - `content`
    - `messageGroupId`
    - `requestType` (`ChatClientRequestType.Question`, `Suggestion`, `Reset`, `Close`, `Keepalive`, etc.)
  - Receives `ChatMessageFromServer` objects and dispatches them based on `messageType` (`Append`, `Update`, `End`, `Suggestion`, `Error`, etc.).

**Backend side**

- WebSocket configuration (in `techcore.websocket`) routes frames to:
  - `MessageProcessingService` in `feature.chat.service`.

### `MessageProcessingService` responsibilities

- `processConnectionEstablished(WebSocketSession session)`
  - Creates chat context via `ChatContextHolder.createContext(session, ip)`.
  - Sets locale using `WebSocketLocaleResolver`.
  - Preloads conversation history via `ChatHistoryService.preloadChatHistory`.
  - If there is no saved history (or preload disabled), starts a new conversation via `ConversationOrchestratorService.startConversation`.

- `processMessage(WebSocketSession session, String payload)`
  1. Parses JSON payload into `ChatMessageFromClient` using `CustomObjectMapper`.
  2. Creates chat context for this message (session, messageGroupId, requestType, IP).
  3. Runs spam protection via `SpamProtectionService.check` (except for KEEPALIVE).
  4. Branches by `ChatClientRequestType`:
     - **QUESTION** → `ConversationOrchestratorService.handleUserQuery(message)`.
     - **SUGGESTION** → `PredefinedQueryService.handlePredefinedQuery(message)`.
     - **CLOSE** → `ChatHistoryService.clearChatHistory(...)`.
     - **RESET** → `ChatHistoryService.clearChatHistoryAndSendWelcomeMessage(...)`.
     - **KEEPALIVE** → `ChatMessageService.keepAlive()`.
     - **USER_PROFILE** → deserializes content into `ClientProfileFormDto`.
  5. Uses `WebSocketExceptionHandler` for error and spam responses to client.
  6. Always calls `ChatContextHolder.reset()` in `finally`.

- `processConnectionClosed(WebSocketSession session)`
  - Creates context and saves the chat history via `ChatHistoryService.saveChatHistory`.
  - Calls `ConversationOrchestratorService.endConversation()` to allow feature‑level cleanup.

## Conversation Orchestration

### Main orchestrator: `ConversationOrchestratorService`

Key collaborators:

- `ConversationHistoryService`
- `SemanticRoutingProperties` and `SemanticRoutingStructuredOutputService`
- `TopicExtractorStructuredOutputService`
- `QuestionReformService`
- `ConversationGraphSearchService`
- `ConversationGuardRailService`
- `QaWithLoanCalculationConversationService` and `SalesAgentConversationService` (implementing `Conversation`)
- `ConversationValidationService`
- `ChatSessionProperties`, `ConversationProperties`
- `LoanUserProfileService`
- `TranslationService`
- `ChatMessageService`

#### `startConversation()`

Executed on new connection:

1. If chat sessions and loan‑profile preload are enabled, calls `LoanUserProfileService.preloadLoanUserProfile()`.
2. If welcome messages are enabled:
   - Fetches translated `"chat.welcome"` message via `TranslationService`.
   - Sends it through `ChatMessageService.sendWelcomeMessage`.
   - Adds assistant message to history.
   - Ends message group via `ChatMessageService.end()`.

#### `handleUserQuery(ChatMessageFromClient message)`

End‑to‑end main pipeline:

1. **Validation**  
   `ConversationValidationService.validateMessageLength(message)`.

2. **History logging**  
   `ConversationHistoryService.addUserMessageToHistory(message)`.

3. **Topic extraction** (`topicExtraction()`)  
   - Skips if `ChatMessageUtils.isIrrelevantQuestion()` is true.  
   - Otherwise calls `TopicExtractorStructuredOutputService.topicExtraction()` to classify into `TypeTag`s and stores them in `ChatContextHolder`.

4. **Conversation selection** (`getConversationService()`)  
   - Based on `ApplicationType` (e.g. WEB vs MOBILE) and extracted `TypeTag`s.
   - Chooses between `SalesAgentConversationService` and `QaWithLoanCalculationConversationService` (via `Conversation` interface).

5. **Semantic routing**  
   - Delegates to `conversationService.runSemanticRouting()` which uses:
     - `SemanticRoutingStructuredOutputService`.
     - Config from `tool-configuration.yml` / `semantic-routing/semantic-routing.yml`.

6. **Guardrails – initial**  
   - `ConversationGuardRailService.handleInputGuardRails(message.getContent())`:
     - Uses Python guardrail endpoints (see Python doc) to decide whether to block or transform the input.
     - If blocked:
       - `conversationService.continueConversation(message, new ConversationFlowResultDTO(), true)`.
       - Return early.

7. **Graph / hybrid search**  
   - `ConversationGraphSearchService.handleGraphSearch(message)`:
     - Calls various search tools depending on semantic route:
       - `SemanticSearchService`, `HybridSearchService`, `KeywordSearchService`.
       - `CrossEncoderService` for reranking.
       - `DataCollectionService`, `DocumentChunkingService`, etc.
     - Under the hood, these tools:
       - Access embeddings (`embedding` table, `graph_node`, `graph_relation`).
       - Call Python service for keyword search, reranking and doc chunking.
     - Fills a `ConversationFlowResultDTO` with:
       - Selected chunks.
       - Graph search results.
       - Intermediate tool responses.

8. **Question reformulation**  
   - `reformMessage(ConversationFlowResultDTO flow)`:
     - Calls `QuestionReformService.reformQuestion()` to get `QuestionReformDTO`.
     - Writes reformed question into `flow`.

9. **PI filter and guardrails – post‑reform**  
   - `ConversationGuardRailService.handlePIFilter(reformedQuestion)` to apply PI filter.
   - `handleInputGuardRails(reformedQuestion)` again to re‑check the modified content.

10. **Conversation continuation**  
    - Calls `conversationService.continueConversation(message, flow, isReformedInputGuardrailBlocked)`.
    - The chosen `Conversation` implementation is responsible for:
      - Turning the flow result into an `AnswerRequestDto`.
      - Triggering downstream tools (most importantly `AnswerService`).

#### `endConversation()`

- Calls `getConversationService().endConversation()` to allow conversation‑specific cleanup and finalization.

## Answer Generation (Backend LLM Layer)

### Core class: `AnswerService`

Located in `feature.tools.answer.AnswerService`.

Dependencies:

- `OpenAIUtil` / `OpenAIChatCompletionStreamService` (Java LLM integration).
- `ToolCallMessageService` (for status messages to UI).
- `MessageService` (i18n).
- `ConversationHistoryService` (for Q&A history).
- `ChatMessageService` (streaming responses).
- `SystemPromptService` (prompt building).
- `SubQuestionSearchService` (multi‑question handling).

#### Flow of `answer(AnswerRequestDto answerRequestDto)`

1. **Log and tool‑call info**
   - Annotated with `@LogHistory(name = ANSWER, role = TOOL)` to store execution in log history tables.
   - Uses `ToolCallMessageService` and `MessageService` to send a “tool started” message (via WebSocket).

2. **Guardrail‑blocked path**
   - If `answerRequestDto.isBlocked()`:
     - Retrieves `"chat.error.blocked"` from `MessageService`.
     - Optionally appends text to chat via `ChatMessageService.append`.
     - Returns a `ConversationHistoryResponseDTO<String>` with the blocked message.

3. **Question breakdown path**
   - If `answerRequestDto.getQuestionBreakdownDTO() != null`:
     - Delegates to `SubQuestionSearchService.subQuestionSearch(...)` to handle multi‑part questions.

4. **Standard answer path**
   1. **Prompt construction**  
      - Calls `getChatMessagesWithSystemPrompt(collectedData)`:
        - `SystemPromptService.getSystemPrompt(collectedData)`:
          - Builds a rich system prompt using:
            - `tool-configuration.yml` → `project.tools.answer.*`.
            - `dynamic-prompt-context.yml` → extra per‑product prompt fragments.
            - Current time and function results placeholders.
        - Wraps system prompt into `SystemMessageDTO`.
        - Fetches previous questions & answers via `ConversationHistoryService.getPreviousQuestionsAndAnswers()`.
        - Prepends system message to the history list.
   2. **Chat request build**  
      - `OpenAIUtil.buildChatCompletionRequest(chatMessages)` converts `ChatMessageDTO`s into a LangChain4j `ChatRequest`.
   3. **Streaming OpenAI call**  
      - `OpenAIChatCompletionStreamService.streamResponse(chatRequest)`:
        - Uses `OpenAIProperties` (bound to `project.openai` in `application.yml`) for model names, timeouts, temperature.
        - Calls OpenAI in streaming mode.
        - During streaming, partial tokens are sent to the user via `ChatMessageService.appendPartialResponse(...)`.
        - Returns `ConversationHistoryResponseDTO<ChatMessageDTO>` with final answer content and LLM usage DTO.
   4. **Wrapping result**  
      - Builds `ConversationHistoryResponseDTO<String>`:
        - `response`: final LLM answer string.
        - `llmUsageDto`: token usage.

### `ChatMessageService` streaming responsibilities

- Creates and sends outbound `ChatMessageFromServer` DTOs over WebSocket.
- Key methods:
  - `append(content)` → single APPEND message.
  - `appendPartialResponse(content, messageGroupId, sessionId, isAutoTest, webSocketSession)` → streaming partial tokens (if `!isAutoTest`).
  - `update(content, messageGroupId)` → modify last message of group (UPDATE type).
  - `clearChat()` / `preloadEnd()` / `sendToolCallMessage()` / `sendWelcomeMessage()`.
  - `end(rateable)` → send END message with optional `rateable` flag.
  - `error(content, throwable)` / `spam(content, throwable)` → send ERROR / SPAM messages using `getErrorResponse`.
  - `keepAlive()` → KEEPALIVE ping messages.

On the frontend, `ChatWebsocketService` interprets these message types and delegates to `chat-message.service.ts` to render, update or clear messages.

## Supporting Concerns

### Spam Protection

- `SpamProtectionService`:
  - Uses `SpamProtectionProperties` and IP subnet rules to rate‑limit or block abusive usage.
  - Determines whether requests from a given `WebSocketSession` are allowed.

### WebSocket IP Resolution

- `WebSocketService.getIpAddress(WebSocketSession session)`:
  - Prefers `X-Forwarded-For` header when present and external.
  - Falls back to remote address (`InetSocketAddress`).
  - Uses `IpSubnetUtil` for subnet comparison and filtering.

### Logging & MDC

- `MessageProcessingService` sets MDC keys:
  - `MDC_SESSION_KEY = "userSessionIdentifier"`.
  - `MDC_MESSAGE_GROUP_ID_KEY = "messageGroupId"`.
  - These keys are added in both `setChatContextData` overloads.

## How to Reuse This Pipeline in Another Project

When re‑implementing, preserve the following **back‑end chat invariants**:

- WebSocket messages must use a **stable schema** (`ChatMessageFromClient` and `ChatMessageFromServer`) versioned in OpenAPI.
- A single **orchestrator service** (`ConversationOrchestratorService` equivalent) must own:
  - Message validation.
  - Topic extraction.
  - Semantic routing.
  - Guardrails integration.
  - RAG / graph search.
  - Question reform.
  - Delegation to answer tool(s).
- All **LLM calls** must:
  - Go through a centralized AI integration (OpenAIUtil / stream service).
  - Use structured prompts loaded from configuration, not hard-coded.
  - Log token usage via dedicated entities (`llm_usage` and log history).
- Chat history must be **persisted and queryable**, with:
  - `conversation_history` as the main table.
  - Optional `conversation_chunk_history` to track RAG sources.

