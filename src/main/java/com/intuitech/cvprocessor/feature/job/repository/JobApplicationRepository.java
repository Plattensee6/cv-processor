package com.intuitech.cvprocessor.feature.job.repository;

import com.intuitech.cvprocessor.domain.job.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
}

