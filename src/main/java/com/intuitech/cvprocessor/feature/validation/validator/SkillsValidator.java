package com.intuitech.cvprocessor.feature.validation.validator;

import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import com.intuitech.cvprocessor.feature.validation.dto.ValidationResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validator for skills field
 *
 * Validates that skills include both Java and LLM.
 */
@Component
@Slf4j
public class SkillsValidator implements Validator {

    private static final String FIELD_NAME = "skills";

    @Override
    public ValidationResultDTO validate(ExtractedFields extractedFields) {
        log.debug("Validating skills");

        String skills = extractedFields.getSkills();

        if (skills == null || skills.trim().isEmpty()) {
            return ValidationResultDTO.invalid("Skills not found", FIELD_NAME);
        }

        String lowerCaseSkills = skills.toLowerCase();

        boolean hasJava = containsSkill(lowerCaseSkills, "java");
        boolean hasLLM = containsSkill(lowerCaseSkills, "llm") ||
                containsSkill(lowerCaseSkills, "large language model") ||
                containsSkill(lowerCaseSkills, "ai");

        if (!hasJava) {
            return ValidationResultDTO.invalid("Skills must include Java", FIELD_NAME);
        }

        if (!hasLLM) {
            return ValidationResultDTO.invalid("Skills must include LLM or AI", FIELD_NAME);
        }

        return ValidationResultDTO.valid("Skills include required Java and LLM/AI", FIELD_NAME);
    }

    /**
     * Check if skills contain a specific skill
     */
    private boolean containsSkill(String skills, String skill) {
        return skills.contains(skill);
    }

    @Override
    public String getFieldName() {
        return FIELD_NAME;
    }

    @Override
    public int getPriority() {
        return 20; // Medium priority
    }
}

