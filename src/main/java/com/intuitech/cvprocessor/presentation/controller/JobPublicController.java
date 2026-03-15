package com.intuitech.cvprocessor.presentation.controller;

import com.intuitech.cvprocessor.api.ApplicationResponse;
import com.intuitech.cvprocessor.api.ErrorMessage;
import com.intuitech.cvprocessor.api.JobSummaryDTO;
import com.intuitech.cvprocessor.application.dto.FileUploadResponseDTO;
import com.intuitech.cvprocessor.application.service.FileUploadService;
import com.intuitech.cvprocessor.domain.job.JobApplication;
import com.intuitech.cvprocessor.domain.job.JobPosting;
import com.intuitech.cvprocessor.feature.job.repository.JobApplicationRepository;
import com.intuitech.cvprocessor.feature.job.repository.JobPostingRepository;
import com.intuitech.cvprocessor.presentation.mapper.JobPostingApiMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Jobs (Public)", description = "Public job listings and application submission")
public class JobPublicController {

    private final JobPostingRepository jobPostingRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final FileUploadService fileUploadService;

    @GetMapping
    public List<JobSummaryDTO> listActiveJobs() {
        return jobPostingRepository.findByActiveTrueOrderByCreatedAtDesc().stream()
                .map(JobPostingApiMapper::toSummaryDto)
                .toList();
    }

    @PostMapping("/{jobId}/apply")
    public ResponseEntity<?> applyForJob(@PathVariable Long jobId,
                                         @RequestParam("name") String name,
                                         @RequestParam("email") String email,
                                         @RequestParam(value = "phone", required = false) String phone,
                                         @RequestParam("cv") MultipartFile cvFile) {
        log.info("Public application received for job {} from {}", jobId, email);

        JobPosting posting = jobPostingRepository.findById(jobId)
                .filter(JobPosting::isActive)
                .orElse(null);

        if (posting == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            FileUploadResponseDTO upload = fileUploadService.uploadFile(cvFile);

            JobApplication application = JobApplication.builder()
                    .jobPosting(posting)
                    .applicantName(name)
                    .applicantEmail(email)
                    .applicantPhone(phone)
                    .cvProcessingRequestId(upload.getRequestId())
                    .submittedAt(OffsetDateTime.now())
                    .build();

            JobApplication saved = jobApplicationRepository.save(application);

            return ResponseEntity.ok(new ApplicationResponse()
                    .applicationId(saved.getId())
                    .message("Jelentkezés sikeresen rögzítve")
                    .cvRequestId(upload.getRequestId()));
        } catch (FileUploadService.FileUploadException e) {
            log.error("Failed to upload CV for application: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new ErrorMessage().message("Nem sikerült a CV feldolgozása: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during job application: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(new ErrorMessage().message("Váratlan hiba történt a jelentkezés közben"));
        }
    }
}

