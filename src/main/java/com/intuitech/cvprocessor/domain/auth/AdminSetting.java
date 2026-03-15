package com.intuitech.cvprocessor.domain.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "admin_settings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminSetting {

    @Id
    @Column(name = "key", length = 100)
    private String key;

    @Column(name = "value", length = 1000)
    private String value;

    @Column(name = "updated_by")
    private Long updatedByUserId;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}

