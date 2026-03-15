#!/usr/bin/env bash
# Run from anywhere in repo: bash scripts/git-commit-features.sh
# Creates separate commits for API versioning, ProcessingStatus, api DTOs, comments, tests, auth (backend+frontend), frontend public, frontend admin.

set -e
REPO_ROOT="$(git rev-parse --show-toplevel)"
git_cmd() { git -C "$REPO_ROOT" "$@"; }

echo "=== 1. API versioning ==="
git_cmd add \
  src/main/java/com/intuitech/cvprocessor/presentation/controller/AuthController.java \
  src/main/java/com/intuitech/cvprocessor/presentation/controller/AdminSettingsController.java \
  src/main/java/com/intuitech/cvprocessor/presentation/controller/UserAdminController.java \
  src/main/java/com/intuitech/cvprocessor/presentation/controller/JobAdminController.java \
  src/main/java/com/intuitech/cvprocessor/presentation/controller/JobPublicController.java \
  src/main/java/com/intuitech/cvprocessor/presentation/controller/CVProcessingController.java \
  src/main/java/com/intuitech/cvprocessor/presentation/controller/CompleteProcessingController.java \
  src/main/java/com/intuitech/cvprocessor/presentation/controller/FileUploadController.java \
  src/main/java/com/intuitech/cvprocessor/infrastructure/controller/OllamaAdminController.java \
  src/main/java/com/intuitech/cvprocessor/infrastructure/controller/OllamaHealthController.java \
  src/main/java/com/intuitech/cvprocessor/presentation/controller/MetricsController.java \
  src/main/java/com/intuitech/cvprocessor/presentation/controller/HealthController.java
git_cmd commit -m "feat(api): add /api/v1 to all REST controller base paths" || true

echo "=== 2. ProcessingStatus top-level enum ==="
git_cmd add \
  src/main/java/com/intuitech/cvprocessor/domain/model/ProcessingStatus.java \
  src/main/java/com/intuitech/cvprocessor/domain/model/CVProcessingRequest.java \
  src/main/java/com/intuitech/cvprocessor/feature/cvprocessing/CVProcessingService.java \
  src/main/java/com/intuitech/cvprocessor/feature/cvprocessing/repository/CVProcessingRequestRepository.java \
  src/main/java/com/intuitech/cvprocessor/application/service/FileUploadService.java \
  src/main/java/com/intuitech/cvprocessor/application/dto/ProcessingResponseDTO.java \
  src/main/java/com/intuitech/cvprocessor/application/dto/FileUploadResponseDTO.java \
  src/test/java/com/intuitech/cvprocessor/unit/repository/CVProcessingRequestRepositoryTest.java \
  src/test/java/com/intuitech/cvprocessor/integration/CVProcessingIntegrationTest.java \
  src/test/java/com/intuitech/cvprocessor/unit/service/FileUploadServiceTest.java \
  src/test/java/com/intuitech/cvprocessor/unit/service/CVProcessingServiceTest.java \
  src/test/java/com/intuitech/cvprocessor/integration/api/FileUploadControllerIntegrationTest.java \
  src/test/java/com/intuitech/cvprocessor/util/TestDataBuilder.java \
  src/test/java/com/intuitech/cvprocessor/util/MockDataFactory.java \
  src/test/java/com/intuitech/cvprocessor/presentation/controller/FileUploadControllerTest.java
git_cmd commit -m "refactor(domain): extract ProcessingStatus to top-level enum" || true

echo "=== 3. Controller inner DTOs -> api package + mappers ==="
git_cmd add \
  src/main/java/com/intuitech/cvprocessor/presentation/controller/AdminSettingsController.java \
  src/main/java/com/intuitech/cvprocessor/presentation/controller/JobPublicController.java \
  src/main/java/com/intuitech/cvprocessor/presentation/controller/UserAdminController.java \
  src/main/java/com/intuitech/cvprocessor/presentation/mapper/JobPostingApiMapper.java \
  src/main/java/com/intuitech/cvprocessor/presentation/mapper/UserApiMapper.java
