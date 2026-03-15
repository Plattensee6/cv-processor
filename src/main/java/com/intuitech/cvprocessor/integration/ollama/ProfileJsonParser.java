package com.intuitech.cvprocessor.integration.ollama;

import com.fasterxml.jackson.databind.JsonNode;
import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import org.springframework.stereotype.Component;

@Component
public class ProfileJsonParser implements JsonFieldParser {

    @Override
    public void parse(JsonNode extractedJson, ExtractedFields.ExtractedFieldsBuilder builder) {
        if (extractedJson.has("profile") && !extractedJson.get("profile").isNull()) {
            builder.profile(extractedJson.get("profile").asText());
        }
    }
}

