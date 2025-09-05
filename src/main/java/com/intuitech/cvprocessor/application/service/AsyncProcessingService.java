package com.intuitech.cvprocessor.application.service;

import com.intuitech.cvprocessor.application.dto.ProcessingResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Service for asynchronous CV processing
 * 
 * Handles async processing of CV documents for better performance.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncProcessingService {

    private final CompleteProcessingService completeProcessingService;

    /**
     * Process CV asynchronously
     * 
     * @param requestId the processing request ID
     * @return CompletableFuture with processing result
     */
    @Async("taskExecutor")
    public CompletableFuture<ProcessingResponseDTO> processCVAsync(Long requestId) {
        log.info("Starting async CV processing for request ID: {}", requestId);

        try {
            ProcessingResponseDTO result = completeProcessingService.processCVCompletely(requestId);
            log.info("Async CV processing completed for request ID: {}", requestId);
            return CompletableFuture.completedFuture(result);

        } catch (Exception e) {
            log.error("Async CV processing failed for request ID {}: {}", requestId, e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Get async processing result
     * 
     * @param requestId the request ID
     * @return CompletableFuture with processing result
     */
    @Async("taskExecutor")
    public CompletableFuture<ProcessingResponseDTO> getProcessingResultAsync(Long requestId) {
        log.debug("Retrieving async processing result for request ID: {}", requestId);

        try {
            ProcessingResponseDTO result = completeProcessingService.getCompleteProcessingResult(requestId);
            return CompletableFuture.completedFuture(result);

        } catch (Exception e) {
            log.error("Failed to retrieve async processing result for request ID {}: {}", requestId, e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
}
