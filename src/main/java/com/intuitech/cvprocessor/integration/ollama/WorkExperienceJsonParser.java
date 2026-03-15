package com.intuitech.cvprocessor.integration.ollama;

import com.fasterxml.jackson.databind.JsonNode;
import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import org.springframework.stereotype.Component;

@Component
public class WorkExperienceJsonParser implements JsonFieldParser {

    @Override
    public void parse(JsonNode extractedJson, ExtractedFields.ExtractedFieldsBuilder builder) {
        if (!extractedJson.has("workExperience")) {
            return;
        }

        JsonNode workExp = extractedJson.get("workExperience");
        if (workExp == null || workExp.isNull()) {
            return;
        }

        if (workExp.has("years") && !workExp.get("years").isNull()) {
            builder.workExperienceYears(workExp.get("years").asInt());
        }
        if (workExp.has("details") && !workExp.get("details").isNull()) {
            builder.workExperienceDetails(workExp.get("details").asText());
        }
    }
}

