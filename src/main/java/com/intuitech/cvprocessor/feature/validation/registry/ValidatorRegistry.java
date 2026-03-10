package com.intuitech.cvprocessor.feature.validation.registry;

import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import com.intuitech.cvprocessor.feature.validation.dto.ValidationResultDTO;
import com.intuitech.cvprocessor.feature.validation.validator.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Registry for managing and executing all validators
 *
 * Automatically discovers and executes all Validator implementations.
 */
@Component
@Slf4j
public class ValidatorRegistry {

    private final List<Validator> validators;

    public ValidatorRegistry(List<Validator> validators) {
        this.validators = validators.stream()
                .sorted((v1, v2) -> Integer.compare(v1.getPriority(), v2.getPriority()))
                .collect(Collectors.toList());

        log.info("Initialized ValidatorRegistry with {} validators: {}",
                validators.size(),
                validators.stream().map(Validator::getFieldName).collect(Collectors.toList()));
    }

    /**
     * Execute all validators on extracted fields
     *
     * @param extractedFields the fields to validate
     * @return map of field name to validation result
     */
    public Map<String, ValidationResultDTO> validateAll(ExtractedFields extractedFields) {
        log.debug("Executing {} validators on extracted fields ID: {}",
                validators.size(), extractedFields.getId());

        return validators.stream()
                .collect(Collectors.toMap(
                        Validator::getFieldName,
                        validator -> {
                            try {
                                return validator.validate(extractedFields);
                            } catch (Exception e) {
                                log.error("Validator {} failed: {}", validator.getFieldName(), e.getMessage());
                                return ValidationResultDTO.invalid(
                                        "Validation error: " + e.getMessage(),
                                        validator.getFieldName()
                                );
                            }
                        }
                ));
    }

    /**
     * Get all registered validators
     *
     * @return list of validators
     */
    public List<Validator> getValidators() {
        return List.copyOf(validators);
    }
}

