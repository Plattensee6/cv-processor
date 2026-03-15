package com.intuitech.cvprocessor.application.service;

import com.intuitech.cvprocessor.application.dto.FileUploadResponseDTO;
import com.intuitech.cvprocessor.domain.model.CVProcessingRequest;
import com.intuitech.cvprocessor.domain.model.ProcessingStatus;
import com.intuitech.cvprocessor.feature.cvprocessing.repository.CVProcessingRequestRepository;
import com.intuitech.cvprocessor.infrastructure.service.DocumentParsingService;
import com.intuitech.cvprocessor.infrastructure.service.FileValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    private final FileValidationService fileValidationService;
    private final DocumentParsingService documentParsingService;
    private final CVProcessingRequestRepository cvProcessingRequestRepository;

    /**
     * Upload and process a file
     * 
     * @param file the uploaded file
     * @return response with processing information
     * @throws FileUploadException if upload fails
     */
    @Transactional
    public FileUploadResponseDTO uploadFile(MultipartFile file) throws FileUploadException {
        log.info("Processing file upload: {}", file.getOriginalFilename());

        try {
            // Validate file
            fileValidationService.validateFile(file);

            // Parse document content
            String parsedText = documentParsingService.parseDocument(file);

            // Create processing request
            CVProcessingRequest request = createProcessingRequest(file, parsedText);

            // Save to database
            CVProcessingRequest savedRequest = cvProcessingRequestRepository.save(request);

            log.info("File uploaded successfully: {} (ID: {})", file.getOriginalFilename(), savedRequest.getId());

            return buildResponse(savedRequest);

        } catch (FileValidationService.FileValidationException e) {
            log.error("File validation failed: {}", e.getMessage());
            throw new FileUploadException("File validation failed: " + e.getMessage(), e);
        } catch (DocumentParsingService.DocumentParsingException e) {
            log.error("Document parsing failed: {}", e.getMessage());
            throw new FileUploadException("Document parsing failed: " + e.getMessage(), e);
        } catch (IOException e) {
            log.error("IO error during file upload: {}", e.getMessage());
            throw new FileUploadException("Failed to read file content", e);
        } catch (Exception e) {
            log.error("Unexpected error during file upload: {}", e.getMessage(), e);
            throw new FileUploadException("Unexpected error during file upload", e);
        }
    }

    private CVProcessingRequest createProcessingRequest(MultipartFile file, String parsedText) throws IOException {
        return CVProcessingRequest.builder()
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .originalContent(new String(file.getBytes()))
                .parsedText(parsedText)
                .status(ProcessingStatus.UPLOADED)
                .build();
    }

    /**
     * Build response DTO from processing request
     */
    private FileUploadResponseDTO buildResponse(CVProcessingRequest request) {
        return FileUploadResponseDTO.builder()
                .requestId(request.getId())
                .fileName(request.getFileName())
                .contentType(request.getContentType())
                .fileSize(request.getFileSize())
                .status(request.getStatus())
                .message("File uploaded and parsed successfully")
                .uploadedAt(request.getCreatedAt())
                .parsedText(request.getParsedText())
                .build();
    }

    public FileUploadResponseDTO getProcessingRequest(Long requestId) throws FileUploadException {
        log.debug("Retrieving processing request: {}", requestId);

        CVProcessingRequest request = cvProcessingRequestRepository.findById(requestId)
                .orElseThrow(() -> new FileUploadException("Processing request not found: " + requestId));

        return buildResponse(request);
    }

    public static class FileUploadException extends Exception {
        public FileUploadException(String message) {
            super(message);
        }

        public FileUploadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