git_cmd commit -m "refactor(api): use api package DTOs in controllers, add UserApiMapper and JobSummaryDto" || true

echo "=== 4. Remove redundant comments ==="
git_cmd add \
  src/main/java/com/intuitech/cvprocessor/feature/cvprocessing/CVProcessingService.java \
  src/main/java/com/intuitech/cvprocessor/feature/auth/AuthService.java \
  src/main/java/com/intuitech/cvprocessor/feature/cvprocessing/repository/CVProcessingRequestRepository.java \
  src/main/java/com/intuitech/cvprocessor/application/service/FileUploadService.java \
  src/main/java/com/intuitech/cvprocessor/domain/model/ExtractedFields.java \
  src/main/java/com/intuitech/cvprocessor/domain/model/ValidationResult.java \
  src/main/java/com/intuitech/cvprocessor/application/dto/ProcessingResponseDTO.java \
  src/main/java/com/intuitech/cvprocessor/application/dto/FileUploadResponseDTO.java
git_cmd commit -m "chore: remove redundant comments per project standards" || true

echo "=== 5. Tests: wildcard imports and class name ==="
git_cmd add \
  src/test/java/com/intuitech/cvprocessor/util/TestDataBuilder.java \
  src/test/java/com/intuitech/cvprocessor/util/MockDataFactory.java \
  src/test/java/com/intuitech/cvprocessor/integration/CVProcessingIntegrationTest.java
git_cmd commit -m "style(tests): replace wildcard imports and fix CVProcessingIntegrationTest class name" || true

echo "=== 6. Auth (backend + frontend) ==="
git_cmd add \
  src/main/java/com/intuitech/cvprocessor/domain/auth/ \
  src/main/java/com/intuitech/cvprocessor/feature/auth/ \
  src/main/java/com/intuitech/cvprocessor/presentation/controller/AuthController.java \
  src/main/java/com/intuitech/cvprocessor/presentation/controller/UserAdminController.java \
  src/main/java/com/intuitech/cvprocessor/techcore/security/ \
  src/main/java/com/intuitech/cvprocessor/techcore/config/JwtConfigProperties.java \
  src/main/java/com/intuitech/cvprocessor/techcore/config/PasswordEncoderConfig.java \
  src/main/java/com/intuitech/cvprocessor/infrastructure/config/SecurityConfig.java \
  src/main/java/com/intuitech/cvprocessor/presentation/mapper/UserApiMapper.java \
  src/main/resources/intuitech/cvprocessor/api/auth/ \
  web/src/app/pages/auth/ \
  web/src/app/context/AuthContext.tsx \
  web/src/app/api/auth.ts \
  web/src/app/api/client.ts \
  web/src/app/lib/jwt.ts
git_cmd commit -m "feat(auth): JWT auth backend and frontend (login, register, password reset, user admin)" || true

echo "=== 7. Frontend – public pages ==="
git_cmd add \
  web/src/app/pages/public/ \
  web/src/app/layouts/PublicLayout.tsx \
  web/src/app/App.tsx \
  web/src/main.tsx \
  web/src/app/routes.ts \
  web/src/styles/ \
  web/index.html \
  web/vite.config.ts \
  web/package.json \
  web/package-lock.json \
  web/Dockerfile \
  web/.dockerignore \
  web/postcss.config.mjs \
  web/src/app/components/ \
  web/src/app/data/
git_cmd commit -m "feat(web): public pages (Home, Careers, Apply), layout and app shell" || true

echo "=== 8. Frontend – admin pages ==="
git_cmd add \
  web/src/app/pages/admin/ \
  web/src/app/layouts/AdminLayout.tsx \
  web/src/app/components/ProtectedAdminLayout.tsx
git_cmd commit -m "feat(web): admin layout, protected routes, job ads, settings, dashboard" || true

echo "Done."
