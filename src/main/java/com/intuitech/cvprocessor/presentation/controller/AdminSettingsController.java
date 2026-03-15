package com.intuitech.cvprocessor.presentation.controller;

import com.intuitech.cvprocessor.api.UpdateResponse;
import com.intuitech.cvprocessor.domain.auth.AdminSetting;
import com.intuitech.cvprocessor.feature.auth.repository.AdminSettingsRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/settings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Settings", description = "Global configuration settings for the application")
public class AdminSettingsController {

    private final AdminSettingsRepository adminSettingsRepository;

    @GetMapping
    public Map<String, String> getAllSettings() {
        return adminSettingsRepository.findAll().stream()
                .collect(Collectors.toMap(AdminSetting::getKey, AdminSetting::getValue));
    }

    @PutMapping
    public ResponseEntity<?> updateSettings(@RequestBody Map<String, String> updates) {
        OffsetDateTime now = OffsetDateTime.now();
        updates.forEach((key, value) -> {
            AdminSetting existing = adminSettingsRepository.findById(key).orElse(null);
            AdminSetting toSave = existing != null
                    ? AdminSetting.builder()
                            .key(existing.getKey())
                            .value(value)
                            .updatedByUserId(existing.getUpdatedByUserId())
                            .updatedAt(now)
                            .build()
                    : AdminSetting.builder()
                            .key(key)
                            .value(value)
                            .updatedAt(now)
                            .build();
            adminSettingsRepository.save(toSave);
        });
        return ResponseEntity.ok(new UpdateResponse().message("Settings updated"));
    }
}

