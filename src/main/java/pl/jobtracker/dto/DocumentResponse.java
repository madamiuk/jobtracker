package pl.jobtracker.dto;

import java.time.OffsetDateTime;

public record DocumentResponse(
        Long id,
        String documentType,
        String fileName,
        String filePath,
        String version,
        OffsetDateTime sentAt,
        String sha256,
        String notes
) {}
