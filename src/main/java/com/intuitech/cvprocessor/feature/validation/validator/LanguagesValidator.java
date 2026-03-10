package com.intuitech.cvprocessor.feature.validation.validator;

import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import com.intuitech.cvprocessor.feature.validation.dto.ValidationResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validator for languages field
 *
 * Validates that languages include both Hungarian and English.
 */
@Component
@Slf4j
public class LanguagesValidator implements Validator {

    private static final String FIELD_NAME = "languages";

    @Override
    public ValidationResultDTO validate(ExtractedFields extractedFields) {
        log.debug("Validating languages");

        String languages = extractedFields.getLanguages();

        if (languages == null || languages.trim().isEmpty()) {
            return ValidationResultDTO.invalid("Languages not found", FIELD_NAME);
        }

        String lowerCaseLanguages = languages.toLowerCase();

        boolean hasHungarian = containsLanguage(lowerCaseLanguages, "hungarian") ||
                containsLanguage(lowerCaseLanguages, "magyar");
        boolean hasEnglish = containsLanguage(lowerCaseLanguages, "english") ||
                containsLanguage(lowerCaseLanguages, "angol");

        if (!hasHungarian) {
            return ValidationResultDTO.invalid("Languages must include Hungarian", FIELD_NAME);
        }

        if (!hasEnglish) {
            return ValidationResultDTO.invalid("Languages must include English", FIELD_NAME);
        }

        return ValidationResultDTO.valid("Languages include required Hungarian and English", FIELD_NAME);
    }

    /**
     * Check if languages contain a specific language
     */
    private boolean containsLanguage(String languages, String language) {
        return languages.contains(language);
    }

    @Override
    public String getFieldName() {
        return FIELD_NAME;
    }

    @Override
    public int getPriority() {
        return 30; // Medium priority
    }
}

