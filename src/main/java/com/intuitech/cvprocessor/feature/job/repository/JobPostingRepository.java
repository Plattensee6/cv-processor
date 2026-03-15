package com.intuitech.cvprocessor.feature.job.repository;

import com.intuitech.cvprocessor.domain.job.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    List<JobPosting> findByActiveTrueOrderByCreatedAtDesc();
}

