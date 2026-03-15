package com.intuitech.cvprocessor.integration.ollama;

import com.fasterxml.jackson.databind.JsonNode;
import com.intuitech.cvprocessor.domain.model.ExtractedFields;

/**
 * Strategy interface for parsing specific parts of the Ollama JSON response
 * into the {@link ExtractedFields} builder.
 */
public interface JsonFieldParser {

    void parse(JsonNode extractedJson, ExtractedFields.ExtractedFieldsBuilder builder);
}

