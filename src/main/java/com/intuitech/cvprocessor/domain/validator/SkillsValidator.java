package com.intuitech.cvprocessor.domain.validator;

import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validator for skills field
 * 
 * Validates that skills include both Java and LLM.
 */
@Component
@Slf4j
public class SkillsValidator {

    /**
     * Validate skills
     * 
     * @param extractedFields the extracted fields
     * @return validation result
     */
    public ValidationResult validate(ExtractedFields extractedFields) {
        log.debug("Validating skills");

        String skills = extractedFields.getSkills();
        
        if (skills == null || skills.trim().isEmpty()) {
            return ValidationResult.invalid("Skills not found");
        }

        String lowerCaseSkills = skills.toLowerCase();
        
        boolean hasJava = containsSkill(lowerCaseSkills, "java");
        boolean hasLLM = containsSkill(lowerCaseSkills, "llm") || 
                        containsSkill(lowerCaseSkills, "large language model") ||
                        containsSkill(lowerCaseSkills, "ai");

        if (!hasJava) {
            return ValidationResult.invalid("Skills must include Java");
        }

        if (!hasLLM) {
            return ValidationResult.invalid("Skills must include LLM or AI");
        }

        return ValidationResult.valid("Skills include required Java and LLM/AI");
    }

    /**
     * Check if skills contain a specific skill
     */
    private boolean containsSkill(String skills, String skill) {
        return skills.contains(skill);
    }

    /**
     * Validation result class
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        private ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public static ValidationResult valid(String message) {
            return new ValidationResult(true, message);
        }

        public static ValidationResult invalid(String message) {
            return new ValidationResult(false, message);
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }
}
