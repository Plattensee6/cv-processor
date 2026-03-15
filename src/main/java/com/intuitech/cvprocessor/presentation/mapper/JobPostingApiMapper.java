package com.intuitech.cvprocessor.presentation.mapper;

import com.intuitech.cvprocessor.api.JobPostingDTO;
import com.intuitech.cvprocessor.api.JobSummaryDTO;
import com.intuitech.cvprocessor.domain.job.JobPosting;

public final class JobPostingApiMapper {

    private JobPostingApiMapper() {}

    public static JobPostingDTO toDto(JobPosting p) {
        return new JobPostingDTO()
                .id(p.getId())
                .title(p.getTitle())
                .location(p.getLocation())
                .employmentType(p.getEmploymentType())
                .active(p.isActive())
                .description(p.getDescription())
                .requirements(p.getRequirements());
    }

    public static JobSummaryDTO toSummaryDto(JobPosting p) {
        return new JobSummaryDTO()
                .id(p.getId())
                .title(p.getTitle())
                .location(p.getLocation())
                .employmentType(p.getEmploymentType());
    }
}
