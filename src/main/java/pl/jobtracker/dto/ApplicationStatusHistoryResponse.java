package pl.jobtracker.dto;

import java.time.OffsetDateTime;

public record ApplicationStatusHistoryResponse(
        Long id,
        String status,
        OffsetDateTime changedAt,
        String comment
) {
}
