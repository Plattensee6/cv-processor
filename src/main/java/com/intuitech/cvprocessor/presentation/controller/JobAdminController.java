package com.intuitech.cvprocessor.presentation.controller;

import com.intuitech.cvprocessor.api.JobPostingDTO;
import com.intuitech.cvprocessor.api.UpsertJobPostingRequest;
import com.intuitech.cvprocessor.domain.job.JobPosting;
import com.intuitech.cvprocessor.feature.job.repository.JobPostingRepository;
import com.intuitech.cvprocessor.presentation.mapper.JobPostingApiMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/jobs")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Job Administration", description = "Manage job postings for HR")
public class JobAdminController {

    private final JobPostingRepository jobPostingRepository;

    @GetMapping
    public List<JobPostingDTO> listAll() {
        return jobPostingRepository.findAll().stream()
                .map(JobPostingApiMapper::toDto)
                .toList();
    }

    @PostMapping
    public ResponseEntity<JobPostingDTO> create(@RequestBody UpsertJobPostingRequest request) {
        OffsetDateTime now = OffsetDateTime.now();

        JobPosting posting = JobPosting.builder()
                .title(request.getTitle())
                .location(request.getLocation())
                .employmentType(request.getEmploymentType())
                .description(request.getDescription())
                .requirements(request.getRequirements())
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        JobPosting saved = jobPostingRepository.save(posting);
        return ResponseEntity.ok(JobPostingApiMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobPostingDTO> update(@PathVariable Long id,
                                                @RequestBody UpsertJobPostingRequest request) {
        OffsetDateTime now = OffsetDateTime.now();
        return jobPostingRepository.findById(id)
                .map(existing -> {
                    JobPosting updated = JobPosting.builder()
                            .id(existing.getId())
                            .title(request.getTitle())
                            .location(request.getLocation())
                            .employmentType(request.getEmploymentType())
                            .description(request.getDescription())
                            .requirements(request.getRequirements())
                            .active(existing.isActive())
                            .createdByUserId(existing.getCreatedByUserId())
                            .createdAt(existing.getCreatedAt())
                            .updatedAt(now)
                            .build();
                    JobPosting saved = jobPostingRepository.save(updated);
                    return ResponseEntity.ok(JobPostingApiMapper.toDto(saved));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/archive")
    public ResponseEntity<JobPostingDTO> archive(@PathVariable Long id) {
        OffsetDateTime now = OffsetDateTime.now();
        return jobPostingRepository.findById(id)
                .map(existing -> {
                    JobPosting updated = JobPosting.builder()
                            .id(existing.getId())
                            .title(existing.getTitle())
                            .location(existing.getLocation())
                            .employmentType(existing.getEmploymentType())
                            .description(existing.getDescription())
                            .requirements(existing.getRequirements())
                            .active(false)
                            .createdByUserId(existing.getCreatedByUserId())
                            .createdAt(existing.getCreatedAt())
                            .updatedAt(now)
                            .build();
                    JobPosting saved = jobPostingRepository.save(updated);
                    return ResponseEntity.ok(JobPostingApiMapper.toDto(saved));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

