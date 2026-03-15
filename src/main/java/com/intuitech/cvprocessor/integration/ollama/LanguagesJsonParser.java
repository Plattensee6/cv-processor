package com.intuitech.cvprocessor.integration.ollama;

import com.fasterxml.jackson.databind.JsonNode;
import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import org.springframework.stereotype.Component;

@Component
public class LanguagesJsonParser implements JsonFieldParser {

    @Override
    public void parse(JsonNode extractedJson, ExtractedFields.ExtractedFieldsBuilder builder) {
        String languages = getTextOrJoinedArray(extractedJson, "languages");
        if (languages != null) {
            builder.languages(languages);
        }
    }

    private String getTextOrJoinedArray(JsonNode root, String fieldName) {
        if (!root.has(fieldName) || root.get(fieldName).isNull()) {
            return null;
        }

        JsonNode node = root.get(fieldName);
        if (node.isArray()) {
            StringBuilder builder = new StringBuilder();
            for (JsonNode element : node) {
                if (builder.length() > 0) {
                    builder.append(", ");
                }
                builder.append(element.asText());
            }
            return builder.toString();
        }

        return node.asText();
    }
}

