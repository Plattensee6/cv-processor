package com.intuitech.cvprocessor.feature.auth.repository;

import com.intuitech.cvprocessor.domain.auth.AdminSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminSettingsRepository extends JpaRepository<AdminSetting, String> {
}

